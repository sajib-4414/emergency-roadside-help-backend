package com.emergency.roadside.help.responder_assignment_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class DummyAuthController {

    @GetMapping()
    public String checkAuthentication() {
        return "It works";
    }
}
