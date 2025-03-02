package com.emergency.roadside.help.responder_assignment_backend.model.responder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponderAvailabilityRepository extends JpaRepository<ResponderAvailability,Long> {
    Optional<ResponderAvailability> findByResponder(Responder responder);
}
