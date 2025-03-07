package com.emergency.roadside.help.client_booking_backend.cqrs.events;

import com.emergency.roadside.help.client_booking_backend.cqrs.commads.RegisterClientBookingCommand;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BookingEventHandler {
    private final VehicleRepository vehicleRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ClientRepository clientRepository;

    @EventHandler
    public void onBookingCreatedEvent(BookingCreatedEvent event){
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

    }

    //this is for testing only
    @EventHandler
    public void onClientBookingRegisteredEvent(ClientBookingRegisteredEvent event){
        System.out.println("EventHandler to write in DB received ClientBookingRegisteredEvent command ");
        System.out.println("Event details: " + event); // Log the event object


    }
}
