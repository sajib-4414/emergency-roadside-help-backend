package com.emergency.roadside.help.responder_assignment_backend.common_module.saga.commands;


import com.emergency.roadside.help.responder_assignment_backend.common_module.commonmodels.AssistanceStatus;
import com.emergency.roadside.help.responder_assignment_backend.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistanceCreatedEvent {

    private String assistanceId;
    private String bookingId;
    private ServiceType serviceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AssistanceStatus status;
    private Long responderId;
    private String responderName;
    private String location;
}
