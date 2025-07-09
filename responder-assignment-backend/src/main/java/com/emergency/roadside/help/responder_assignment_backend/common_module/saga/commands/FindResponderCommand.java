package com.emergency.roadside.help.responder_assignment_backend.common_module.saga.commands;


import com.emergency.roadside.help.responder_assignment_backend.common_module.commonmodels.Priority;
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
public class FindResponderCommand {
    @TargetAggregateIdentifier
    private String bookingId;
    private ServiceType serviceType;
    //start time is the time when someone is matched, responder service will do it
    //end time is also fillable by responder service
    private String description;//this will be copied to assignment notes
    private Priority priority;
}
