package com.emergency.roadside.help.client_booking_backend.services.booking;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestDTO;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;
    private final ModelMapper modelMapper;
    private final VehicleService vehicleService;

    @Transactional
    public BookingRequest createBooking(BookingRequestDTO payload) {
        BookingRequest bookingRequest = modelMapper.map(payload, BookingRequest.class);

        if (payload.getVehicleId() != null) {
            Vehicle vehicle = vehicleService.getById(payload.getVehicleId());
            bookingRequest.setVehicle(vehicle);
        } else if (payload.getVehicle() != null) {
            Vehicle vehicle = modelMapper.map(payload.getVehicle(), Vehicle.class);//creates a new instance of the Vehicle class and copies all the matching properties from the payload.getVehicle()
            vehicle = vehicleService.addVehicle(vehicle);
            bookingRequest.setVehicle(vehicle);
        }

        bookingRequest = bookingRequestRepository.save(bookingRequest);
        return bookingRequest;
    }

    @Transactional
    public void deleteBooking(Long id) {
        bookingRequestRepository.deleteById(id);
    }

    @Transactional
    public BookingRequest updateBooking(Long id, BookingRequestDTO payload) {
        BookingRequest existingBookingRequest = bookingRequestRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("BookingRequest not found"));

        modelMapper.map(payload, existingBookingRequest);

        if (payload.getVehicleId() != null) {
            Vehicle vehicle = vehicleService.getById(payload.getVehicleId());
            existingBookingRequest.setVehicle(vehicle);
        } else if (payload.getVehicle() != null) {
            Vehicle vehicle = modelMapper.map(payload.getVehicle(), Vehicle.class);
            vehicle = vehicleService.addVehicle(vehicle);
            existingBookingRequest.setVehicle(vehicle);
        }

        existingBookingRequest = bookingRequestRepository.save(existingBookingRequest);
        return existingBookingRequest;
    }
}
