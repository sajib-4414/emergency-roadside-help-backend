package com.emergency.roadside.help.client_booking_backend.model.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //this is a method we declared, and jpa will automatically create the method as followed the convention
    //we dont need implementation
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}