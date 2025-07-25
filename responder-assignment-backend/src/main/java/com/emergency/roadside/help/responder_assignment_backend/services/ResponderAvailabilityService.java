package com.emergency.roadside.help.responder_assignment_backend.services;



import com.emergency.roadside.help.common_module.commonexternal.ExternalUser;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.emergency.roadside.help.responder_assignment_backend.configs.auth.AuthHelper.getCurrentUser;

@Service
@AllArgsConstructor
public class ResponderAvailabilityService {
    private final ResponderAvailabilityRepository responderAvailabilityRepository;
    private final ResponderRepository responderRepository;
    private void validateDaysOfWeek(List<String> days) {
        List<String> validDays = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
        for (String day : days) {
            if (!validDays.contains(day.toUpperCase())) {
                throw new IllegalArgumentException("Invalid day of the week: " + day);
            }
        }
    }

    @Transactional
    public ResponderAvailability createOrUpdateResponderAvailability(ResponderAvailabilityDTO dto) {

        Responder loggedInResponder = getLoggedInResponder();
        //see if any avaiblabilit exist, then update it
        Optional<ResponderAvailability> availability = responderAvailabilityRepository.findByResponder(loggedInResponder);

        // Validate the days of the week
        validateDaysOfWeek(dto.getDayOfWeeks());

        // Convert the list of days to a comma-separated string
        String daysString = String.join(",", dto.getDayOfWeeks());

        // Parse the start and end times
        LocalTime start = LocalTime.parse(dto.getStartTime());
        LocalTime end = LocalTime.parse(dto.getEndTime());

        ResponderAvailability rs;
        if (availability.isPresent()){
            rs = availability.get();

        }
        else{
            rs = new ResponderAvailability();
        }
        rs.setResponder(getLoggedInResponder());
        rs.setDayOfWeeks(daysString);
        rs.setStartTime(java.sql.Time.valueOf(start));
        rs.setEndTime(java.sql.Time.valueOf(end));
        responderAvailabilityRepository.save(rs);
        return rs;
    }

    public Optional<ResponderAvailability> getResponderAvailability() {
        return responderAvailabilityRepository.findByResponder(getLoggedInResponder());
    }

    private Responder getLoggedInResponder(){
        ExternalUser user = getCurrentUser();
        Responder responder = responderRepository.findByUserId(user.getId()).orElseThrow(()->new ItemNotFoundException("responder not found"));
        return responder;
    }


}
