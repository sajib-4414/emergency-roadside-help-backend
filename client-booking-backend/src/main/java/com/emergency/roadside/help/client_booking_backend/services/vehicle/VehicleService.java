package com.emergency.roadside.help.client_booking_backend.services.vehicle;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public Vehicle getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("vehcile not found"));
        return vehicle;
    }

    public Vehicle updateVehicle(Long vehicleId, Vehicle payload) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        modelMapper.map(payload, vehicle);
        return vehicleRepository.save(vehicle);
    }
}
