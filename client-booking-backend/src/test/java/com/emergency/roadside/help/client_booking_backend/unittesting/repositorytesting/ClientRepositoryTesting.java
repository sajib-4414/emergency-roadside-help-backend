package com.emergency.roadside.help.client_booking_backend.unittesting.repositorytesting;

import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.client.ClientRepository;
import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.client.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ClientRepositoryTesting {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    @Rollback
    public void testCreateClient(){
        String username = "test1";
        String password = "12345";
        String email = "test1@example.com";

        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        User saveduser = userRepository.save(user);
        assertNotNull(saveduser);

        // Assert that the retrieved user id is not null
        assertNotNull(saveduser.getId());

        assertTrue(userRepository.findById(saveduser.getId()).isPresent());

        //now lets create client

        final String clientName = "testclient";
        final String phoneNum = "testclient";
        Client client = Client
                .builder()
                .user(user)
                .name(clientName)
                .build();
        Client savedClient = clientRepository.save(client);

        assertNotNull(savedClient);
        Optional<Client> retrievedClient = clientRepository.findByUser(user);
        assertTrue(retrievedClient.isPresent());
        assertEquals(clientName, retrievedClient.get().getName());
    }
}
