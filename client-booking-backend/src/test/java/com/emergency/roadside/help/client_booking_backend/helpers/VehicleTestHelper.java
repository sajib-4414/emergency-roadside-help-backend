package com.emergency.roadside.help.client_booking_backend.helpers;

import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VehicleTestHelper {

    private static final List<String> MAKES = Arrays.asList("Toyota", "Ford", "Honda", "BMW", "Chevrolet");
    private static final List<String> MODELS = Arrays.asList("Corolla", "F-150", "Civic", "X5", "Impala");
    private static final List<String> TRIMS = Arrays.asList("Base", "Sport", "Luxury");

    private static final Random random = new Random();


    public static Vehicle generateVehicle() {
        Vehicle vehicle = new Vehicle();

        vehicle.setMake(randomFromList(MAKES));
        vehicle.setModel(randomFromList(MODELS));
        vehicle.setTrim(randomFromList(TRIMS));
        vehicle.setYear(2000 + random.nextInt(26)); // 2000–2025
        vehicle.setPlate(generatePlate());

        return vehicle;
    }

    private static String randomFromList(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    private static String generatePlate() {
        StringBuilder plate = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            plate.append((char) ('A' + random.nextInt(26)));
        }
        plate.append("-");
        for (int i = 0; i < 4; i++) {
            plate.append(random.nextInt(10));
        }
        return plate.toString();
    }
}
