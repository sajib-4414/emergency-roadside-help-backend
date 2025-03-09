package com.emergency.roadside.help.assistance_service_backend.cqrs.events;

import com.emergency.roadside.help.assistance_service_backend.models.assistance.Assistance;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceItem;
import com.emergency.roadside.help.assistance_service_backend.models.assistance.AssistanceRepository;
import com.emergency.roadside.help.common_module.saga.commands.AssistanceCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AssistanceEventHandler {
    @Autowired
    AssistanceRepository assistanceRepository;

    @EventHandler
    public void onAssistanceCreatedEvent(AssistanceCreatedEvent event){
        System.out.println("EventHandler to write in DB received AssistanceCreatedEvent command ");
        System.out.println("Event details: " + event); // Log the event object
        Assistance assistance = Assistance
                .builder()
                .assistanceId(event.getAssistanceId())
                .startTime(event.getStartTime())
                .responderName(event.getResponderName())
                .responderId(event.getResponderId())
                .status(event.getStatus())
                .bookingId(event.getBookingId())
                .serviceType(event.getServiceType())
                .location(event.getLocation())
                .build();

        log.info("printing the object before persisting....");
        log.info(assistance.toString());
        log.info("status is"+assistance.getStatus());
        assistanceRepository.save(assistance);

    }
}
