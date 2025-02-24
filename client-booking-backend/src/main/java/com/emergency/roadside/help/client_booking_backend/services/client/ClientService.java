package com.emergency.roadside.help.client_booking_backend.services.client;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.ProfileDTO;
import com.emergency.roadside.help.client_booking_backend.model.client.SignupDTO;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ClientService {


    private final ClientRepository clientRepository;
    private final VehicleService vehicleService;



    public Client signup(SignupDTO payload) {
        Client client = Client.builder()
                .name(payload.getName())
                .username(payload.getUsername())
                .phoneNo(payload.getPhoneNo())
                .build();
        return clientRepository.save(client);
    }

    public ProfileDTO getProfile(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(()->new ItemNotFoundException("User not found"));
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(client.getName());
        profileDTO.setUsername(client.getUsername());
        profileDTO.setPhoneNo(client.getPhoneNo());
        profileDTO.setClientVehicles(client.getClientVehicles());
        return profileDTO;
    }

    @Transactional
    public Vehicle addVehicleToClientProfile(Long userId, Vehicle vehicle) {
        Client client = clientRepository.findById(userId).orElseThrow(()->new ItemNotFoundException("Client not found"));
        vehicleService.addVehicle(vehicle);
        client.getClientVehicles().add(vehicle);
        clientRepository.save(client);
        return vehicle;
    }

    @Transactional
    public void deleteMyVehicle(Long userId, Long vehicleId) {
        Client client = clientRepository.findById(userId).orElseThrow(()->new ItemNotFoundException("Client not found"));
        Vehicle vehicle = vehicleService.getById(vehicleId);
        client.getClientVehicles().remove(vehicle);
        clientRepository.save(client);
    }


}

