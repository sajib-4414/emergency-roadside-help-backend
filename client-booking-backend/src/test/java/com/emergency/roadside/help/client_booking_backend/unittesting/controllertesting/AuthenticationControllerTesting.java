package com.emergency.roadside.help.client_booking_backend.unittesting.controllertesting;

import com.emergency.roadside.help.client_booking_backend.controller.AuthenticationController;
import com.emergency.roadside.help.client_booking_backend.model.client.RegisterRequest;
import com.emergency.roadside.help.client_booking_backend.services.client.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTesting {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    public void registerUserOnlySuccess() throws Exception{
        RegisterRequest registerdto = new RegisterRequest( "test@gmail.com","Test","12345");


        when(authenticationService.registerUserOnly(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body("Success: User details successfully saved!"));

    }
}
