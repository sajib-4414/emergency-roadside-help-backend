package com.emergency.roadside.help.client_booking_backend.model.client;

import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import lombok.Data;

import java.util.Set;

@Data
public class ProfileDTO {
    private String name;
    private String email;
    private String username;
    private String phoneNo;
    private Set<Vehicle> clientVehicles;

    // Getters and setters
}

