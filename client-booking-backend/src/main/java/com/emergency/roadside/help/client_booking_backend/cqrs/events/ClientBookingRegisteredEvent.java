package com.emergency.roadside.help.client_booking_backend.cqrs.events;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientBookingRegisteredEvent {

    private Long clientId;
    private String bookingId;
    private ServiceType serviceType;
    private BookingStatus status;

    private Long vehicleId;

    private String description;

    private Priority priority;

    private LocalDateTime dateCreated;

    private String address;
}
