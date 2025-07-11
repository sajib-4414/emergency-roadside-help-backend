package com.emergency.roadside.help.responder_assignment_backend.cqrs.aggregate;


import com.emergency.roadside.help.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.common_module.saga.commands.CancelResponderAssignmentCommand;
import com.emergency.roadside.help.common_module.saga.commands.FindResponderCommand;
import com.emergency.roadside.help.common_module.saga.events.ResponderAssignedEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderAssignmentCancelledEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderNotFoundEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderReservedAndNotifiedEvent;
import com.emergency.roadside.help.responder_assignment_backend.cqrs.commands.ResponderAcceptedCommand;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.Assignment;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aggregate
@NoArgsConstructor
public class AssignmentAggregate {


    @AggregateIdentifier
    private String assignmentId;

    private String bookingId;
    private Long responderId;

    private AssignStatus assignStatus;

    private ServiceType serviceType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String assignmentNotes;


    @CommandHandler
    public AssignmentAggregate(FindResponderCommand command, ResponderRepository responderRepository){

        try{
            //validate the command before storing it in eventstore, which is primary write DB
            //check things like if status is good, priority is good, if applicable
            // Check if the aggregate already exists, handle it in saga
            //this check
            if(this.assignStatus == AssignStatus.ASSIGNED){
                throw new IllegalStateException("Order already assigned");
            }
            //read the database to see who is available, see the avaiablity, city etc.
            //for simplicity lets assume we give to the first driver, and we keep assigning, we are not checking if someone is free

            Responder responder = responderRepository.findById(1L).orElseThrow(()->new ItemNotFoundException("Responder not found"));
            String uniqueAssignmentId = UUID.randomUUID().toString();

            //if all good persist to event db that booking created
            ResponderReservedAndNotifiedEvent event = ResponderReservedAndNotifiedEvent
                    .builder()
                    .assignmentId(uniqueAssignmentId)
                    .bookingId(command.getBookingId())
                    .description(command.getDescription())
                    .priority(command.getPriority())
                    .assignStatus(AssignStatus.RESERVED)
                    .startTime(LocalDateTime.now())
                    .endTime(null)
                    .responderId(responder.getId())
                    .serviceType(command.getServiceType())
                    .build();
            AggregateLifecycle.apply(event);
            log.info("just dispatched the ResponderReservedAndNotifiedEvent as soon as i reserved a responder in the responder service");
        } catch (ItemNotFoundException e) {
            log.error("driver not found in command handler of responder microservice");
            log.info("sending event that booking cancelled");
            ResponderNotFoundEvent responderNotFoundEvent = ResponderNotFoundEvent
                    .builder()
                    .bookingId(command.getBookingId())
                    .build();
            AggregateLifecycle.apply(responderNotFoundEvent);

        }


    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void onResponderAcceptedCommand(ResponderAcceptedCommand command){
        try{
            //validate the command before storing it in eventstore, which is primary write DB
            //check things like if status is good, priority is good, if applicable
            // Check if the aggregate already exists, handle it in saga
//            if (this.bookingId != null) {
//                throw new IllegalStateException("Booking already exists for ID: " + this.bookingId);
//            }
            //read the database to see who is available, see the avaiablity, city etc.
            //for simplicity lets assume we give to the first driver, and we keep assigning, we are not checking if someone is free


            //if all good persist to event db that booking created
            ResponderAssignedEvent event = ResponderAssignedEvent
                    .builder()
                    .assignmentId(command.getAssignmentId())
                    .responderName(command.getResponderName())
                    .responderId(command.getResponderId())
                    .bookingId(command.getBookingId())
                    .assignStatus(AssignStatus.ASSIGNED)
                    .build();
            AggregateLifecycle.apply(event);
            log.info("just dispatched the ResponderAssignedEvent as soon as i respoonder accepted in the responder service");
        } catch (ItemNotFoundException e) {
            log.error("responder accepting failure, ");
            log.info("sending event that responder accepting failed, either assign another one, or if max retry fail, send saga notificaiton that we failed");
//            ResponderNotFoundEvent responderNotFoundEvent = ResponderNotFoundEvent
//                    .builder()
//                    .bookingId(command.getBookingId())
//                    .build();
//            AggregateLifecycle.apply(responderNotFoundEvent);
        }
    }


    @CommandHandler
    public void onCancelResponderAssignmentCommand(CancelResponderAssignmentCommand command){
        try{


            //if all good persist to event db that booking created
            ResponderAssignmentCancelledEvent event = ResponderAssignmentCancelledEvent
                    .builder()
                    .assignmentId(command.getAssignmentId())
                    .bookingId(command.getBookingId())
                    .assignStatus(AssignStatus.REVOKED) //means system cancelled it
                    .build();
            AggregateLifecycle.apply(event);
            log.info("just dispatched the ResponderAssignmentCancelledEvent as soon as ");
        } catch (ItemNotFoundException e) {
//            ResponderNotFoundEvent responderNotFoundEvent = ResponderNotFoundEvent
//                    .builder()
//                    .bookingId(command.getBookingId())
//                    .build();
//            AggregateLifecycle.apply(responderNotFoundEvent);
        }
    }


    //to update the event on eventstore for replaying
    @EventSourcingHandler
    public void onResponderReservedAndNotifiedEvent(ResponderReservedAndNotifiedEvent event){



        if(this.bookingId!=null){
            log.info("booking id=",bookingId);
            throw new IllegalStateException("Aggregate already exists!");
        }


        if(this.bookingId ==null)
            this.bookingId = event.getBookingId();

        this.assignmentId = event.getAssignmentId();
        this.assignStatus = event.getAssignStatus();
        this.serviceType = event.getServiceType();
        this.startTime =event.getStartTime();
        this.endTime = event.getEndTime();
        this.assignmentNotes = event.getDescription();
        if(this.responderId ==null)
            this.responderId =event.getResponderId();
    }

    @EventSourcingHandler
    public void onResponderAssignedEvent(ResponderAssignedEvent event){
        this.bookingId = event.getBookingId();
        this.assignmentId = event.getAssignmentId();
        this.assignStatus = event.getAssignStatus();
    }

    @EventSourcingHandler
    public void onResponderAssignmentCancelledEvent(ResponderAssignmentCancelledEvent event){
        this.bookingId = event.getBookingId();
        this.assignmentId = event.getAssignmentId();
        this.assignStatus = event.getAssignStatus();
    }
}

