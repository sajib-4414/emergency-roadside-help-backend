package com.emergency.roadside.help.assistance_service_backend.controllers;

import com.emergency.roadside.help.assistance_service_backend.models.assistance.Assistance;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceItem;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceItemInputDTO;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceRepository;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assistance")
@AllArgsConstructor
public class AssistanceController {
    AssistanceRepository assistanceRepository;

    @GetMapping("/{assistanceId}")
    public ResponseEntity<Assistance> getAssistanceDetail(
            @PathVariable String assistanceId) {
        Assistance assistance = assistanceRepository.findByAssistanceId(assistanceId).orElseThrow(()-> new ItemNotFoundException("assistance not found"));
        return new ResponseEntity<>(assistance, HttpStatus.OK);
    }

    @GetMapping("/find-by-booking/{bookingId}")
    public ResponseEntity<Assistance> getAssistanceDetailByBookingId(
            @PathVariable String bookingId) {
        Assistance assistance = assistanceRepository.findByBookingId(bookingId).orElseThrow(()-> new ItemNotFoundException("assistance not found"));
        return new ResponseEntity<>(assistance, HttpStatus.OK);
    }
}
