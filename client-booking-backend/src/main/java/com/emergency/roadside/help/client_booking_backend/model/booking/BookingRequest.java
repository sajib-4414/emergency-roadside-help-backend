package com.emergency.roadside.help.client_booking_backend.model.booking;


import com.emergency.roadside.help.client_booking_backend.model.client.Client;
import com.emergency.roadside.help.client_booking_backend.model.vehicle.Vehicle;
import com.emergency.roadside.help.common_module.commonmodels.BaseEntity;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "booking_request")
@Data
public class BookingRequest extends BaseEntity {

    @Column(name = "booking_id", unique = true)
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client requestedBy;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, columnDefinition = "varchar(50) default 'NOW'")
    private Priority priority;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Override
    public String toString() {
        return "BookingRequest(id=" + getId() +
                ", requestedBy=" + (requestedBy != null ? requestedBy.getId() : "null") +
                ", dateCreated=" + dateCreated +
                ", status=" + status +
                ", vehicle=" + (vehicle != null ? vehicle.getId() : "null") +
                ", description=" + description +
                ", priority=" + priority +
                ", address=" + address +
                ", serviceType=" + serviceType + ")";
    }

}

