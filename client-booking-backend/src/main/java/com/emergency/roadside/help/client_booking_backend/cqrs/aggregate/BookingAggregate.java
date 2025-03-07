package com.emergency.roadside.help.client_booking_backend.cqrs.aggregate;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.BookingCreatedEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.ClientBookingRegisteredEvent;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.client_booking_backend.model.booking.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.BadDataException;
import io.lettuce.core.dynamic.annotation.Command;
import lombok.NoArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
}
