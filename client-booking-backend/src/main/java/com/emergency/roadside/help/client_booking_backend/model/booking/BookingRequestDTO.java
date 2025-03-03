package com.emergency.roadside.help.client_booking_backend.model.booking;

import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleDTO;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {


    private LocalDateTime dateCreated;

    //client can either send a vehcile which was created before or a new vehcile
    private Long vehicleId;
    private VehicleDTO vehicle;
    private String detailDescription;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String address;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

}
