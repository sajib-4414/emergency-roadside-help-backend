package com.emergency.roadside.help.assistance_service_backend.controllers;

import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceItem;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceItemInputDTO;
import com.emergency.roadside.help.assistance_service_backend.services.AssistanceItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assistance-items")
@AllArgsConstructor
public class AssistanceItemController {
    private final AssistanceItemService assistanceItemService;

    @PostMapping("/{assistanceId}")
    public ResponseEntity<List<AssistanceItem>> createAssistanceItems(
            @PathVariable Long assistanceId,
            @Validated  @RequestBody List<AssistanceItemInputDTO> payloadItems) {
        List<AssistanceItem> createdItems = assistanceItemService.createAssistanceItems(assistanceId, payloadItems);
        return new ResponseEntity<>(createdItems, HttpStatus.CREATED);
    }

    @GetMapping("/{assistanceId}")
    public ResponseEntity<List<AssistanceItem>> getAssistanceItems(
            @PathVariable Long assistanceId,
            @Validated  @RequestBody List<AssistanceItemInputDTO> payloadItems) {
        List<AssistanceItem> assistanceItems = assistanceItemService.getAssistanceItems(assistanceId);
        return new ResponseEntity<>(assistanceItems, HttpStatus.OK);
    }

    @DeleteMapping("/{assistanceItemId}")
    public ResponseEntity<Void> deleteAssistanceItem(@PathVariable Long assistanceItemId) {
        assistanceItemService.deleteAssistanceItem(assistanceItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
