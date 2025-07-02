package com.emergency.roadside.help.client_booking_backend.cqrs.saga;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CancelBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.CreateBookingCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderAssignedWaitingToAcceptCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderFoundCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.payload.*;
//import com.emergency.roadside.help.client_booking_backend.cqrs.events.ClientBookingRegisteredEvent;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.common_module.saga.commands.AssistanceCreatedEvent;
import com.emergency.roadside.help.common_module.saga.commands.CancelResponderAssignmentCommand;
import com.emergency.roadside.help.common_module.saga.commands.CreateAssistanceCommand;
import com.emergency.roadside.help.common_module.saga.commands.FindResponderCommand;
import com.emergency.roadside.help.common_module.saga.events.ResponderAssignedEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderAssignmentCancelledEvent;
import com.emergency.roadside.help.common_module.saga.events.ResponderReservedAndNotifiedEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Saga
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class BookingSaga {

    //must use autowired. Cosntructor injection does not work
    //must use transient without this, serialization error occurs
    @Autowired
    private transient CommandGateway commandGateway;
    private  QueryGateway queryGateway;
    @Autowired
    private transient BookingRequestRepository bookingRequestRepository;


    private static final List<Long>  delaySchedule = Arrays.asList(5000L, 10000L, 20000L);
    private Map<String, String > bookingAndDeadLineIDs = new HashMap<>();
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
//    @StartSaga
//    @SagaEventHandler(associationProperty = "clientId")
//    private void handleClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
//        log.info("booking is being created for the client in saga starting, for clientid="+event.getClientId()+", for bookingid="+event.getBookingId());
//        log.info("event="+event);
//        try{
//            //by now in eventstore we have written that client is wanting to book this booking
//            //you can do some check with queryhandler, to send some query to relationalDB if  you want.
//            //like to see if projection is written, all good to go to next stage of creating the booking
//            //as per deepseek, its not recommended to do a query here to the read db, because readdb is meant to be
//            //eventually consistent, but if you still need to be very consistent you can do it.
//
//            //this saga started with asscociating with client id, but as its actually a booking id, so are manually associating it as
//            //well, otherwise saga handler with this association id will not be fired
//            SagaLifecycle.associateWith("bookingId", event.getBookingId());
//
//            //then you can command the next step create booking
//            CreateBookingCommand command = CreateBookingCommand
//                    .builder()
//                    .bookingId(event.getBookingId())
//                    .status(event.getStatus()) //now booking is in queued state
//                    .clientId(event.getClientId())
//                    .dateCreated(event.getDateCreated())
//                    .description(event.getDescription())
//                    .priority(event.getPriority())
//                    .serviceType(event.getServiceType())
//                    .vehicleId(event.getVehicleId())
//                    .address(event.getAddress())
//                    .build();
//            commandGateway.sendAndWait(command);
//        } catch (Exception e) {
//            log.error("error after booking id added in clients profile in eventstore but before going to create booking,clientid="+event.getClientId()+",bookingid="+ event.getBookingId());
//            log.error("cancelling the booking creation from saga, doing a cancel booking command");
//            log.error(e.getMessage());
//            sendClientBookingCancelCommand(event);
//        }
//
//
//    }

    @StartSaga
    @SagaEventHandler(associationProperty = "bookingId")
    private void handleBookingCreatedEvent(BookingCreatedEvent event){
        log.info("in saga, handleBookingCreatedEvent for bookingid="+event.getBookingId()+", client id="+event.getClientId());
        log.info("bookign created in the event store, should be created asynchrounsly in read db also");
        log.info("event="+event);
        try{
            //by now in eventstore we have written booking in tits db
            //you can do some check with queryhandler, to send some query to relationalDB if  you want.
            //like to see if projection is written, all good to go to next stage of creating the booking
            //as per deepseek, its not recommended to do a query here to the read db, because read
            // db is updated asyncrhonosuly, you can check other things like if external service data is processed/payment
            // is processed by extenral service
            // because readdb is meant to be
            //eventually consistent, but if you still need to be very consistent you can do it.

            FindResponderCommand command = FindResponderCommand.builder()
                    .bookingId(event.getBookingId())
                    .description(event.getDescription())
                    .serviceType(event.getServiceType())
                    .priority(event.getPriority())
                    .build();
//            commandGateway.sendAndWait(command, 15,TimeUnit.SECONDS
//                    ); // Timeout in milliseconds
            //better way is to have asyncrhonous handler

            //a better way is to try with a retry settings with exponential backoff, although we can write manual code
            //but axon provides a way of doing central retry, retry settigns(which error to retry) with exponential backoff
            //i already did htat in axon config, so i am not repeating that here
            commandGateway.send(command, (originalCommandMessage, commandResultMessage) -> {
                if (commandResultMessage.isExceptional()) {
                    Throwable cause = commandResultMessage.exceptionResult();
                    System.err.println("Command failed: " + cause.getMessage());
                    // Log or take action based on the failure
                    sendResponderServiceFailureCompensatingCommand(event.getBookingId(), BookingCancelReason.RESPONDER_SERVICE_UNAVAILABLE);
                } else {
                    System.out.println("Command processed successfully: " + commandResultMessage.getPayload());
                }
            });




        } catch (Exception e) {
            log.error("unexpected error is ",e.getStackTrace());
            log.error("find responder command creating unexpected error..............");
            log.error(e.getMessage());
            sendResponderServiceFailureCompensatingCommand(event.getBookingId(), BookingCancelReason.ERROR_ON_BOOKING_SERVICE);
        }

    }

    //means no driver responded, or we did not get any driver assigned event
    @DeadlineHandler(deadlineName = "findResponderDeadline")
    public void on(DeadlineForFindResponderPayload deadlinePayload) {
        log.warn("deadline si being triggered");
        // handle the deadline
        sendResponderServiceFailureCompensatingCommand(deadlinePayload.getBookingId(), BookingCancelReason.RESPONDER_DID_NOT_ACCEPT);
    }


    private void sendResponderServiceFailureCompensatingCommand(String bookingId, BookingCancelReason reason) {

        CancelBookingCommand command1  = CancelBookingCommand.
                builder()
                .bookingId(bookingId)
                .cancelReason(reason)

                .build();
        commandGateway.send(command1,((commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                //error happened again
                //show it up on tracer or alert maybe
            }
            else{
                //all good
                log.error("success doing a compensating command to roll back booking in my current service");
            }
        }));
    }



