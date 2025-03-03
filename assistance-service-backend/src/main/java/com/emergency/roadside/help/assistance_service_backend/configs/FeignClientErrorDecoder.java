package com.emergency.roadside.help.assistance_service_backend.configs;


import com.emergency.roadside.help.common_module.exceptions.customexceptions.*;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;

@ControllerAdvice
public class FeignClientErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            String errorMessage = "Error response came from rent service: " + response.status() + " " + response.reason();
            try {
                if (response.body() != null) {
                    errorMessage += ", Body: " + Util.toString(response.body().asReader());
                }
            } catch (IOException e) {
                errorMessage += ", Error reading body: " + e.getMessage();
            }
            System.out.println(errorMessage);
            switch (response.status()) {
                case 404:
                    return new ItemNotFoundException("Resource not found in client-service");
                case 400:
                    return new BadDataException("Invalid request to client-service");
                case 401:
                    return new UnAuthorizedError("Unauthorized access to client-service");
                case 403:
                    return new PermissionError("Forbidden access to client-service");
                case 422:
                    return new UnprocessableEntityException("Got unprocessable entity error from rent-microservice");
                default:
                    return new SystemException("Unknown Client error occurred in client-service");
            }
        }
        return defaultErrorDecoder.decode(methodKey, Response.class.cast(response));
    }
}
