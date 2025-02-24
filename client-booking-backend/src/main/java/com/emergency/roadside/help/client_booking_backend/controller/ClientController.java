package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ProfileDTO;
import com.emergency.roadside.help.client_booking_backend.model.client.SignupDTO;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.client.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private ClientService clientService;

    @PostMapping("/signup")
    public Client signup(@Validated  @RequestBody SignupDTO signupDTO) {
        return clientService.signup(signupDTO);
    }

    @GetMapping("/profile")
    public ProfileDTO getProfile() {
        Long id = 1L;
        return clientService.getProfile(id);
    }

    @PostMapping("/vehicles")
    public Vehicle addVehicle(@Validated @RequestBody Vehicle vehicle) {
        Long userId = 1L;
        return clientService.addVehicleToClientProfile(userId, vehicle);
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public void deleteVehicle(@PathVariable Long vehicleId) {
        Long userId = 1L;
        clientService.deleteMyVehicle(userId, vehicleId);
    }


}
