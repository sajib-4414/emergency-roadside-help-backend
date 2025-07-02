package com.emergency.roadside.help.client_booking_backend.cqrs.aggregate;

import com.emergency.roadside.help.client_booking_backend.configs.exceptions.BookingAlreadyExists;
import com.emergency.roadside.help.client_booking_backend.configs.exceptions.ClientHasActiveBookingsException;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.RegisterClientBookingCommand;
//import com.emergency.roadside.help.client_booking_backend.cqrs.events.ClientBookingRegisteredEvent;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

//@Aggregate
//@Slf4j
//public class ClientBookingsAggregate {
//    @AggregateIdentifier
//    private Long clientId;
//    private Set<String> activeBookingIds = new HashSet<>();
//
//
//
//@CommandHandler
//@CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
//    public void handleEvent(RegisterClientBookingCommand command) {
//        /*Validation Stage*/
////         Check if client already has active bookings
//    //will put this back on, when i have complete mechanism of removing active booking
////        if (!this.activeBookingIds.isEmpty()) {
////            throw new ClientHasActiveBookingsException("Client already has active bookings");
////        }
////        if (this.activeBookingIds.contains(command.getBookingId())) {
////            throw new BookingAlreadyExists("Client already has active bookings");
////        }
//
//        //If all good persist to eventstore DB
//        ClientBookingRegisteredEvent event = new ClientBookingRegisteredEvent();
//        BeanUtils.copyProperties(command,event);
//        AggregateLifecycle.apply(event);
//        log.info("event successfully applied to lifecycle");
//        //this is going to eventsource handler, event handler, saga event handler
//
//    }
//
//
//    //to update the event on eventstore for replaying
//    @EventSourcingHandler
//    public void onClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
//
////        if(this.clientId !=null)
////            System.out.println("aggreagate exists with this client id="+this.clientId);
//
////        System.out.println("now hashset before is "+this.activeBookingIds);
//        try{
//            this.clientId = event.getClientId();
//            this.activeBookingIds.add(event.getBookingId());
//
////            System.out.println("now hashset is"+this.activeBookingIds);
//        }catch (Exception exception){
//            System.out.println("error happened here"+exception.getMessage());
//        }
//
//    }
//
//    //we are not having any event handler to write to DB for this event, as its more like checking from
//    //agregate. in db we already have client bookings in the bookings table
//
//    //but we will still listen to this in saga
//
//}
