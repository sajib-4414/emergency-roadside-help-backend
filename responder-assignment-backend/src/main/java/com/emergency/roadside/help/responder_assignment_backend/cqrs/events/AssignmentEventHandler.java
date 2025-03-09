package com.emergency.roadside.help.responder_assignment_backend.cqrs.events;

import com.emergency.roadside.help.common_module.saga.events.ResponderAssignedEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderReservedAndNotifiedEvent;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.Assignment;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class AssignmentEventHandler {

    private final AssignmentRepository assignmentRepository;
    private final ResponderRepository responderRepository;

    @EventHandler
    @Transactional
    public void onBookingCreatedEvent(ResponderReservedAndNotifiedEvent event){
        System.out.println("EventHandler to write in DB received ResponderReservedAndNotifiedEvent command ");
        System.out.println("Event details: " + event); // Log the event object
        Assignment assignment = Assignment
                .builder()
                .assignmentId(event.getAssignmentId())
                .assignmentNotes(event.getDescription())
                .assignStatus(event.getAssignStatus())
                .bookingId(event.getBookingId())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .responder(responderRepository.findById(event.getResponderId()).get())//not validating again, as it was validated in command handler
                .serviceType(event.getServiceType())
                .build();
        log.info("printing the object before saving in assignment table....");
        log.info(assignment.toString());
        log.info("status is"+assignment.getAssignStatus());
        assignmentRepository.save(assignment);
    }

    @EventHandler
    @Transactional
    public void onResponderAssignedEvent(ResponderAssignedEvent event){
        System.out.println("EventHandler to write in DB received ResponderAssignedEvent command ");
        System.out.println("Event details: " + event); // Log the event object
        Assignment assignment = assignmentRepository.findByBookingId(event.getBookingId());
        assignment.setAssignStatus(event.getAssignStatus());
        log.info("printing the object before saving in assignment table onResponderAssignedEvent ....");
        log.info(assignment.toString());
        log.info("status is"+assignment.getAssignStatus());
        assignmentRepository.save(assignment);
    }
}
