package com.emergency.roadside.help.client_booking_backend.services.client;


import com.emergency.roadside.help.client_booking_backend.configs.auth.JWTService;
import com.emergency.roadside.help.client_booking_backend.model.client.*;

import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ClientRepository clientRepository;

    public AuthResponse authenticate(LoginRequest request) {

        var token = new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        try{
            Authentication result = authenticationManager.authenticate(token);

        }catch (InternalAuthenticationServiceException ex){
            throw new ItemNotFoundException("User not found");
        }
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(()->new ItemNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return  AuthResponse.builder().token(jwtToken).user(user).build();


        // Continue with your logic here


    }

    //default register only allows to get a role of User.
    //later we will add more roles to a User, only via endpoint
    @Transactional
    public AuthResponse register(RegisterRequest request) {


        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        //also create a client
        Client client = Client.builder()
                .user(user)
                .build();


        userRepository.save(user);
        clientRepository.save(client);


        var jwtToken = jwtService.generateToken(user);
        return  AuthResponse.builder().token(jwtToken).user(user).build();

    }

    @Transactional
    public AuthResponse registerUserOnly(@Valid RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return  AuthResponse.builder().token(jwtToken).user(user).build();

    }
}
