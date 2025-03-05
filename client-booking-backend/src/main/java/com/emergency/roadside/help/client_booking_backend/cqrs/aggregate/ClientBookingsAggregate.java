package com.emergency.roadside.help.client_booking_backend.cqrs.aggregate;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.BookingAlreadyExists;
import com.emergency.roadside.help.client_booking_backend.configs.exceptions.ClientHasActiveBookingsException;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.RegisterClientBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.ClientBookingRegisteredEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashSet;
import java.util.Set;

@Aggregate
public class ClientBookingsAggregate {
    @AggregateIdentifier
    private Long clientId;
    private Set<String> activeBookingIds = new HashSet<>();

    @CommandHandler
    public ClientBookingsAggregate(RegisterClientBookingCommand command) {
        /*Validation Stage*/
        // Check if client already has active bookings
        if (!activeBookingIds.isEmpty()) {
            throw new ClientHasActiveBookingsException("Client already has active bookings");
        }
        if (activeBookingIds.contains(command.getBookingId())) {
            throw new BookingAlreadyExists("Client already has active bookings");
        }

        //If all good persist to eventstore DB
        AggregateLifecycle.apply(new ClientBookingRegisteredEvent(command.getClientId(), command.getBookingId()));

    }

    //to update the event on eventstore for replaying
    @EventSourcingHandler
    public void onClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
        this.clientId = event.getClientId();
        this.activeBookingIds.add(event.getBookingId());
    }

    //we are not having any event handler to write to DB for this event, as its more like checking from
    //agregate. in db we already have client bookings in the bookings table

    //but we will still listen to this in saga

}
