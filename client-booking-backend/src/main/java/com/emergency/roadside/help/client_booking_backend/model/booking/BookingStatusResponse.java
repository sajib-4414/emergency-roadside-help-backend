package com.emergency.roadside.help.client_booking_backend.model.booking;

import com.emergency.roadside.help.client_booking_backend.model.client.ClientMiniResponse;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusResponse {
    private Long id;
    private ClientMiniResponse requestedBy;
    private LocalDateTime dateCreated;
    private BookingStatus status;
    private String description;
    private Priority priority;
    private String address;
    private ServiceType serviceType;

    public BookingStatusResponse(BookingRequest bookingRequest) {
        this.id = bookingRequest.getId();
        this.requestedBy = new ClientMiniResponse(bookingRequest.getRequestedBy());
        this.dateCreated = bookingRequest.getDateCreated();
        this.status = bookingRequest.getStatus();
        this.description = bookingRequest.getDescription();
        this.priority = bookingRequest.getPriority();
        this.address = bookingRequest.getAddress();
        this.serviceType = bookingRequest.getServiceType();
    }
}
