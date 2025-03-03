package com.emergency.roadside.help.assistance_service_backend.configs.exceptions.customexceptions;

import lombok.Getter;


@Getter
public class BadDataException extends RuntimeException{
    private String code;
    public BadDataException(String message) {
        super(message);
        this.code = "invalid_data";
    }

    public BadDataException(String code, String message) {
        super(message);
        this.code = code;
    }

}