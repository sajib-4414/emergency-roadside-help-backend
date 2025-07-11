package com.emergency.roadside.help.responder_assignment_backend.configs.auth;


import com.emergency.roadside.help.common_module.commonexternal.CustomUserDetails;
import com.emergency.roadside.help.common_module.commonexternal.ExternalUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    public static ExternalUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) authentication.getPrincipal();
        ExternalUser externalUser = ud.getExternalUser();
        return externalUser;
    }
}
