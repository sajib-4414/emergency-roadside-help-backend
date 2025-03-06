package com.emergency.roadside.help.client_booking_backend.cqrs.saga;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.events.ClientBookingRegisteredEvent;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;

@Saga
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class BookingSaga {
    private  CommandGateway commandGateway;
    private  QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "bookingId")
    /*
    Why Not Use clientId as the associationProperty?
Scope Mismatch:
The clientId identifies the client, not the booking process.
Using clientId as the associationProperty would cause the saga to handle all bookings
 for the same client, which is not the desired behavior.

 Also
 The @AggregateIdentifier in the ClientBookingsAggregate is clientId,
 but the saga correlates events using bookingId because the saga manages
 the booking process, not the client.
     */
    private void handleClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
        log.info("booking is being created for the client in saga starting, for clientid="+event.getClientId()+", for bookingid="+event.getBookingId());
        log.info("event="+event);
        try{
            //by now in eventstore we have written that client is wanting to book this booking
            //you can do some check with queryhandler, to send some query to relationalDB if  you want.
            //like to see if projection is written, all good to go to next stage of creating the booking
            //as per deepseek, its not recommended to do a query here to the read db, because readdb is meant to be
            //eventually consistent, but if you still need to be very consistent you can do it.

            //then you can command the next step create booking
            CreateBookingCommand command = CreateBookingCommand
                    .builder()
                    .bookingId(event.getBookingId())
                    .bookingStatus(BookingStatus.CREATED.toString()) //now booking is in created state
                    .clientId(event.getClientId())
                    .dateCreated(event.getDateCreated())
                    .description(event.getDescription())
                    .priority(event.getPriority())
                    .serviceType(event.getServiceType())
                    .vehicleId(event.getVehicleId())
                    .build();
            commandGateway.sendAndWait(command);
        } catch (Exception e) {
            log.error("error after booking id added in clients profile in eventstore but before going to create booking,clientid="+event.getClientId()+",bookingid="+ event.getBookingId());
            log.error("cancelling the booking creation from saga, doing a cancel booking command");
            sendClientBookingCancelCommand(event);
        }



    }

    private void sendClientBookingCancelCommand(ClientBookingRegisteredEvent event) {

    }

}
