package com.emergency.roadside.help.client_booking_backend.unittesting.controllertesting;

import com.emergency.roadside.help.client_booking_backend.configs.GlobalSecurityConfiguration;
import com.emergency.roadside.help.client_booking_backend.configs.auth.JWTService;
import com.emergency.roadside.help.client_booking_backend.controller.AuthenticationController;
import com.emergency.roadside.help.client_booking_backend.model.client.AuthResponse;
import com.emergency.roadside.help.client_booking_backend.model.client.RegisterRequest;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.services.client.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebMvcTest(AuthenticationController.class)
@Import(GlobalSecurityConfiguration.class) //we need to import spring security config class of ours,
// as our applicaiton have spring security
public class AuthenticationControllerTesting {

    @Autowired
    private MockMvc mockMvc;


    //jwt service also needs to be mocked, otherwise mocked AuthenticationService will fail.
    // JWTService is a dependency in AuthenticationService
    @MockitoBean
    private JWTService jwtService;
    @MockitoBean
    private AuthenticationService authenticationService;

    //this is needed as we have imported the global security conig, there we are needing this.
    @MockitoBean
    private AuthenticationProvider authenticationProvider;



    @Autowired
    private ObjectMapper objectMapper;


    @Test
//    @WithAnonymousUser
    public void registerUserOnlySuccess() throws Exception{
        String mockemail = "test@gmail.com";
        String mockusername = "Test";
        String mockAuthToken = "mock-jwt-token";
        String password = "12345";
        RegisterRequest registerDto = new RegisterRequest( mockemail,mockusername,password);
        AuthResponse mockAuthResponse = AuthResponse
                .builder()
                .token(mockAuthToken)
                .user(User
                        .builder()
                        .username(mockusername)
                        .email(mockemail)
                        .build()

                )
                .build();

        when(authenticationService.registerUserOnly(any()))
                .thenReturn(mockAuthResponse);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/register-user-only")

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(mockAuthToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value(mockusername))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value(mockusername))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value(mockemail));




    }
}
