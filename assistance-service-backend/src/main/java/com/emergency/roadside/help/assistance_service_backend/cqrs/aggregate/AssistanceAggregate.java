package com.emergency.roadside.help.assistance_service_backend.cqrs.aggregate;

import com.emergency.roadside.help.common_module.commonmodels.AssistanceStatus;
import com.emergency.roadside.help.common_module.commonmodels.ServiceType;
import com.emergency.roadside.help.common_module.saga.commands.AssistanceCreatedEvent;
import com.emergency.roadside.help.common_module.saga.commands.CreateAssistanceCommand;
import jakarta.persistence.Column;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Aggregate
public class AssistanceAggregate {
    @AggregateIdentifier
    private String assistanceId;
    private String bookingId;
    private ServiceType serviceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AssistanceStatus status;
    private Long responderId;
    private String responderName;
    private String location;
    private LocalDateTime estimatedArrivalTime;

    @CommandHandler
    public AssistanceAggregate(CreateAssistanceCommand command){
        //do some validation wherever needed.

        if (this.assistanceId != null) {
            throw new IllegalStateException("assistanceId already exists for ID: " + this.assistanceId);
        }

        //query the db to get location, servicetype
        String uuid = UUID.randomUUID().toString();
        //if all good persist to event db that booking created
        AssistanceCreatedEvent event = AssistanceCreatedEvent
                .builder()
                .assistanceId(uuid)
                .bookingId(command.getBookingId())
                .startTime(LocalDateTime.now())
                .endTime(null)
                .location(command.getAddress())
                .status(AssistanceStatus.RESPONDER_ACCEPTED)
                .serviceType(command.getServiceType())
                .responderId(command.getResponderId())
                .responderName(command.getResponderName())
                .build();
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void onAssistanceCreatedEvent(AssistanceCreatedEvent event){
        this.bookingId = event.getBookingId();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.status = event.getStatus();
        this.serviceType = event.getServiceType();
        this.responderId = event.getResponderId();
        this.location = event.getLocation();
        this.assistanceId = event.getAssistanceId();
        this.responderName = event.getResponderName();
    }
}
