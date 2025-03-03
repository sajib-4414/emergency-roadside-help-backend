package com.emergency.roadside.help.assistance_service_backend.models.assistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssistanceItemRepository extends JpaRepository<AssistanceItem, Long> {

    List<AssistanceItem> findAllByAssistance_Id(Long AssistanceId);

}
