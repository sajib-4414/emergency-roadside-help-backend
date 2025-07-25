package com.emergency.roadside.help.responder_assignment_backend.cqrs.commands;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderAcceptedCommand {
    @TargetAggregateIdentifier
    private String assignmentId;
    private String bookingId;
    private Long responderId;
    private String responderName;
}
