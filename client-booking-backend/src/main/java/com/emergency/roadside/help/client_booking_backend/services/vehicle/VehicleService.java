package com.emergency.roadside.help.client_booking_backend.services.vehicle;



import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;

import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
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
        payload.setId(null);

        modelMapper.typeMap(Vehicle.class, Vehicle.class).addMappings(mapper -> {
            mapper.skip(Vehicle::setId); // Skip the ID field
        });
        modelMapper.map(payload, vehicle);

        log.info("mapped vehicle is "+vehicle);
        return vehicleRepository.save(vehicle);
    }
}
