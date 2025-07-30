package com.emergency.roadside.help.client_booking_backend.model.vehicle;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleDTO {

    private String make;

    private String model;

    private String trim;

    private int year;

    private String plate;
}
