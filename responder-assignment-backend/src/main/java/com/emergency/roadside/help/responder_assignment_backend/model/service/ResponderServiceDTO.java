package com.emergency.roadside.help.responder_assignment_backend.model.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResponderServiceDTO {
    @NotNull
    private ServiceType serviceType;

    @NotNull
    private Double price;

    private String notes;
}
