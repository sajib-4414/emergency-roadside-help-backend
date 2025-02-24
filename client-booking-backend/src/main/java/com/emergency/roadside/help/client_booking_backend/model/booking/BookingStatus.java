package com.emergency.roadside.help.client_booking_backend.model.booking;

public enum BookingStatus {
    CREATED, RESPONDER_ASSIGNED, RESPONDER_ON_WAY, RESPONDER_REACHED,
    SERVICE_IN_PROGRESS, SERVICE_DONE_AWAITING_PAYMENT, COMPLETED, CANCELLED
}
