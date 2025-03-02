package com.emergency.roadside.help.responder_assignment_backend.model.respondersupport;

import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponderServiceRepository extends JpaRepository<ResponderService, Long> {
    List<ResponderService> findAllByResponder(Responder responder);
    // Additional query methods can be defined here if needed
}
