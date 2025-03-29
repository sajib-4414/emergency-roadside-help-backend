package com.emergency.roadside.help.client_booking_backend.configs;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.Decisions;
import org.axonframework.messaging.deadletter.EnqueueDecision;
import org.axonframework.messaging.deadletter.EnqueuePolicy;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class RetryConstrainedEnqueuePolicy implements EnqueuePolicy<EventMessage<?>> {
    private static final int MAX_RETRIES = 3;

    //when we retry with the trigerred,(ex in an api call given), if the message is now successfully processed
    //you will see log that these deadletters are evicted as event process was successful
    @Override
    public EnqueueDecision<EventMessage<?>> decide(DeadLetter<? extends EventMessage<?>> letter, Throwable cause) {
        int retries = (int) letter.diagnostics().getOrDefault("retries", 0);
        log.info("retry count is {}",String.valueOf(retries));

        if(retries == 0){
            //event did not exist on dlq before, we are adding for first time,
            //although i have seen reenqueue, and enqueue works similarly
            //0 means, we are keeping the event such as, this event is not retried at all from the DLQ
            //when we retry events, we will requeue with a incremented retry count.
            return Decisions.enqueue(cause, l -> l.diagnostics().and("retries", retries + 1));
            //this retry gets stored on the db somehow with the metadata, i checked with server restarts, i can still see retry count persist
        }
        else if (retries >0 && retries < MAX_RETRIES) {
            // Requeue the event with an incremented retry count
            return Decisions.requeue(cause, l -> l.diagnostics().and("retries", retries + 1));
        }
        else { //max retry surpassed, we can either evict it from DLQ or do something else like Alert, to cleanup
            return Decisions.evict();
        }

    }
}
