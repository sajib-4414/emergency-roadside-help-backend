package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.RegisterClientBookingCommand;
import com.emergency.roadside.help.client_booking_backend.model.booking.*;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.CacheService;
import com.emergency.roadside.help.client_booking_backend.services.booking.BookingRequestService;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.BadDataException;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    private CacheService cacheService;

    @PostMapping
    public ResponseEntity<?> createBooking(@Validated @RequestBody BookingRequestDTO payload) {

//        BookingRequest createdBooking = bookingRequestService.createBooking(payload);
        return ResponseEntity.ok("this endpoint should not be used anymore, use the saga one");
//        return ResponseEntity.ok(createdBooking);
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
            CreateBookingCommand command = CreateBookingCommand
                    .builder()
                    .bookingId(uniqueBookingId)
                    .status(BookingStatus.QUEUED) //now booking is in queued state
                    .clientId(client.getId())
                    .dateCreated(LocalDateTime.now())
                    .description(payload.getDetailDescription())
                    .priority(payload.getPriority()==null? Priority.NEXT_BUSINESS_DAY: payload.getPriority())
                    .serviceType(payload.getServiceType())
                    .vehicleId(vehicle.getId())
                    .address(payload.getAddress())
                    .build();

//            RegisterClientBookingCommand command = RegisterClientBookingCommand.builder()
//                    .bookingId(uniqueBookingId)
//                    .clientId(client.getId())
//                    .status(BookingStatus.QUEUED)
//                    .dateCreated(LocalDateTime.now())
//                    .vehicleId(vehicle.getId())
//                    .address(payload.getAddress())
//                    .description(payload.getDetailDescription())
//                    .priority(payload.getPriority()==null? Priority.NEXT_BUSINESS_DAY: payload.getPriority()) //should default to next business day
//                    .serviceType(payload.getServiceType())
//                    .build();
            commandGateway.sendAndWait(command);

            BookingRequest dummyUnwrittenBookingRequest = getBookingRequest(client, command, vehicle);
            //solving the dual write problem, just writing to cache, and get booking status method will also return from cache always
            //when the booking will be created in db, we will update the cache also.
            BookingStatusResponse statusResponse = new BookingStatusResponse(dummyUnwrittenBookingRequest);
            cacheService.putBookingToCache(statusResponse);


            return ResponseEntity.ok(command);
        } catch (Exception e) {
            System.out.println("exception happened"+e.getMessage());
            System.out.println(e.getStackTrace());
            throw new BadDataException("Client already has a booking in progress");
        }


    }

    private static BookingRequest getBookingRequest(Client client, CreateBookingCommand command, Vehicle vehicle) {
        BookingRequest dummyUnwrittenBookingRequest = new BookingRequest();
        dummyUnwrittenBookingRequest.setRequestedBy(client);
        dummyUnwrittenBookingRequest.setBookingId(command.getBookingId());
        dummyUnwrittenBookingRequest.setStatus(command.getStatus());
        dummyUnwrittenBookingRequest.setDateCreated(command.getDateCreated());
        dummyUnwrittenBookingRequest.setVehicle(vehicle);
        dummyUnwrittenBookingRequest.setAddress(command.getAddress());
        dummyUnwrittenBookingRequest.setDescription(command.getDescription());
        dummyUnwrittenBookingRequest.setPriority(command.getPriority());
        dummyUnwrittenBookingRequest.setServiceType(command.getServiceType());
        return dummyUnwrittenBookingRequest;
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
        BookingStatusResponse bookingStatus = bookingRequestService.getBookingById(id);
        return ResponseEntity.ok(bookingStatus);
    }
    @GetMapping("/booking-id/{bookingId}")
    public ResponseEntity<BookingStatusResponse> getBookingDetailsByBookingId(@PathVariable String bookingId) throws IOException {
        BookingStatusResponse bookingStatus = bookingRequestService.getBookingByBookingIdFromCacheOrDB(bookingId);
        return ResponseEntity.ok(bookingStatus);
    }

}

