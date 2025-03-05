package com.emergency.roadside.help.client_booking_backend.configs.exceptions;

import lombok.Getter;

@Getter
public class BookingAlreadyExists extends RuntimeException{
    private String code;
    public BookingAlreadyExists(String message) {
        super(message);
        this.code = "booking_already_exists";
    }

    public BookingAlreadyExists(String code, String message) {
        super(message);
        this.code = code;
    }

}
