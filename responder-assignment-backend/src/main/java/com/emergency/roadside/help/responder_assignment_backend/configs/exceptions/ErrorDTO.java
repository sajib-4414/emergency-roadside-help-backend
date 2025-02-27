package com.emergency.roadside.help.client_booking_backend.configs.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private String code;
    private String message;
}
