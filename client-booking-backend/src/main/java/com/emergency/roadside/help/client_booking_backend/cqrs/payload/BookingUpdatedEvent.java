package com.emergency.roadside.help.client_booking_backend.cqrs.payload;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdatedEvent {
    private String bookingId;

    private BookingStatus status;

    private String responderName;

    private Long responderId;

    private ServiceType serviceType;

    private String address;

}
