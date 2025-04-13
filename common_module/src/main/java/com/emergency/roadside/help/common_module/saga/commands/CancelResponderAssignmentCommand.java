package com.emergency.roadside.help.common_module.saga.commands;

import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
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
