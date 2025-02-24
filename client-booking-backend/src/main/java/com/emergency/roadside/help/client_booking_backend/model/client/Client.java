package com.emergency.roadside.help.client_booking_backend.model.client;

import com.emergency.roadside.help.client_booking_backend.model.BaseEntity;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "client")
@Data
@Builder
public class Client extends BaseEntity {

    //by default they are nullable
    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "phone_no")
    private String phoneNo;

    @ManyToMany
    @JoinTable(
            name = "client_vehicles",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    Set<Vehicle> clientVehicles;
}
