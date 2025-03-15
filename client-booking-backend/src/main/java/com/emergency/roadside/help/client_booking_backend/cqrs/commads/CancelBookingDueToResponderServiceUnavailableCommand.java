package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CancelBookingDueToResponderServiceUnavailableCommand {
    @TargetAggregateIdentifier
    private String bookingId;

}
