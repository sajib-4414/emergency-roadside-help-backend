package com.emergency.roadside.help.client_booking_backend.unittesting.servicetesting;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestDTO;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequestRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleDTO;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.VehicleRepository;
import com.emergency.roadside.help.client_booking_backend.services.booking.BookingRequestService;
import com.emergency.roadside.help.client_booking_backend.services.vehicle.VehicleService;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BRServiceTests {


    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    private BookingRequestRepository BRRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BookingRequestRepository bookingRequestRepository;

    @InjectMocks
    private BookingRequestService BRService;

    private User mockUser;
    private Client mockClient;

    @BeforeEach
    public void settingAuth(){
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockClient = new Client();
        mockClient.setName("newname");
        mockClient.setUser(mockUser);
        mockClient.setId(1L);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    public void cleanUpTest(){
        SecurityContextHolder.clearContext();

    }

    @Test
    public void createBookingTest(){


//        when(vehicleRepository.save(ArgumentMatchers.<Vehicle>any()))
//                .thenAnswer(invocation -> invocation.getArgument(0));


        VehicleDTO payloadVehicle = VehicleDTO
                .builder()
                .make("Toyota")
                .model("camry")
                .plate("126asda")
                .year(2020)
                .build();
        Vehicle mockedVehicle = Vehicle
                .builder()
                .make("Toyota")
                .model("camry")
                .plate("126asda")
                .year(2020)
                .build();
        Vehicle mockSavedVehicle = Vehicle
                .builder()
                .make("Toyota")
                .model("camry")
                .plate("126asda")
                .year(2020)
                .build();
        mockSavedVehicle.setId(1L);


        BookingRequestDTO payload = new BookingRequestDTO();
        payload.setAddress("33 shaw st");
        payload.setPriority(Priority.NOW);
        payload.setServiceType(ServiceType.TOWING);
        payload.setVehicle(payloadVehicle);



        BookingRequest mockBookingRequest = new BookingRequest();
        mockBookingRequest.setAddress("33 shaw st");
        mockBookingRequest.setPriority(Priority.NOW);
        mockBookingRequest.setServiceType(ServiceType.TOWING);
        mockBookingRequest.setVehicle(mockedVehicle);
        mockBookingRequest.setId(1L);

        when(modelMapper.map(eq(payload), eq(BookingRequest.class)))
                .thenReturn(mockBookingRequest);

        when(modelMapper.map(eq(payloadVehicle), eq(Vehicle.class)))
                .thenReturn(mockedVehicle);
        when(vehicleService.addVehicle(mockedVehicle))
                .thenReturn(mockSavedVehicle);
        when(clientRepository.findByUser(mockUser))
                .thenReturn(Optional.ofNullable(mockClient));
        when(bookingRequestRepository.save(mockBookingRequest))
                .thenReturn(mockBookingRequest);

        BookingRequest br = BRService.createBooking(payload);
        assertNotNull(br);

        BRService.deleteBooking(1L);
        verify(bookingRequestRepository,times(1)).deleteById(1L);

        Long nonExistentBookingId=999L;
        doThrow(new EmptyResultDataAccessException(1))
                .when(bookingRequestRepository).deleteById(nonExistentBookingId);

        assertThrows(EmptyResultDataAccessException.class, ()->{
            BRService.deleteBooking(nonExistentBookingId);
        });
        verify(bookingRequestRepository,times(1)).deleteById(1L);
    }




}
