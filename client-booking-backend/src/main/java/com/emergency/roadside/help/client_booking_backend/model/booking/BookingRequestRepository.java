package com.emergency.roadside.help.client_booking_backend.model.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findAllByRequestedBy_Id(Long clientId);
}
