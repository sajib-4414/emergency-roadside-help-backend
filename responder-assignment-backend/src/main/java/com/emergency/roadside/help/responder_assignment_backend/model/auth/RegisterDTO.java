package com.emergency.roadside.help.responder_assignment_backend.model.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {


    @NotEmpty(message = "email is required")
    private String email;

    @NotEmpty(message = "username is required")
    private String username;

    @NotEmpty(message = "password is required")
    private String password;


    private String name;

    private String companyName;

    @NotEmpty(message = "city is required")
    private String city;

    private String phoneNo;


}