package com.emergency.roadside.help.assistance_service_backend.models.assistance;

import com.emergency.roadside.help.assistance_service_backend.models.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "assistance_items")
public class AssistanceItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "assistance_id", nullable = false)
    private Assistance assistance;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "charge", nullable = false)
    private Double charge;

    @Column(name = "description")
    private String description;

}
