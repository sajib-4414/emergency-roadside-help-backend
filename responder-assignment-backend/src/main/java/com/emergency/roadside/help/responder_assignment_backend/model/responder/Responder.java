package com.emergency.roadside.help.responder_assignment_backend.model.responder;

import com.emergency.roadside.help.responder_assignment_backend.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(
        name = "responder",
        indexes = @Index(name = "idx_responder_user_id", columnList = "user_id")
)
@NoArgsConstructor
@AllArgsConstructor
public class Responder extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "phone_no", nullable = false)
    private String phoneNo;
}
