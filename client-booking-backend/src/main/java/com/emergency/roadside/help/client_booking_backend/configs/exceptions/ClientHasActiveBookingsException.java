package com.emergency.roadside.help.client_booking_backend.configs.exceptions;

import lombok.Getter;

@Getter
public class ClientHasActiveBookingsException extends RuntimeException{
    private String code;
    public ClientHasActiveBookingsException(String message) {
        super(message);
        this.code = "invalid_data";
    }

    public ClientHasActiveBookingsException(String code, String message) {
        super(message);
        this.code = code;
    }

}
