package com.emergency.roadside.help.responder_assignment_backend.controller;

import com.emergency.roadside.help.responder_assignment_backend.external.AuthResponse;
import com.emergency.roadside.help.responder_assignment_backend.external.RegisterRequest;
import com.emergency.roadside.help.responder_assignment_backend.model.RegisterDTO;
import com.emergency.roadside.help.responder_assignment_backend.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDTO payload) {
        // Register user and create responder
        AuthResponse authResponse = authService.registerUserAndResponder(payload);
        return ResponseEntity.ok(authResponse);
    }
}
