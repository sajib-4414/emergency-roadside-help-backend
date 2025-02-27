package com.emergency.roadside.help.client_booking_backend.configs.auth;

import com.emergency.roadside.help.client_booking_backend.model.client.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    public static User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }
}
