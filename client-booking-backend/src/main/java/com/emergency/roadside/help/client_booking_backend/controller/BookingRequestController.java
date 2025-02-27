package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestDTO;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatusResponse;
import com.emergency.roadside.help.client_booking_backend.services.booking.BookingRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.emergency.roadside.help.client_booking_backend.configs.auth.AuthHelper.getCurrentUser;

@RestController
@RequestMapping("/api/v1/bookings")
@AllArgsConstructor
public class BookingRequestController {
    private final BookingRequestService bookingRequestService;

    @PostMapping
    public ResponseEntity<BookingRequest> createBooking(@Validated @RequestBody BookingRequestDTO payload) {
        BookingRequest createdBooking = bookingRequestService.createBooking(payload);
        return ResponseEntity.ok(createdBooking);
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

