package com.emergency.roadside.help.client_booking_backend.unittesting.repositorytesting;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatus;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.client.UserRepository;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BRRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRequestRepository BRRepository;

    private User user;
    private Client client;
    private Vehicle vehicle;

    @BeforeEach
    void setUp(){

        final String username = "test1";
        final String password = "12345";
        final String email = "test1@example.com";
        User payload = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        user = userRepository.save(payload);


        final String clientName = "testclient";
        Client payloadClient = Client
                .builder()
                .user(user)
                .name(clientName)
                .build();
        client = clientRepository.save(payloadClient);

        Vehicle payloadVehicle = Vehicle
                .builder()
                .make("Toyota")
                .model("camry")
                .plate("126asda")
                .year(2020)
                .build();
        vehicle = vehicleRepository.save(payloadVehicle);

    }

    @Test
    @Transactional
    @Rollback
    public void testCreateBooking(){

        String uniqueBookingId = UUID.randomUUID().toString();
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setBookingId(uniqueBookingId);
        bookingRequest.setRequestedBy(client);
        bookingRequest.setStatus(BookingStatus.QUEUED);
        bookingRequest.setPriority(Priority.NOW);
        bookingRequest.setAddress("33 shaw st");
        bookingRequest.setDateCreated(LocalDateTime.now());
        bookingRequest.setServiceType(ServiceType.TOWING);
        bookingRequest.setVehicle(vehicle);
        BookingRequest savedBR = BRRepository.save(bookingRequest);
        assertNotNull(savedBR);
        BookingRequest retrievedBookingRequest = BRRepository.findByBookingId(uniqueBookingId);
        assertNotNull(retrievedBookingRequest);

        List<BookingRequest> allbookingByClient = BRRepository.findAllByRequestedBy_Id(client.getId(), Sort.by(Sort.Direction.ASC, "dateCreated"));
        assertEquals(1, allbookingByClient.size());
    }
}
