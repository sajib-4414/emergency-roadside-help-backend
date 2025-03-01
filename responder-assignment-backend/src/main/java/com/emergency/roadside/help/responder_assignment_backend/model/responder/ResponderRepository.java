package com.emergency.roadside.help.responder_assignment_backend.model.responder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponderRepository extends JpaRepository<Responder, Long> {
    // Custom queries if necessary
    Optional<Responder> findByUserId(Long userId);
}