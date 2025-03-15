package com.emergency.roadside.help.client_booking_backend.cqrs.events;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledDuetoRespUnavailable {
    private String bookingId;

    private BookingStatus status;
}
