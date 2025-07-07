package com.emergency.roadside.help.client_booking_backend.cqrs.events;

import com.emergency.roadside.help.client_booking_backend.common_module.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.client_booking_backend.cqrs.commads.UpdateBookingWithResponderAssignedWaitingToAcceptCommand;
import com.emergency.roadside.help.client_booking_backend.cqrs.payload.BookingAssignmentDoneWaitingToAcceptEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.payload.BookingCancelledEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.payload.BookingCreatedEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.payload.BookingUpdatedEvent;
import com.emergency.roadside.help.client_booking_backend.cqrs.query.BookingStatusQuery;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatusResponse;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.CacheService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ReplayStatus;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@ProcessingGroup("bookings") //this should tie to bookings rlated dead letter
public class BookingEventHandler {
    private final VehicleRepository vehicleRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ClientRepository clientRepository;
    private CacheService cacheService;
    private QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void onBookingCreatedEvent(BookingCreatedEvent event, ReplayStatus replayStatus){

//        if(true)
//            throw new NullPointerException();

        //ensure this create is fired only if booking does not exist
        BookingRequest br = bookingRequestRepository.findByBookingId(event.getBookingId());
        if(br != null) {
            log.info("booking was already created before with booking id"+event.getBookingId());
            log.info("skipping persisting to db");
            return;
        }

        System.out.println("EventHandler to write in DB received BookingCreatedEvent command ");
        System.out.println("Event details: " + event); // Log the event object
        BookingRequest bookingRequest = new BookingRequest();
        BeanUtils.copyProperties(event,bookingRequest);
        bookingRequest.setRequestedBy(clientRepository.findById(event.getClientId()).get());//we should already have a valid client id here
        bookingRequest.setVehicle(vehicleRepository.findById(event.getVehicleId()).get()); //we should aready have a valid vehicle id here
        log.info("printing the object before persisting....");
        log.info(bookingRequest.toString());
        log.info("status is"+bookingRequest.getStatus());
        bookingRequestRepository.save(bookingRequest);
        BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest);
        cacheService.putBookingToCache(statusResponse);

        //for subscription query
        queryUpdateEmitter.emit(BookingStatusQuery.class,
                query -> query.getBookingId().equals(event.getBookingId()),
                statusResponse);

    }

    @EventHandler
    public void onBookingUpdatedEvent(BookingUpdatedEvent event, ReplayStatus replayStatus){
        System.out.println("EventHandler to write in DB received BookingUpdatedEvent command ");
        System.out.println("Event details: " + event); // Log the event object
        BookingRequest bookingRequest = bookingRequestRepository.findByBookingId(event.getBookingId());
        if(bookingRequest !=null){
            bookingRequest.setStatus(event.getStatus());
            log.info("printing the object before persisting....");
            log.info(bookingRequest.toString());
            log.info("status is"+bookingRequest.getStatus());
            bookingRequestRepository.save(bookingRequest);
            BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest);
            cacheService.putBookingToCache(statusResponse);


            //for subscription query
            queryUpdateEmitter.emit(BookingStatusQuery.class,
                    query -> query.getBookingId().equals(event.getBookingId()),
                    statusResponse);
        }




    }

    @EventHandler
    public void onBookingUpdatedEventWithWaitingForResponderToAccept(BookingAssignmentDoneWaitingToAcceptEvent event, ReplayStatus replayStatus){
        System.out.println("EventHandler to write in DB received BookingAssignmentDoneWaitingToAcceptEvent event ");
        System.out.println("Event details: " + event); // Log the event object
        BookingRequest bookingRequest = bookingRequestRepository.findByBookingId(event.getBookingId());
        if(bookingRequest !=null){
            bookingRequest.setStatus(event.getStatus());
            bookingRequest.setAssignmentId(event.getAssignmentId());
            log.info("printing the object before persisting....");
            log.info(bookingRequest.toString());
            log.info("status is"+bookingRequest.getStatus());
            bookingRequestRepository.save(bookingRequest);
            BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest);
            cacheService.putBookingToCache(statusResponse);


            //for subscription query
            queryUpdateEmitter.emit(BookingStatusQuery.class,
                    query -> query.getBookingId().equals(event.getBookingId()),
                    statusResponse);
        }




    }

    //this is for testing only
//    @EventHandler
//    public void onClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
//        System.out.println("EventHandler to write in DB received ClientBookingRegisteredEvent command ");
//        System.out.println("Event details: " + event); // Log the event object
//
//
//    }

    @EventHandler
    public void onBookingCancelledDuetoRespUnavailable(BookingCancelledEvent event, ReplayStatus replayStatus){

        System.out.println("EventHandler to write in DB received BookingCancelledDuetoRespUnavailable command ");
        System.out.println("Event details: " + event); // Log the event object
        BookingRequest bookingRequest = bookingRequestRepository.findByBookingId(event.getBookingId());
        if(bookingRequest !=null){
            BookingStatus newStatus = event.getStatus();//this contains RESPONDER_SERVICE_UNAVAILABLE
            //it means if the existing status is earlier than RESPONDER_SERVICE_UNAVAILABLE in the enum, only then update
            //if the status has progressed further, then dont update
            if(bookingRequest.getStatus().ordinal()<newStatus.ordinal()){
                bookingRequest.setStatus(event.getStatus());
                log.info("printing the object before persisting....");
                log.info(bookingRequest.toString());
                log.info("status is"+bookingRequest.getStatus());
                bookingRequestRepository.save(bookingRequest);
                BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest);
                cacheService.putBookingToCache(statusResponse);

                //for subscription query
                queryUpdateEmitter.emit(BookingStatusQuery.class,
                        query -> query.getBookingId().equals(event.getBookingId()),
                        statusResponse);
            }
            else{
                log.info("skipping updating the booking as RESPONDER_SERVICE_UNAVAILABLE as the booking was already accepted before");
            }

        }

    }



    @QueryHandler
    public BookingStatusResponse handle(BookingStatusQuery query) {
//        BookingRequest booking = bookingRequestRepository.findByBookingId(query.getBookingId());
//        if(booking == null)
//            throw new ItemNotFoundException("booking not found with id "+query.getBookingId());
//        BookingStatusResponse statusResponse = new BookingStatusResponse(booking);

        BookingStatusResponse bookingStatus = cacheService.getBookingFromCache(query.getBookingId())
                .orElseGet(()->{
                    log.error("booking was not found in cache with the given booking id.................");
                    BookingRequest bookingRequest2 = bookingRequestRepository.findByBookingId(query.getBookingId());
                    if (bookingRequest2==null )
                        throw new ItemNotFoundException("booking not found");
                    BookingStatusResponse statusResponse = new BookingStatusResponse(bookingRequest2);
                    cacheService.putBookingToCache(statusResponse);
                    return statusResponse;
                });
        return
                bookingStatus;
    }
}
