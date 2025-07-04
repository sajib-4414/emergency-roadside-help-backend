package com.emergency.roadside.help.client_booking_backend.common_module.saga.events;


import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.AssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderAssignedEvent {
    private String bookingId;
    private String assignmentId;
    private AssignStatus assignStatus;
    private Long responderId;
    private String responderName;
}
