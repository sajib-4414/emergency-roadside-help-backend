package com.emergency.roadside.help.responder_assignment_backend.controller;

import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.service.ResponderService;
import com.emergency.roadside.help.responder_assignment_backend.model.service.ResponderServiceDTO;
import com.emergency.roadside.help.responder_assignment_backend.services.ResponderSupportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/responder-services")
@AllArgsConstructor
public class ResponderServiceController {


    private ResponderSupportService responderSupportService;

    @PostMapping
    public ResponderService createService(@RequestBody ResponderServiceDTO responderServiceDTO) {
        ResponderService createdService = responderSupportService.createService(responderServiceDTO);
        return createdService;
    }

    @PutMapping("/{id}")
    public ResponderService updateService(@PathVariable Long id, @RequestBody ResponderServiceDTO responderServiceDTO) {

        ResponderService updatedService = responderSupportService.updateService(id, responderServiceDTO);
        return updatedService;
    }

    @DeleteMapping("/{id}")
    public void deleteService(@PathVariable Long id) {
        responderSupportService.deleteService(id);
    }

    @GetMapping("/my-services")
    public List<ResponderService> getAllMyServices() {
        List<ResponderService> services = responderSupportService.getAllServices();
        return services;
    }

}