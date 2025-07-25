package com.emergency.roadside.help.responder_assignment_backend.model.assignment;

import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    Assignment findByBookingId(String bookingId);
    List<Assignment> findAllByResponderOrderByStartTimeAsc(Responder responder);
    Assignment findByAssignmentId(String id);
    Optional<Assignment> findByAssignmentIdAndBookingId(String assignmentId, String bookingId);
}
