package com.emergency.roadside.help.assistance_service_backend.configs.exceptions.customexceptions;

import lombok.Getter;

@Getter
public class ItemNotFoundException extends RuntimeException{
    private String code;
    public ItemNotFoundException(String message) {
        super(message);
        this.code = "Item not found";
    }

    public ItemNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

}
