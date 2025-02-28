package com.emergency.roadside.help.responder_assignment_backend.configs.auth;


import com.emergency.roadside.help.responder_assignment_backend.external.ExternalUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    public static ExternalUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExternalUser user = (ExternalUser) authentication.getPrincipal();
        return user;
    }
}
