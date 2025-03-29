package com.emergency.roadside.help.client_booking_backend.cqrs.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadlineForFindResponderPayload {
    String bookingId;
}
