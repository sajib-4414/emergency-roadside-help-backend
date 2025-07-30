package com.emergency.roadside.help.client_booking_backend.unittesting.servicetesting;

import com.emergency.roadside.help.client_booking_backend.configs.auth.JWTService;
import com.emergency.roadside.help.client_booking_backend.model.client.*;
import com.emergency.roadside.help.client_booking_backend.services.client.AuthenticationService;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.BadDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static io.lettuce.core.ShutdownArgs.Builder.save;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//This tells JUnit 5 to use Mockito's extension to enable Mockito annotations like @Mock and @InjectMocks.
public class AuthenticationServiceTesting {

    @Mock
    //This means userRepository will be a mock object, and you can define its behavior using when(...).thenReturn(...).
    //it will behave how you define it
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    //This creates an instance of the class and injects the mocks into it.
    //This means authenticationService will be created, and any dependencies (like UserRepository) will be injected
    // using the mocks you've defined.
    //thats why there is userRepository, password Encoder, jwtSErvice on top of this as mock, otherwise
    //u will see error that these things are null inside the authetnication service
    private AuthenticationService authenticationService;

    private User user;
    final String username = "test1";
    final String password = "12345";
    final String email = "test1@example.com";

    @BeforeEach
    void setUp(){


        user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }

    @Test
    public void registerUserOnlyMethodTest(){
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());


        RegisterRequest registerRequest = RegisterRequest
                .builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        AuthResponse authResponse = authenticationService.registerUserOnly(registerRequest);

        //verify during registerUserOnly method call, findByUsername method of userRepository was called 1 time exactly.
        verify(userRepository,times(1)).findByUsername(user.getUsername());

        verify(userRepository,times(1)).save(any(User.class));

        assertEquals(authResponse.getUser().getUsername(), username);
        assertEquals(authResponse.getUser().getEmail(), email);
    }

    @Test
    public void registerRegularMethodTest(){
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());


        RegisterRequest registerRequest = RegisterRequest
                .builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        AuthResponse authResponse = authenticationService.register(registerRequest);

        //verify during registerUserOnly method call, findByUsername method of userRepository was called 1 time exactly.
        verify(userRepository,times(1)).findByUsername(user.getUsername());

        verify(userRepository,times(1)).save(any(User.class));

        assertEquals(authResponse.getUser().getUsername(), username);
        assertEquals(authResponse.getUser().getEmail(), email);


        //this wont work as nothing is saved to database
//        Optional<User> dbUser = userRepository.findByUsername(username);
//        assertTrue(dbUser.isPresent());
//        //check if a valid client was also created
//        assertNotNull(clientRepository.findByUser(dbUser.get()));

        //instead we check if client save method was called
        verify(clientRepository,times(1)).save(any(Client.class));

    }

    @Test
    public void registerUserWithAlreadyExistsEmailFail(){
        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        RegisterRequest registerRequest = RegisterRequest
                .builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        assertThrows(BadDataException.class, ()->{
            authenticationService.registerUserOnly(registerRequest);
        });
        verify(userRepository, times(0)).save(any(User.class));

    }

}
