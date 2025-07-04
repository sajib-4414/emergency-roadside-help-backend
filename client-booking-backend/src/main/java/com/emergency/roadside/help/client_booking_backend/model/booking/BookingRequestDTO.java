package com.emergency.roadside.help.client_booking_backend.model.booking;

import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.Priority;
import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleDTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {


    //client can either send a vehcile which was created before or a new vehcile
    private Long vehicleId;
    private VehicleDTO vehicle;
    private String detailDescription;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotEmpty
    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

}
