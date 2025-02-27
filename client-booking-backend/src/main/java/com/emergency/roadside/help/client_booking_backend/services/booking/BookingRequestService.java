package com.emergency.roadside.help.client_booking_backend.services.booking;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestDTO;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.LocalDateTimeDeserializer2;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;

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
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

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
    public BookingRequest getBookingByIdFromCacheOrDB(Long id) throws IOException {
        Cache cache = cacheManager.getCache("booking");
        Cache.ValueWrapper valueWrapper = cache.get(id);

        if (valueWrapper != null) {

            Object cachedValue = valueWrapper.get();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer2());
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            BookingRequest bookingRequest = gson.fromJson(gson.toJson(cachedValue), BookingRequest.class);
            return bookingRequest;
//            BookingRequest cachedBookingRequest = (BookingRequest) valueWrapper.get();
//            BookingRequest pojo = objectMapper.convertValue(cachedValue, BookingRequest.class);
//            return pojo;
//            if (cachedValue instanceof LinkedHashMap) {
//                System.out.println("Cached value is not of type BookingRequest: " + cachedValue.getClass());
//                String cacheValueAsString = cachedValue != null ? cachedValue.toString() : "null";
//                System.out.println("Cached value for ID " + id + ": " + cacheValueAsString);
//
//
//                BookingRequest bookingRequest = objectMapper.convertValue(cachedValue, BookingRequest.class);
//                cache.put(id, bookingRequest);
//                return bookingRequest;
//            } else if (cachedValue instanceof BookingRequest) {
//                return (BookingRequest) cachedValue;
//            } else {
//                String json = (String) cachedValue;
//                byte[] json2 = (byte[]) cachedValue;
//                BookingRequest pojo = objectMapper.readValue(json2, BookingRequest.class);
//                System.out.println("Cached value is of unknown type: " + cachedValue.getClass());
//                return null;
//            }
//            if (cachedValue instanceof BookingRequest) {
//                BookingRequest bookingRequest = (BookingRequest) cachedValue;
//                // Proceed with bookingRequest
//                cache.put(id, bookingRequest);
//                return bookingRequest;
//            } else {
//                // Handle the case where the cached value is not of type BookingRequest
//                System.out.println("Cached value is not of type BookingRequest: " + cachedValue.getClass());
//                String cacheValueAsString = cachedValue != null ? cachedValue.toString() : "null";
//                System.out.println("Cached value for ID " + id + ": " + cacheValueAsString);
//                return null;
//            }

//            // Item exists in cache, access it to reset TTL
//            BookingRequest bookingRequest = (BookingRequest) valueWrapper.get();
//
//            // Reput the item to reset TTL
//            cache.put(id, bookingRequest);
//
//            return bookingRequest;
        } else {

            BookingRequest bookingRequest = bookingRequestRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("booking not found"));

          //  byte[] json = objectMapper.writeValueAsBytes(bookingRequest);
            // as accessed from DB we are caching it again
            cache.put(id, bookingRequest);
            return bookingRequest;
        }
    }
}
