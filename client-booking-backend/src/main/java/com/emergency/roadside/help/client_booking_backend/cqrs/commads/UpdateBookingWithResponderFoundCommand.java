package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingWithResponderFoundCommand {
    @TargetAggregateIdentifier
    private String bookingId;
    private BookingStatus bookingStatus;
    private String responderName;
    private Long responderId;
    private String address;
    private ServiceType serviceType;
}
