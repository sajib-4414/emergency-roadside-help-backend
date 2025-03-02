package com.emergency.roadside.help.responder_assignment_backend.model.assignment;

import com.emergency.roadside.help.responder_assignment_backend.model.BaseEntity;
import com.emergency.roadside.help.responder_assignment_backend.model.respondersupport.ServiceType;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "assignment",
        indexes = @Index(name = "idx_assignment_responder_id", columnList = "responder_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assignment extends BaseEntity {

    //have fileds like
    //responder, assignstatus:enum, reserved[yet not accepted it means], assigned[means accepted], cancelled[did not accept, or cancelled]
    //booking id,
    //servicetype
    //starttime -> localdatetime cannot be null
    //endtime ->localendtime, nullable
    //assignementNotes
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responder_id", nullable = false)
    private Responder responder;

    @Enumerated(EnumType.STRING)
    @Column(name = "assign_status", nullable = false)
    private AssignStatus assignStatus; // Enum: RESERVED, ASSIGNED, CANCELLED

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "assignment_notes", nullable = true)
    private String assignmentNotes;

}
