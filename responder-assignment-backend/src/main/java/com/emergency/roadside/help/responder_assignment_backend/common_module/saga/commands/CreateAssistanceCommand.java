package com.emergency.roadside.help.responder_assignment_backend.common_module.saga.commands;


import com.emergency.roadside.help.responder_assignment_backend.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssistanceCommand {
    @TargetAggregateIdentifier
    private String bookingId;

    private String responderName;

    private Long responderId;

    private String address;

    private ServiceType serviceType;

}
