package com.emergency.roadside.help.responder_assignment_backend.model.responder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ResponderAvailabilityDTO {
    @NotNull
    @NotEmpty
    private List<String> dayOfWeeks;  // List of days (e.g., MON, TUE, etc.)

    @NotNull
    private String startTime;  // Start time in HH:mm format

    @NotNull
    private String endTime;    // End time in HH:mm format
}
