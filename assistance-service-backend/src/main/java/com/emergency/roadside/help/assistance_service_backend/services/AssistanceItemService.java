package com.emergency.roadside.help.assistance_service_backend.services;

import com.emergency.roadside.help.assistance_service_backend.models.assistance.*;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AssistanceItemService {
    private final AssistanceItemRepository assistanceItemRepository;
    private final AssistanceRepository assistanceRepository;

    @Transactional
    public List<AssistanceItem> createAssistanceItems(Long assistanceId, List<AssistanceItemInputDTO> payloadItems){
        Assistance assistance = assistanceRepository.findById(assistanceId).orElseThrow(()->new ItemNotFoundException("assistance not found"));
        List<AssistanceItem> itemsAssociatedWithAssistance = payloadItems
                 .stream()
                 .map(item->{
                     AssistanceItem assistanceItem = AssistanceItem.builder()
                             .itemName(item.getItemName())
                             .assistance(assistance)
                             .charge(item.getCharge())
                             .description(item.getDescription())
                             .quantity(item.getQuantity())
                             .build();
                     return assistanceItem;
                 })
                .collect(Collectors.toList());
        assistanceItemRepository.saveAll(itemsAssociatedWithAssistance);
        return itemsAssociatedWithAssistance;
    }
    @Transactional
    public void deleteAssistanceItem(Long assistanceItemId){
        assistanceItemRepository.deleteById(assistanceItemId);
    }

    public List<AssistanceItem> getAssistanceItems(Long assistanceId){
        return assistanceItemRepository.findAllByAssistance_Id(assistanceId);
    }

}
