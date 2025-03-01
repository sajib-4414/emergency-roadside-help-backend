package com.emergency.roadside.help.responder_assignment_backend.services;

import com.emergency.roadside.help.responder_assignment_backend.external.AuthResponse;
import com.emergency.roadside.help.responder_assignment_backend.external.RegisterRequest;
import com.emergency.roadside.help.responder_assignment_backend.external.UserServiceClient;
import com.emergency.roadside.help.responder_assignment_backend.model.auth.RegisterDTO;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserServiceClient userServiceClient;
    private final ResponderRepository responderRepository;

    @Transactional
    public AuthResponse registerUserAndResponder(RegisterDTO payload) {
        // Step 1: Create the user via Feign client
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(payload.getEmail());
        registerRequest.setPassword(payload.getPassword());
        registerRequest.setUsername(payload.getUsername());
        AuthResponse authResponse = userServiceClient.registerDriverUser(registerRequest);

        // Step 2: Create the responder and associate it with the created user
        Responder responder = new Responder();
        responder.setName(payload.getName());
        responder.setCompanyName(payload.getCompanyName());
        responder.setUserId(authResponse.getUser().getId());  // Link userId from external service
        responder.setPhoneNo(payload.getPhoneNo());
        responder.setCity(payload.getCity());

        responderRepository.save(responder);  // Save the responder in the database

        // Step 3: Return the AuthResponse with token and user data

        return authResponse;
    }
}
