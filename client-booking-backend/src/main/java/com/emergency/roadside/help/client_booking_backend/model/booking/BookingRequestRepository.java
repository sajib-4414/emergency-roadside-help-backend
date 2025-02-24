package com.emergency.roadside.help.client_booking_backend.model.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
}
