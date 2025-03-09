package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.RegisterClientBookingCommand;
import com.emergency.roadside.help.client_booking_backend.model.booking.*;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.booking.BookingRequestService;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.BadDataException;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.emergency.roadside.help.client_booking_backend.configs.auth.AuthHelper.getCurrentUser;

@RestController
@RequestMapping("/api/v1/bookings")
@AllArgsConstructor
public class BookingRequestController {
    private final BookingRequestService bookingRequestService;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;
    private final ModelMapper modelMapper;
    private CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<BookingRequest> createBooking(@Validated @RequestBody BookingRequestDTO payload) {

        BookingRequest createdBooking = bookingRequestService.createBooking(payload);
        return ResponseEntity.ok(createdBooking);
    }

    @PostMapping("/create-saga-booking")
    public ResponseEntity<?> createSagaBooking(@Validated @RequestBody BookingRequestDTO payload) {
        String uniqueBookingId = UUID.randomUUID().toString();
        User user = getCurrentUser();
        Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("client not found"));

        Vehicle vehicle;
        if(payload.getVehicleId() !=null)
            vehicle = vehicleRepository.findById(payload.getVehicleId()).orElseThrow(()->new ItemNotFoundException("vehcile not found"));
        else if(payload.getVehicle()!=null){
            Vehicle dbPayload = modelMapper.map(payload.getVehicle(), Vehicle.class);
            vehicle = vehicleService.addVehicle(dbPayload);
        }
        else{
            throw new BadDataException("vehicle data is needed");
        }
        try{
            RegisterClientBookingCommand command = RegisterClientBookingCommand.builder()
                    .bookingId(uniqueBookingId)
                    .clientId(client.getId())
                    .status(BookingStatus.QUEUED)
                    .dateCreated(LocalDateTime.now())
                    .vehicleId(vehicle.getId())
                    .address(payload.getAddress())
                    .description(payload.getDetailDescription())
                    .priority(payload.getPriority()==null? Priority.NEXT_BUSINESS_DAY: payload.getPriority()) //should default to next business day
                    .serviceType(payload.getServiceType())
                    .build();
            commandGateway.sendAndWait(command);
            return ResponseEntity.ok(command);
        } catch (Exception e) {
            System.out.println("exception happened"+e.getMessage());
            System.out.println(e.getStackTrace());
            throw new BadDataException("Client already has a booking in progress");
        }


    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingRequest>> getMyBookings() {
        List<BookingRequest> mybookings = bookingRequestService.getAllMyBookings();
        return ResponseEntity.ok(mybookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingRequestService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingRequest> updateBooking(@PathVariable Long id, @Validated @RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingRequest updatedBooking = bookingRequestService.updateBooking(id, bookingRequestDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingStatusResponse> getBookingDetails(@PathVariable Long id) throws IOException {
        BookingStatusResponse bookingStatus = bookingRequestService.getBookingByIdFromCacheOrDB(id);
        return ResponseEntity.ok(bookingStatus);
    }

}

