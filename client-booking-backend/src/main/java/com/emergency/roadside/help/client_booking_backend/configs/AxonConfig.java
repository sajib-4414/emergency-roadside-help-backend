package com.emergency.roadside.help.client_booking_backend.configs;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
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

    @Bean
    public DefaultCommandGateway commandGateway(CommandBus commandBus, CustomIntervalRetryScheduler retryScheduler) {
        return DefaultCommandGateway.builder()
                .commandBus(commandBus)
                .retryScheduler(retryScheduler)
                .build();
    }
}