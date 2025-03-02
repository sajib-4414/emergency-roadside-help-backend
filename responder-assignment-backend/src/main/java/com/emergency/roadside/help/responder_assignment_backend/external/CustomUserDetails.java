package com.emergency.roadside.help.responder_assignment_backend.external;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final ExternalUser externalUser;

    public CustomUserDetails(ExternalUser externalUser) {
        this.externalUser = externalUser;
    }

    public ExternalUser getExternalUser() {
        return externalUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assuming ExternalUser has a "role" field, map it to a Spring Security role
        return Collections.emptyList(); // N
    }

    @Override
    public String getPassword() {
        return null; // Password is not needed in this case since it's coming from an external service
    }

    @Override
    public String getUsername() {
        return externalUser.getUsername(); // Assuming ExternalUser has a getUsername() method
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
