package com.emergency.roadside.help.responder_assignment_backend.configs.exceptions.customexceptions;

import lombok.Getter;


@Getter
public class UnprocessableEntityException extends RuntimeException{
    private String code;
    public UnprocessableEntityException(String message) {
        super(message);
        this.code = "invalid_data";
    }

    public UnprocessableEntityException(String code, String message) {
        super(message);
        this.code = code;
    }

}
