package com.emergency.roadside.help.client_booking_backend.model.vehicle;


import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.BaseEntity;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "client_vehicles")
@Data
@Builder
public class ClientVehicle extends BaseEntity {

    //@ManyToOne with Client: This indicates that many ClientVehicle records can be associated with one Client
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    //@ManyToOne with Vehicle: This indicates that many ClientVehicle records can be associated with one Vehicle
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}