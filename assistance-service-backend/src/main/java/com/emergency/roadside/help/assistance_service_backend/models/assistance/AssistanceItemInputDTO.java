package com.emergency.roadside.help.assistance_service_backend.models.assistance;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssistanceItemInputDTO {

    @NotNull
    private String itemName;

    private Integer quantity;

    @NotNull
    private Double charge;

    private String description;
}
