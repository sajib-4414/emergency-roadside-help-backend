package com.emergency.roadside.help.responder_assignment_backend.model.assignment;

import com.emergency.roadside.help.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AssignmentStatusResponse {

    private String information;

    private String assignmentId;

    private AssignStatus assignStatus; // Enum: RESERVED, ASSIGNED, CANCELLED

    private String bookingId;

    private ServiceType serviceType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String assignmentNotes;

    public AssignmentStatusResponse(String assignmentId, AssignStatus assignStatus, String bookingId,
                                    ServiceType serviceType, LocalDateTime startTime, LocalDateTime endTime,
                                    String assignmentNotes) {
        this.assignmentId = assignmentId;
        this.assignStatus = assignStatus;
        this.bookingId = bookingId;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.assignmentNotes = assignmentNotes;
    }
}
