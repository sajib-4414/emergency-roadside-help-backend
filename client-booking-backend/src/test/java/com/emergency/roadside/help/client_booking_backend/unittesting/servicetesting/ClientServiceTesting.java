package com.emergency.roadside.help.client_booking_backend.unittesting.servicetesting;

import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.ProfileDTO;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.services.client.ClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTesting {

    private User mockUser;
    private Client mockClient;

    @Mock
    ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

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
    public void testGetProfile(){

        when(clientRepository.findByUser(mockUser)).thenReturn(Optional.ofNullable(mockClient));
        ProfileDTO profileDTO = clientService.getProfile(mockUser.getId());
        assertNotNull(profileDTO);
        assertEquals(profileDTO.getEmail(), mockUser.getEmail());
        assertEquals(profileDTO.getName(), mockClient.getName());
    }
}
