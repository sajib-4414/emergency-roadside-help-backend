package com.emergency.roadside.help.client_booking_backend.cqrs.payload;

import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.Priority;
import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;

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

    private BookingStatus status;

    private ServiceType serviceType;

    private Long vehicleId;

    private String description;

    private Priority priority;

    private LocalDateTime dateCreated;

    private String address;
}
