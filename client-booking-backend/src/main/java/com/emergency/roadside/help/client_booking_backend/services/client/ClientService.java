package com.emergency.roadside.help.client_booking_backend.services.client;


import com.emergency.roadside.help.client_booking_backend.model.client.*;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.emergency.roadside.help.client_booking_backend.configs.auth.AuthHelper.getCurrentUser;

@Service
@AllArgsConstructor
public class ClientService {


    private final ClientRepository clientRepository;
    private final VehicleService vehicleService;

    public ProfileDTO getProfile(Long userId) {

        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("Client not found"));
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(client.getName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setUsername(user.getUsername());

        profileDTO.setPhoneNo(client.getPhoneNo());
        profileDTO.setClientVehicles(client.getClientVehicles());
        return profileDTO;
    }

    @Transactional
    public Vehicle addVehicleToClientProfile( Vehicle vehicle) {
        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("Client not found"));
        vehicle.setId(null);
        vehicleService.addVehicle(vehicle);
        client.getClientVehicles().add(vehicle);
        clientRepository.save(client);
        return vehicle;
    }

    @Transactional
    public void deleteMyVehicle( Long vehicleId) {
        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("Client not found"));
        Vehicle vehicle = vehicleService.getById(vehicleId);
        client.getClientVehicles().remove(vehicle);
        clientRepository.save(client);
    }


}

