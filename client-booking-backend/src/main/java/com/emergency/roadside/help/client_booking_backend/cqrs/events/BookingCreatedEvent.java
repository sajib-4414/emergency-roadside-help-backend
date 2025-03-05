package com.emergency.roadside.help.client_booking_backend.cqrs.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    private String bookingId;

    private Long clientId;

    private String bookingStatus;

    private String serviceType;

    private Long vehicleId;

    private String description;

    private String priority;

    private LocalDateTime dateCreated;
}
