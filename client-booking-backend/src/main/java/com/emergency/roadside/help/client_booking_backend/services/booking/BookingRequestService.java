package com.emergency.roadside.help.client_booking_backend.services.booking;


import com.emergency.roadside.help.client_booking_backend.model.booking.*;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.LocalDateTimeDeserializer2;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.CacheService;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;

import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
    private final CacheService cacheService;

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
      //  log.info("booking request is "+bookingRequest);
        bookingRequest.setId(null);
        bookingRequest.setDateCreated(LocalDateTime.now());
        bookingRequest.setStatus(BookingStatus.CREATED);
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


//    @Cacheable(
//            value = "booking",
//            key = "#id",                      // Custom key expression
//            condition = "#id != null",        // Only cache if ID isn't null
//            unless = "#result == null"      // Don't cache null results
////            ,cacheManager = "customManager"
//    )
    public BookingStatusResponse getBookingByIdFromCacheOrDB(Long id) throws IOException {
        BookingStatusResponse bookingStatus = cacheService.getBookingFromCache(id)
                .orElseGet(()->{
                    BookingRequest bookingRequest2 = bookingRequestRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("booking not found"));
                    BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest2);
                    cacheService.putBookingToCache(statusResponse);
                    return statusResponse;
                });
        return bookingStatus;


    }
}
