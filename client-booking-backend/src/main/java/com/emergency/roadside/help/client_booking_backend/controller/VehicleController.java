package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.client.ClientService;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/vehicles")
public class VehicleController {

    private VehicleService vehicleService;

    @PutMapping("/vehicles/{vehicleId}")
    public Vehicle updateMyVehicle(@PathVariable Long vehicleId, @RequestBody Vehicle updatedVehicle) {
        //TODO check if the vehicle is owned by the user
        return vehicleService.updateVehicle(vehicleId, updatedVehicle);
    }
}
