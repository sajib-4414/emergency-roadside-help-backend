package com.emergency.roadside.help.client_booking_backend.cqrs.aggregate;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderFoundCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.BookingCreatedEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.BookingUpdatedEvent;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Aggregate
@NoArgsConstructor
public class BookingAggregate {

    @AggregateIdentifier
    private String bookingId;

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

    @EventSourcingHandler
    public void onBookingCreatedEvent(BookingUpdatedEvent event){
        this.bookingId = event.getBookingId();
        this.status = event.getStatus();
    }
}
