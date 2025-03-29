package com.emergency.roadside.help.client_booking_backend.services.cqrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.stream.BlockingStream;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericTrackedDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.SequencedDeadLetterProcessor;
import org.axonframework.messaging.deadletter.SequencedDeadLetterQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class CQRSMainService {
    private EventProcessingConfiguration config;
    private final EventStore eventStore;

    //just retries the earliest
    public void retryAnySequence(String processingGroup) {
        config.sequencedDeadLetterProcessor(processingGroup)
                .ifPresent(SequencedDeadLetterProcessor::processAny);
    }

//    @Async
    public void retryAllSequence(String processingGroup) {
        Optional<SequencedDeadLetterProcessor<EventMessage<?>>> optionalLetterProcessor =
                config.sequencedDeadLetterProcessor(processingGroup);
        if (!optionalLetterProcessor.isPresent()) {
            return;
        }
        SequencedDeadLetterProcessor<EventMessage<?>> letterProcessor = optionalLetterProcessor.get();

        // Retrieve all the dead lettered event sequences:
        Iterable<Iterable<DeadLetter<? extends EventMessage<?>>>> deadLetterSequences =
                config.deadLetterQueue(processingGroup)
                        .map(SequencedDeadLetterQueue::deadLetters)
                        .orElseThrow(() -> new IllegalArgumentException("No such Processing Group"));

        // Iterate over all sequences:
        for (Iterable<DeadLetter<? extends EventMessage<?>>> sequence : deadLetterSequences) {
            Iterator<DeadLetter<? extends EventMessage<?>>> sequenceIterator = sequence.iterator();
            String firstLetterId = sequenceIterator.next()
                    .message()
                    .getIdentifier();

            // SequencedDeadLetterProcessor#process automatically retries an entire sequence.
            // Hence, we only need to filter on the first entry of the sequence:
            letterProcessor.process(deadLetter -> deadLetter.message().getIdentifier().equals(firstLetterId));

        }
    }

    @Async
    public void retryAllBookingDeadLetters() throws InterruptedException {
        Thread.sleep(5000);
        retryAllSequence("bookings");
        return ;//if we return fturue, it will make us wait, the api response, rather we return void
    }

    public List<DomainEventMessage<?>> getAllEventsForBooking(String bookingId) {
        /* Retrieve all events for a specific booking ID across aggregates */
        BlockingStream eventStream = eventStore.openStream(null);




        return eventStream
                .asStream()

                .filter(item -> {
                    GenericTrackedDomainEventMessage msg = (GenericTrackedDomainEventMessage) item;
                    try {
                        Field field = msg.getPayload().getClass().getDeclaredField("bookingId");
                        field.setAccessible(true); // Make the field accessible if it's private
                        Object bookingIdValue = field.get(msg.getPayload());
                        return bookingIdValue != null && bookingIdValue.equals(bookingId);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        log.error("conversion failed...{}",e.getMessage());
                        return false;
                    }

                })
                .toList();
    }
}
