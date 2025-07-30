package com.emergency.roadside.help.client_booking_backend.unittesting.servicetesting;

import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleDTO;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.emergency.roadside.help.client_booking_backend.helpers.VehicleTestHelper.generateVehicle;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTesting {
    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        // Create a UserDto object with test data before running the tests
        vehicle = generateVehicle();
    }

    @Test
    public void addVehicleTesting(){
        //it does not make much sense here
        //the whole point is telling vehicle repository that if you are asked if there is a vheicle with id 1,
        //return null. here we never aske the repostirotyy if any vehcile with id 1 exists though
        //it makes sense, if we are registering a user, that time we tell the mock repo that imagine
        //no such user exists, now i will do register, and dont say that user exists which will prevent registration
//        when(vehicleRepository.findById(1L)).thenReturn(null);

        vehicleService.addVehicle(vehicle);

        //verify that the save method of vehicleRepository was called exactly once, with an object of vehicle class
        verify(vehicleRepository,times(1)).save(any(Vehicle.class));



    }
}
