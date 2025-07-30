package com.emergency.roadside.help.client_booking_backend.unittesting.repositorytesting;

import com.emergency.roadside.help.client_booking_backend.model.client.UserRepository;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.emergency.roadside.help.client_booking_backend.helpers.VehicleTestHelper.generateVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VehicleRepositoryTesting {



    @Autowired
    private VehicleRepository vehicleRepository;


    @Test
    @Transactional
    @Rollback
    public void testCreateVehicle(){
        Vehicle vh = generateVehicle();
        Vehicle savedVh = vehicleRepository.save(vh);

        assertNotNull(savedVh);
        assertEquals(savedVh.getMake(), vh.getMake());


        Optional<Vehicle> retrieved = vehicleRepository.findById(savedVh.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(retrieved.get().getMake(), vh.getMake());
    }

    @Test
    @Transactional
    @Rollback
    public void testFindingNonExistentVehicle(){

        Optional<Vehicle> retrieved = vehicleRepository.findById(10L);
        Assertions.assertTrue(retrieved.isEmpty());

    }


}
