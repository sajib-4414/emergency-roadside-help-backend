package com.emergency.roadside.help.assistance_service_backend.models.payment;

import com.emergency.roadside.help.assistance_service_backend.models.assistance.Assistance;
import com.emergency.roadside.help.assistance_service_backend.models.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "assistance_id", nullable = false)
    private Assistance assistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "transaction_id")
    private String transactionId;

}
