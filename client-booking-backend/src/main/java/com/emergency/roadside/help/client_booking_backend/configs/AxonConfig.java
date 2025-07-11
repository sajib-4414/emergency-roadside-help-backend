package com.emergency.roadside.help.client_booking_backend.configs;

import com.emergency.roadside.help.client_booking_backend.cqrs.events.BookingEventHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.opentelemetry.api.OpenTelemetry;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.config.Configurer;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.axonframework.eventhandling.gateway.DefaultEventGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.axonframework.tracing.MultiSpanFactory;
import org.axonframework.tracing.SpanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.axonframework.tracing.opentelemetry.OpenTelemetrySpanFactory;

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
                                            CustomIntervalRetryScheduler retryScheduler
                                             ) {


    // Build and return the gateway
    return DefaultCommandGateway.builder()
            .commandBus(commandBus)
            .retryScheduler(retryScheduler)
            .build();
}

    @Bean
    public EventGateway eventGateway(EventBus eventBus
                                     ) {

        return DefaultEventGateway.builder()
                .eventBus(eventBus)
                .build();
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



//    @Bean
//    public SpanFactory spanFactory() {
//        return new SimpleLoggingSpanFactory();
//    }

//    @Bean
//    public SpanFactory spanFactory() {
//        return OpenTelemetrySpanFactory.builder().build();
//    }


    @Bean
    public SpanFactory spanFactory() {
        return new MultiSpanFactory(
                Arrays.asList(
                     //   new SimpleLoggingSpanFactory(),
                        OpenTelemetrySpanFactory.builder().build()
                )
        );
    }

//    public void configure(Configurer configurer) {
//        configurer.configureSpanFactory(configuration -> new MultiSpanFactory(
//                Arrays.asList(
//                        new SimpleLoggingSpanFactory(),
//                        OpenTelemetrySpanFactory.builder().build()
//                )
//        ));
//    }


    //TODO /*  Not sure if I need it, have to check*/
//    @Bean
//    public ConfigurerModule deadLetterConfigurerModule(RetryConstrainedEnqueuePolicy deadLetterEnqueuePolicy){
//        return configurer -> configurer.eventProcessing()
//                .registerDeadLetterQueue(
//                "bookings",
//                configuration -> JpaSequencedDeadLetterQueue.builder()
//                        .processingGroup("bookings")
//                        .serializer(configuration.eventSerializer())
//                        .transactionManager(configuration.getComponent(TransactionManager.class))
//                        .entityManagerProvider(configuration.getComponent(EntityManagerProvider.class))
//
//                        .build()
//        )
//                .registerDeadLetterPolicy("bookings",configuration -> deadLetterEnqueuePolicy);
//    }




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