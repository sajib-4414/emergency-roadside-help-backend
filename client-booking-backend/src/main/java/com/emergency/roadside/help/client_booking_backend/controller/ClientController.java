package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ProfileDTO;
import com.emergency.roadside.help.client_booking_backend.model.client.SignupDTO;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.client.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.emergency.roadside.help.client_booking_backend.configs.auth.AuthHelper.getCurrentUser;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private ClientService clientService;


    @GetMapping("/profile")
    public ProfileDTO getProfile() {

        return clientService.getProfile(getCurrentUser().getId());
    }

    @PostMapping("/vehicles")
    public Vehicle addVehicle(@Validated @RequestBody Vehicle vehicle) {
        return clientService.addVehicleToClientProfile(vehicle);
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public void deleteVehicle(@PathVariable Long vehicleId) {
        clientService.deleteMyVehicle( vehicleId);
    }


}
