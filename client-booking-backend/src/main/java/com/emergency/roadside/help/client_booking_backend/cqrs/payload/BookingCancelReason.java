package com.emergency.roadside.help.client_booking_backend.cqrs.payload;

public enum BookingCancelReason {
    ERROR_ON_BOOKING_SERVICE,RESPONDER_DID_NOT_ACCEPT, NO_RESPONDER_FOUND, RESPONDER_SERVICE_UNAVAILABLE
}
