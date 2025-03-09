package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingCommand {

    @TargetAggregateIdentifier
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
