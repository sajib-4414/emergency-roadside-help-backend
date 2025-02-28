package com.emergency.roadside.help.responder_assignment_backend.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponderRepository extends JpaRepository<Responder, Long> {
    // Custom queries if necessary
}