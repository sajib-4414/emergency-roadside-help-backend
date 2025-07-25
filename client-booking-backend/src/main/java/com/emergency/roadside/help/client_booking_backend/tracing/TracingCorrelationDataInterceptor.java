package com.emergency.roadside.help.client_booking_backend.tracing;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class TracingCorrelationDataInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    @Autowired
    private ObservationRegistry observationRegistry;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, message) -> {
            Observation current = observationRegistry.getCurrentObservation();
            if (current != null) {
                return message.andMetaData(Map.of(
                        "traceId", current.getContext().getName()
                ));
            }
            return message;
        };
    }
}
