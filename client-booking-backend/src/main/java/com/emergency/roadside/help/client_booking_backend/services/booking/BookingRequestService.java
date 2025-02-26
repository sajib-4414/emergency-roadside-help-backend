package com.emergency.roadside.help.client_booking_backend.services.booking;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestDTO;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.emergency.roadside.help.client_booking_backend.configs.auth.AuthHelper.getCurrentUser;

@Slf4j
@Service
@AllArgsConstructor
public class BookingRequestService {
    private final BookingRequestRepository bookingRequestRepository;
    private final ModelMapper modelMapper;
    private final VehicleService vehicleService;
    private final ClientRepository clientRepository;

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
        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("client not found"));
        bookingRequest.setRequestedBy(client);
        log.info("booking request is "+bookingRequest);
        bookingRequest = bookingRequestRepository.save(bookingRequest);
        return bookingRequest;
    }

    @Transactional
    public void deleteBooking(Long id) {
        //not deleting the vehciles right now, but we could dlete in future maybe
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

    public List<BookingRequest> getAllMyBookings() {
        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("client not found"));
        return bookingRequestRepository.findAllByRequestedBy_Id(client.getId());
    }
}
