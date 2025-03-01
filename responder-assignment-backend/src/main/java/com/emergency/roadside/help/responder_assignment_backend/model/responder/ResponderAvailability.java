package com.emergency.roadside.help.responder_assignment_backend.model.responder;

import com.emergency.roadside.help.responder_assignment_backend.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Entity
@Table(
        name = "responder_availability",
        indexes = @Index(name = "idx_responder_availability_responder_id", columnList = "responder_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponderAvailability extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responder_id", nullable = false)
    private Responder responder;

    @Column(name = "day_of_weeks", nullable = false)
    private String dayOfWeeks; // Can store MON, TUE, etc. Multiple or one. API will check this. All 3 char uppercase.

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    // Getters and setters
}
