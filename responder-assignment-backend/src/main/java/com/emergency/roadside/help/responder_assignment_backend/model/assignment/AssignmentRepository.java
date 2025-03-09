package com.emergency.roadside.help.responder_assignment_backend.model.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    Assignment findByBookingId(String bookingId);
}
