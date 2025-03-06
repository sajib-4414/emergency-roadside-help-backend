package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisterClientBookingCommand {

    private String bookingId;

    @TargetAggregateIdentifier
    private Long clientId;

    private String bookingStatus;

    private String serviceType;

    private Long vehicleId;

    private String description;

    private String priority;

    private LocalDateTime dateCreated;
}
