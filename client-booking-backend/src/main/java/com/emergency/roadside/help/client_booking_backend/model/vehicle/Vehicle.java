package com.emergency.roadside.help.client_booking_backend.model.vehicle;




import com.emergency.roadside.help.common_module.commonmodels.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "trim")
    private String trim;

    @Column(name = "vehicle_year")
    private int year;

    @Column(name = "plate", nullable = false)
    private String plate;
}
