package com.emergency.roadside.help.common_module.saga.events;

import com.emergency.roadside.help.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderReservedAndNotifiedEvent {
    private String assignmentId;
    private String bookingId;
    private ServiceType serviceType;
    //start time is the time when someone is matched, responder service will do it
    //end time is also fillable by responder service
    private String description;//this will be copied to assignment notes
    private Priority priority;
    private AssignStatus assignStatus;
    private LocalDateTime startTime;//the time assigned
    private LocalDateTime endTime;
    private Long responderId;
}
