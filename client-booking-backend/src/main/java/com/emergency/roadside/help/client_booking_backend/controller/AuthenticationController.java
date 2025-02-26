package com.emergency.roadside.help.client_booking_backend.controller;

import com.emergency.roadside.help.client_booking_backend.model.client.AuthResponse;
import com.emergency.roadside.help.client_booking_backend.model.client.LoginRequest;
import com.emergency.roadside.help.client_booking_backend.model.client.RegisterRequest;
import com.emergency.roadside.help.client_booking_backend.services.client.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth" )
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping(value = "/authenticate", produces = "application/json")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }



}
