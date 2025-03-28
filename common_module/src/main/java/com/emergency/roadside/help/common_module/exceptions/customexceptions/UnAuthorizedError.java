package com.emergency.roadside.help.common_module.exceptions.customexceptions;

import lombok.Getter;


@Getter
public class UnAuthorizedError extends RuntimeException{
    private String code;
    public UnAuthorizedError(String message) {
        super(message);
        this.code = "invalid_data";
    }

    public UnAuthorizedError(String code, String message) {
        super(message);
        this.code = code;
    }

}