package com.emergency.roadside.help.client_booking_backend.common_module.saga.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelResponderAssignmentCommand {
    @TargetAggregateIdentifier
    private String assignmentId;

    private String bookingId;
}
