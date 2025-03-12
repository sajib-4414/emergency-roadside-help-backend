package com.emergency.roadside.help.assistance_service_backend.models.assistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssistanceRepository extends JpaRepository<Assistance,Long> {
    Optional<Assistance> findByAssistanceId(String assistanceId);
    Optional<Assistance> findByBookingId(String bookingId);
}