//    private void sendClientBookingCancelCommand(ClientBookingRegisteredEvent event) {
//        //send a command to update the aggreagate also
//
//    }

    @SagaEventHandler(associationProperty = "bookingId")
    private void handleResponderAssignedEvent(ResponderAssignedEvent event, DeadlineManager deadlineManager){
        log.info("in saga, ResponderAssignedEvent for bookingid="+event.getBookingId());
        log.info("event="+event);

        //if we get event that responder was assigned, then cancel the deadline

        String deadlineId = bookingAndDeadLineIDs.get(event.getBookingId());
        bookingAndDeadLineIDs.remove(event.getBookingId());
        if (deadlineId != null) {
            deadlineManager.cancelSchedule("findResponderDeadline", deadlineId);
        }

        try{
            //by now in eventstore we have written booking in tits db
            //you can do some check with queryhandler, to send some query to relationalDB if  you want.
            //like to see if projection is written, all good to go to next stage of creating the booking
            //as per deepseek, its not recommended to do a query here to the read db, because readdb is meant to be
            //eventually consistent, but if you still need to be very consistent you can do it.
            UpdateBookingWithResponderFoundCommand command = UpdateBookingWithResponderFoundCommand.builder()
                    .bookingId(event.getBookingId())
                    .responderId(event.getResponderId())
                    .responderName(event.getResponderName())
                    .bookingStatus(BookingStatus.RESPONDER_ASSIGNED)
                    .build();
            commandGateway.sendAndWait(command);


        } catch (Exception e) {
            throw new RuntimeException(e);

//            sendBookingCancelCompensatingCommand(event);
        }

    }

    @SagaEventHandler(associationProperty = "bookingId")
    private void handleBookingUpdatedEvent(BookingUpdatedEvent event){
        log.info("in saga, BookingUpdatedEvent for bookingid="+event.getBookingId());
        log.info("event="+event);

        try{
            if(event.getStatus() == BookingStatus.RESPONDER_ASSIGNED){
                log.info("my next stage is calling assitance service and then create a assistance row");

                //do some db queyr to get address and service type
                BookingRequest bookingRequest= bookingRequestRepository.findByBookingId(event.getBookingId());

                CreateAssistanceCommand command = CreateAssistanceCommand.builder()
                        .bookingId(event.getBookingId())
                        .responderName(event.getResponderName())
                        .responderId(event.getResponderId())
                        .address(bookingRequest.getAddress())
                        .serviceType(bookingRequest.getServiceType())
                        .build();
                commandGateway.sendAndWait(command);
            }

            //by now in eventstore we have written booking in tits db
            //you can do some check with queryhandler, to send some query to relationalDB if  you want.
            //like to see if projection is written, all good to go to next stage of creating the booking
            //as per deepseek, its not recommended to do a query here to the read db, because readdb is meant to be
            //eventually consistent, but if you still need to be very consistent you can do it.



        } catch (Exception e) {
            throw new RuntimeException(e);

//            sendBookingCancelCompensatingCommand(event);
        }

    }
    @EndSaga
    @SagaEventHandler(associationProperty = "bookingId")
    //assistance item has been created, responder assigned. now we give time to assistnace guy come on in
    //the status update will happen from the assistance service as saga orchestrator
    public void handleAssistanceCreatedBookingCompleted(AssistanceCreatedEvent event){
        log.info("SAGA is DONE...................YEEEEEEEEEEEE");
        log.info("assistance also created on the assistance service, for now booking creation done");
        log.info("event="+event);
    }



    @SagaEventHandler(associationProperty = "bookingId")
    public void onBookingCancelled(BookingCancelledEvent event){
        //if the error is related to like nobody responded, then send a command to responder service to cancel the assignment there
        if(event.getStatus() == BookingStatus.RESPONDER_DID_NOT_ACCEPT){
            CancelResponderAssignmentCommand command = CancelResponderAssignmentCommand.builder()
                    .bookingId(event.getBookingId())
                    .assignmentId(event.getAssignmentId())
                    .build();
            commandGateway.send(command,((commandMessage, commandResultMessage) -> {
                if (commandResultMessage.isExceptional()) {
                    System.out.println(command);
                    System.out.println(commandResultMessage);
                    //error happened again
                    //show it up on tracer or alert maybe
                }
                else{
                    //all good
                    log.error("success sending a command to responder to cancel the assignment of that booking");
                }
            }));
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "bookingId")
    public void onResponderAssignmentCancelledEventInResponderService(ResponderAssignmentCancelledEvent event){

        //booking was already cancelled before sending command to responder assignment service
        //so this just serves as an acknowledgement

    }

    @SagaEventHandler(associationProperty = "bookingId")
    public void onResponderReservedAndWaitingForAcceptanceForAggregate(ResponderReservedAndNotifiedEvent event){
        //command to update booking with assignment id, then we will start deadline after booking is updated
        UpdateBookingWithResponderAssignedWaitingToAcceptCommand command = UpdateBookingWithResponderAssignedWaitingToAcceptCommand.builder()
                .bookingId(event.getBookingId())
                .assignmentId(event.getAssignmentId())
                .bookingStatus(BookingStatus.RESERVED_WAITING)
                .build();
        commandGateway.sendAndWait(command);
    }

    //means booking db and event store has stored the assignment id, now go ahead with deadline
    @SagaEventHandler(associationProperty = "bookingId")
    public void onBookingAssignmentDoneWaitingToAcceptEvent(BookingAssignmentDoneWaitingToAcceptEvent event ,DeadlineManager deadlineManager){
        //now we start the deadline and wait
        //also start a deadline to not wait indefinitely for driver acceptance
        String deadlineId = deadlineManager.schedule(
                Duration.ofMillis(15000), "findResponderDeadline",
                new DeadlineForFindResponderPayload(event.getBookingId())
        );
        if(bookingAndDeadLineIDs == null)
            bookingAndDeadLineIDs = new HashMap<>();
        bookingAndDeadLineIDs.put(event.getBookingId(), deadlineId);

    }


}
