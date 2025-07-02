package com.emergency.roadside.help.client_booking_backend.configs;

import com.emergency.roadside.help.client_booking_backend.cqrs.events.BookingEventHandler;
import com.emergency.roadside.help.client_booking_backend.tracing.TracingCorrelationDataInterceptor;
import com.emergency.roadside.help.client_booking_backend.tracing.TracingEventDispatchInterceptor;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.ResourceAttributes;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.axonframework.eventhandling.gateway.DefaultEventGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class AxonConfig {
    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        xStream.addPermission(AnyTypePermission.ANY);
        return xStream;
    }

    @Primary
    @Bean
    public Serializer serializer(XStream xStream) {
        return XStreamSerializer.builder()
                .xStream(xStream)
                .build();
    }

//    @Bean
//    public IntervalRetryScheduler retryScheduler() {
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        return IntervalRetryScheduler.builder()
//                .retryExecutor(scheduler)
//                .retryInterval(5) // Retry every 5 seconds
//                .maxRetryCount(3) // Retry up to 3 times
//                .build();
//    }

//    @Bean
//    public DefaultCommandGateway commandGateway(CommandBus commandBus, CustomIntervalRetryScheduler retryScheduler) {
//        return DefaultCommandGateway.builder()
//                .commandBus(commandBus)
//                .retryScheduler(retryScheduler)
//                .build();
//    }

    //for axon tracing
@Bean
public DefaultCommandGateway commandGateway(CommandBus commandBus,
                                            CustomIntervalRetryScheduler retryScheduler,
                                            TracingCorrelationDataInterceptor tracingInterceptor) {
    // Register the tracing interceptor
    commandBus.registerDispatchInterceptor(tracingInterceptor);

    // Build and return the gateway
    return DefaultCommandGateway.builder()
            .commandBus(commandBus)
            .retryScheduler(retryScheduler)
            .build();
}

    @Bean
    public EventGateway eventGateway(EventBus eventBus,
                                     TracingEventDispatchInterceptor tracingInterceptor) {
        eventBus.registerDispatchInterceptor(tracingInterceptor);
        return DefaultEventGateway.builder()
                .eventBus(eventBus)
                .build();
    }


    @Bean
    public ResourceProvider tracingResourceProvider() {
        return new ResourceProvider() {
            @Override
            public Resource createResource(ConfigProperties config) {
                return Resource.create(
                        Attributes.of(ResourceAttributes.SERVICE_NAME, "client-jaeger-exporter")
                );
            }

            @Override
            public int order() {
                return 0;
            }
        };
    }

    @Bean
    public ConfigurerModule deadLetterConfigurerModule(RetryConstrainedEnqueuePolicy deadLetterEnqueuePolicy){
        return configurer -> configurer.eventProcessing()
                .registerDeadLetterQueue(
                "bookings",
                configuration -> JpaSequencedDeadLetterQueue.builder()
                        .processingGroup("bookings")
                        .serializer(configuration.eventSerializer())
                        .transactionManager(configuration.getComponent(TransactionManager.class))
                        .entityManagerProvider(configuration.getComponent(EntityManagerProvider.class))

                        .build()
        )
                .registerDeadLetterPolicy("bookings",configuration -> deadLetterEnqueuePolicy);
    }




    @Autowired
    private TransactionManager transactionManager;

    @Bean
    public DeadlineManager deadlineManager(final org.axonframework.config.Configuration configuration) {
        return SimpleDeadlineManager.builder()
                .scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
                .transactionManager(transactionManager)
                .build();
    }

}