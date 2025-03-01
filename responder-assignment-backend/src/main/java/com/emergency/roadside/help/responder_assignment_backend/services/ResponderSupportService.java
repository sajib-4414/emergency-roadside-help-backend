package com.emergency.roadside.help.responder_assignment_backend.services;

import com.emergency.roadside.help.responder_assignment_backend.configs.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.responder_assignment_backend.external.ExternalUser;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.service.ResponderService;
import com.emergency.roadside.help.responder_assignment_backend.model.service.ResponderServiceDTO;
import com.emergency.roadside.help.responder_assignment_backend.model.service.ResponderServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.emergency.roadside.help.responder_assignment_backend.configs.auth.AuthHelper.getCurrentUser;

@Service
@AllArgsConstructor
public class ResponderSupportService {
    private ResponderServiceRepository responderServiceRepository;
    private ResponderRepository responderRepository;

    @Transactional
    public ResponderService createService(ResponderServiceDTO payload) {
        ExternalUser user = getCurrentUser();
        Responder responder = responderRepository.findByUserId(user.getId()).orElseThrow(()->new ItemNotFoundException("responder not found"));
        ResponderService rs = ResponderService.builder()
                .notes(payload.getNotes())
                .serviceType(payload.getServiceType())
                .price(payload.getPrice())
                .provider(responder)
                .build();
        return responderServiceRepository.save(rs);
    }

    @Transactional
    public ResponderService updateService(Long id, ResponderServiceDTO payload) {
        Optional<ResponderService> existingService = responderServiceRepository.findById(id);
        if (existingService.isPresent()) {
            ResponderService serviceToUpdate = existingService.get();
            serviceToUpdate.setServiceType(payload.getServiceType());
            serviceToUpdate.setPrice(payload.getPrice());
            serviceToUpdate.setNotes(payload.getNotes());
            return responderServiceRepository.save(serviceToUpdate);
        } else {
            throw new RuntimeException("Service not found");
        }
    }

    @Transactional
    public void deleteService(Long id) {
        responderServiceRepository.deleteById(id);
    }

    public List<ResponderService> getAllServices() {
        ExternalUser user = getCurrentUser();
        Responder responder = responderRepository.findByUserId(user.getId()).orElseThrow(()->new ItemNotFoundException("responder not found"));
        return responderServiceRepository.findAllByProvider(responder);
    }
}
