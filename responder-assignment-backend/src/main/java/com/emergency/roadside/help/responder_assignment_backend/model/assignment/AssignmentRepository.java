package com.emergency.roadside.help.responder_assignment_backend.model.assignment;

import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    Assignment findByBookingId(String bookingId);
    List<Assignment> findByResponder(Responder responder);
    Assignment findByAssignmentId(String id);
}
