package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import com.emergency.roadside.help.client_booking_backend.cqrs.payload.BookingCancelReason;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CancelBookingCommand {
    @TargetAggregateIdentifier
    private String bookingId;

    private BookingCancelReason cancelReason;
}
