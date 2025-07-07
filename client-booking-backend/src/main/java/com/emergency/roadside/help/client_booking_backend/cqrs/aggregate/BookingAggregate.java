package com.emergency.roadside.help.client_booking_backend.cqrs.aggregate;

import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.Priority;
import com.emergency.roadside.help.client_booking_backend.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CancelBookingCommand;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderAssignedWaitingToAcceptCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderFoundCommand;

import com.emergency.roadside.help.client_booking_backend.cqrs.payload.*;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Aggregate
@NoArgsConstructor
@Slf4j
public class BookingAggregate {

    @AggregateIdentifier
    private String bookingId;

    private String assignmentId;

    private Long clientId;

    private BookingStatus status;

    private ServiceType serviceType;

    private Long vehicleId;

    private String description;

    private Priority priority;

    private LocalDateTime dateCreated;

    private String address;


    @CommandHandler
    public BookingAggregate(CreateBookingCommand command){


        //validate the command before storing it in eventstore, which is primary write DB
        //check things like if status is good, priority is good, if applicable
        // Check if the aggregate already exists, handle it in saga
        //this i think is only applicable for the first command hanlder of an aggregate
        if (this.bookingId != null) {
            throw new IllegalStateException("Booking already exists for ID: " + this.bookingId);
        }

        //if all good persist to event db that booking created
        BookingCreatedEvent event = BookingCreatedEvent.builder().build();
        BeanUtils.copyProperties(command,event );
        event.setStatus(BookingStatus.CREATED);
        AggregateLifecycle.apply(event);
    }

    //to update the event on eventstore for replaying
    @EventSourcingHandler
    public void onBookingCreatedEvent(BookingCreatedEvent event){
        this.bookingId = event.getBookingId();
        this.clientId = event.getClientId();
        this.status = event.getStatus();
        this.serviceType = event.getServiceType();
        this.vehicleId = event.getVehicleId();
        this.description = event.getDescription();
        this.priority = event.getPriority();
        this.dateCreated = event.getDateCreated();
        this.address = event.getAddress();
    }

    @CommandHandler
    public void onUpdateBookingWithResponderFoundCommand(UpdateBookingWithResponderFoundCommand command){


        //validate the command before storing it in eventstore, which is primary write DB
        //check things like if status is good, priority is good, if applicable
        // Check if the aggregate already exists, handle it in saga


        //if all good persist to event db that booking created
        BookingUpdatedEvent event = BookingUpdatedEvent
                .builder()
                .bookingId(command.getBookingId())
                .status(command.getBookingStatus())
                .responderId(command.getResponderId())
                .responderName(command.getResponderName())
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void onUpdateBookingWithResponderAssignedWaitingToAcceptCommand(UpdateBookingWithResponderAssignedWaitingToAcceptCommand command){


        //validate the command before storing it in eventstore, which is primary write DB
        //check things like if status is good, priority is good, if applicable
        // Check if the aggregate already exists, handle it in saga


        //if all good persist to event db that booking created
        BookingAssignmentDoneWaitingToAcceptEvent event = BookingAssignmentDoneWaitingToAcceptEvent
                .builder()
                .bookingId(command.getBookingId())
                .assignmentId(command.getAssignmentId())
                .status(command.getBookingStatus())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void onBookingCreatedEvent(BookingUpdatedEvent event){
        this.bookingId = event.getBookingId();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void onBookingCreatedEvent(BookingAssignmentDoneWaitingToAcceptEvent event){
        this.bookingId = event.getBookingId();
        this.status = event.getStatus();
        this.assignmentId =event.getAssignmentId();
    }

    @CommandHandler
    public void CancelBookingCommandHandler(CancelBookingCommand command){
        log.info("received cancel booking command......");
        log.info(command.toString());

        BookingCancelledEvent event= BookingCancelledEvent.builder()
                .bookingId(command.getBookingId())
                .assignmentId(this.assignmentId)
                .build();
        if(command.getCancelReason() == BookingCancelReason.RESPONDER_SERVICE_UNAVAILABLE)
            event.setStatus(BookingStatus.RESPONDER_SERVICE_UNAVAILABLE);
        else if(command.getCancelReason() == BookingCancelReason.NO_RESPONDER_FOUND)
            event.setStatus(BookingStatus.NO_RESPONDER_FOUND);
        else if(command.getCancelReason() == BookingCancelReason.ERROR_ON_BOOKING_SERVICE)
            event.setStatus(BookingStatus.CANCELLED);
        else if(command.getCancelReason() == BookingCancelReason.RESPONDER_DID_NOT_ACCEPT)
            event.setStatus(BookingStatus.RESPONDER_DID_NOT_ACCEPT);
        else{
            log.warn("booking is being cancelled, with null reason");
            log.warn("booking id={}", command.getBookingId());
            event.setStatus(BookingStatus.CANCELLED);
        }

        log.info("booking cancel event being applied to lifecycle");
        log.info(event.toString());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void onBookingCancelledDuetoRespUnavailable(BookingCancelledEvent event){
        this.bookingId = event.getBookingId();
        this.status = event.getStatus();
    }


}
