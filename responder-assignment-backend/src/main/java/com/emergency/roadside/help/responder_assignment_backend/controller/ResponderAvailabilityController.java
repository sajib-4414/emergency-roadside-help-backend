package com.emergency.roadside.help.responder_assignment_backend.controller;

import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderAvailability;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderAvailabilityDTO;
import com.emergency.roadside.help.responder_assignment_backend.services.ResponderAvailabilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/availabilities")
@AllArgsConstructor
public class ResponderAvailabilityController {

    private final ResponderAvailabilityService responderAvailabilityService;

    @PostMapping
    public ResponseEntity<ResponderAvailability> createResponderAvailability(
            @Validated @RequestBody ResponderAvailabilityDTO responderAvailabilityDTO) {

        // Call service to create availability
        ResponderAvailability createdAvailability = responderAvailabilityService
                .createOrUpdateResponderAvailability(responderAvailabilityDTO);

        // Return response with HTTP 201 (Created)
        return new ResponseEntity<>(createdAvailability, HttpStatus.CREATED);
    }

    @GetMapping("/my-availability")
    public ResponseEntity<ResponderAvailability> getMyAvailability() {

        // Call the service to get the availability
        Optional<ResponderAvailability> availability = responderAvailabilityService.getResponderAvailability();

        if (availability.isPresent()) {
            return ResponseEntity.ok(availability.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
