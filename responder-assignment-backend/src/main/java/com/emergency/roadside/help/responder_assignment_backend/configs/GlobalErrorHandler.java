package com.emergency.roadside.help.responder_assignment_backend.configs;


import com.emergency.roadside.help.client_booking_backend.configs.exceptions.ErrorDTO;
import com.emergency.roadside.help.client_booking_backend.configs.exceptions.ErrorHttpResponse;

import com.emergency.roadside.help.responder_assignment_backend.configs.exceptions.customexceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleIllegalArgument(HttpMediaTypeNotAcceptableException ex) {
        return ResponseEntity.badRequest().body("bad errorr");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorHttpResponse> handleBadBody(HttpMessageNotReadableException ex) {
        ErrorDTO error = ErrorDTO.builder().code("body_missing").message("Request body is missing").build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorHttpResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorDTO error = ErrorDTO.builder().code("bad_credentials").message("Request credentials not correct, "+ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorHttpResponse> handleBadCredentials(MethodArgumentNotValidException ex) {
        List<ErrorDTO> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorDTO.builder()
                        .code(fieldError.getCode())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                .errors(errors)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorHttpResponse> handleTransactionException(TransactionSystemException ex) {
        System.out.println("transaction exception happened,"+ex);
        Throwable rootCause = ex.getMostSpecificCause();

        List<ErrorDTO> errors = new ArrayList<>();

        if (rootCause instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) rootCause;
            errors = cve.getConstraintViolations().stream()
                    .map(violation -> ErrorDTO.builder()
                            .code("VALIDATION_ERROR")
                            .message(violation.getMessage())
                            .build())
                    .collect(Collectors.toList());
        } else {
            errors.add(ErrorDTO.builder()
                    .code("VALIDATION_ERROR")
                    .message(rootCause.getMessage())
                    .build());
        }

        ErrorHttpResponse errorResponse = ErrorHttpResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }



//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ErrorHttpResponse> handleDuplicateEntry(DataIntegrityViolationException ex) {
//        ErrorDTO error = ErrorDTO.builder().code("data_error").message("Duplicate data or other error").build();
//        ErrorHttpResponse errorResponse = ErrorHttpResponse
//                .builder()
//                .errors(Collections.singletonList(error))
//                .build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorHttpResponse> handleAllOtherError(Exception ex) {
//        console.log("Exception happened, here is the detail: "+ex);
//        ErrorDTO error = ErrorDTO.builder().code("unknown_error").message("Unknown error occurred").build();
//        ErrorHttpResponse errorResponse = ErrorHttpResponse
//                .builder()
//                .errors(Collections.singletonList(error))
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorHttpResponse> handleItemNotFound(ItemNotFoundException ex) {
        ErrorDTO error = ErrorDTO.builder().code(ex.getCode()).message(ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PermissionError.class)
    public ResponseEntity<ErrorHttpResponse> handlePermissionError(PermissionError ex) {
        ErrorDTO error = ErrorDTO.builder().code(ex.getCode()).message(ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(BadDataException.class)
    public ResponseEntity<ErrorHttpResponse> handlebadDataError(BadDataException ex) {
        ErrorDTO error = ErrorDTO.builder().code(ex.getCode()).message(ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorHttpResponse> handleUnprocessableDataError(UnprocessableEntityException ex) {
        ErrorDTO error = ErrorDTO.builder().code(ex.getCode()).message(ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorHttpResponse> handlePermissionError(NoResourceFoundException ex) {
        ErrorDTO error = ErrorDTO.builder().code("invalid_resouce").message("invalid_resouce").build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorHttpResponse> handleSystemError(SystemException ex) {
        ErrorDTO error = ErrorDTO.builder().code(ex.getCode()).message(ex.getMessage()).build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorHttpResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ErrorDTO error = ErrorDTO.builder().code("not_allowed").message("Method not allowed").build();
        ErrorHttpResponse errorResponse = ErrorHttpResponse
                .builder()
                .errors(Collections.singletonList(error))
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

}
