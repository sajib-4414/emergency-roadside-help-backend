package com.emergency.roadside.help.client_booking_backend.cqrs.query;

public class BookingStatusQuery {
    private final String bookingId;

    public BookingStatusQuery(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }
}
