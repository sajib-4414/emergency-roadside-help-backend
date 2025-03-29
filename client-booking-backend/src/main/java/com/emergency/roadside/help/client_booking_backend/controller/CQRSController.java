package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.services.cqrs.CQRSMainService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.SequencedDeadLetterProcessor;
import org.axonframework.messaging.deadletter.SequencedDeadLetterQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cqrs")
@AllArgsConstructor
public class CQRSController {

    CQRSMainService cqrsMainService;

    @GetMapping("/retry")
    public ResponseEntity<String> retryAllDeadLetters() throws InterruptedException {
       // retryAnySequence("bookings");
        cqrsMainService.retryAllBookingDeadLetters();
        return ResponseEntity.ok("started...");
    }

    @GetMapping("/get-all-booking-events/{bookingId}")
    public ResponseEntity<?> getAllEventsByBookingId(@PathVariable String bookingId) throws InterruptedException {
        // retryAnySequence("bookings");
        List<DomainEventMessage<?>> eventsList = cqrsMainService.getAllEventsForBooking(bookingId);
        return ResponseEntity.ok(eventsList);
    }
}
