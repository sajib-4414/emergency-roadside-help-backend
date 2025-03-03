package com.emergency.roadside.help.assistance_service_backend.configs.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorHttpResponse {


    private List<ErrorDTO> errors;
}
