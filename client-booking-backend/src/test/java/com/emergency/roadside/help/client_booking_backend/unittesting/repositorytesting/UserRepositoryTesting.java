package com.emergency.roadside.help.client_booking_backend.unittesting.repositorytesting;

import com.emergency.roadside.help.client_booking_backend.model.client.User;
import com.emergency.roadside.help.client_booking_backend.model.client.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest //->this helps setup a h2 database and mock the userrepostiory. but somehow h2 wasnt the database where
//migration ran and picked up by user repository, so i used active profile test, that for sure overrides the database
//config and ensured h2 database
@ActiveProfiles("test")
public class UserRepositoryTesting {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Rollback
    public void testCreateUser(){
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

        // Assert that the retrieved user id is not null
        assertNotNull(saveduser.getUsername());

        // Assert that the retrieved user id is not null
        assertEquals(username, saveduser.getUsername());

        assertEquals(email, saveduser.getEmail());
    }

}
