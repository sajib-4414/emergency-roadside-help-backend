package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.cqrs.query.BookingStatusQuery;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatusResponse;
import com.emergency.roadside.help.client_booking_backend.services.cqrs.CQRSMainService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.SequencedDeadLetterProcessor;
import org.axonframework.messaging.deadletter.SequencedDeadLetterQueue;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cqrs")
@AllArgsConstructor
public class CQRSController {

    CQRSMainService cqrsMainService;
    QueryGateway queryGateway;

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

    @GetMapping(value = "/booking-subscription-query/{bookingId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookingStatusResponse> streamBookingUpdates(@PathVariable("bookingId") String bookingId) {
        SubscriptionQueryResult<BookingStatusResponse, BookingStatusResponse> subscriptionQuery =
                queryGateway.subscriptionQuery(
                        new BookingStatusQuery(bookingId),
                        ResponseTypes.instanceOf(BookingStatusResponse.class),
                        ResponseTypes.instanceOf(BookingStatusResponse.class)
                );

        return subscriptionQuery.initialResult().concatWith(subscriptionQuery.updates());
    }
}
