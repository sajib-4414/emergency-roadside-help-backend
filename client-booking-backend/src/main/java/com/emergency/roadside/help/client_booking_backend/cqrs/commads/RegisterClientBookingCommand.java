package com.emergency.roadside.help.client_booking_backend.cqrs.commads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterClientBookingCommand {
    private Long clientId;
    private String bookingId;
}
