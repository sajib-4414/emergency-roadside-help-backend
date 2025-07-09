package com.emergency.roadside.help.responder_assignment_backend.common_module.saga.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderNotFoundEvent {
    @TargetAggregateIdentifier
    private String bookingId;
}
