package com.emergency.roadside.help.assistance_service_backend.models.assistance;

import com.emergency.roadside.help.common_module.commonmodels.BaseEntity;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "assistance")
@NoArgsConstructor
@AllArgsConstructor
//assistance cannot be created with API, it is event driven only
//cannot be deleted either
public class Assistance extends BaseEntity {

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssistanceStatus status;

    @Column(name = "responder_id", nullable = false)
    private Long responderId;

    @Column(name = "responder_name", nullable = false)
    private String responderName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;

}
