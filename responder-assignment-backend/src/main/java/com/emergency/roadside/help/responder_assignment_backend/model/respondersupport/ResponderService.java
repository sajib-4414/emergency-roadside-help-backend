package com.emergency.roadside.help.responder_assignment_backend.model.respondersupport;


import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.responder_assignment_backend.model.BaseEntity;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "responder_service",
        indexes = @Index(name = "idx_responder_service_responder_id", columnList = "responder_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderService extends BaseEntity {


    @JoinColumn(name = "responder_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Responder responder;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "notes", nullable = true)
    private String notes;
}
