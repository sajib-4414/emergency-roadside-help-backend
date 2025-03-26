package com.emergency.roadside.help.client_booking_backend.configs;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.commandhandling.gateway.ExponentialBackOffIntervalRetryScheduler;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.messaging.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Random;

import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
@Component
/* this retry class provide custom way to defining what is transient error (will allow retry) and what is non transient error
and also in case of transient error, it will retry with exponential backoff with jitter
 */
public class CustomIntervalRetryScheduler extends ExponentialBackOffIntervalRetryScheduler {

    private Random random;

    /*
    The class ExponentialBackOffIntervalRetryScheduler does not require
    an explicit retry interval because it calculates the retry interval dynamically
     based on the backoff factor and the number of retries (which is inferred from the number of failures).

     but backoffFactor is the base interval based on the axon's ExponentialBackOffIntervalRetryScheduler implementation
     */
    public CustomIntervalRetryScheduler(
            @Value("${retry.maxCount:3}") int maxRetryCount
    ) {

        super(builder()
                .retryExecutor(Executors.newScheduledThreadPool(1))
                .backoffFactor(800L) //backoffFactor is the base interval, 800ms here

                .maxRetryCount(maxRetryCount)
        );
        random = new Random();
    }


    @Override
    protected long computeRetryInterval(CommandMessage commandMessage, RuntimeException lastFailure, List<Class<? extends Throwable>[]> failures) {
        long baseIntervalWithBackoff = super.computeRetryInterval(commandMessage, lastFailure, failures);
        double jitterFactor = 0.2; // Adjust as needed
        long jitter = (long) (random.nextDouble() * baseIntervalWithBackoff * jitterFactor);
        log.info("returning interval----");
        log.info(String.valueOf(baseIntervalWithBackoff + jitter));
        return baseIntervalWithBackoff + jitter;
    }

    //overrides the buillt in definition of what is transient error(retryable) and what is not retryable
    @Override
    protected boolean isExplicitlyNonTransient(Throwable throwable) {

        if(throwable instanceof NoHandlerForCommandException){
            log.warn("error of type NoHandlerForCommandException found, will retry");
            return false;
        }

        else if (super.isExplicitlyNonTransient(throwable)) {
            log.warn("error of type isExplicitlyNonTransient found, will NOT retry");
            return true;
        }

        //for everything else, its transient, nontransient=false
        log.warn("some other Transient found, will  retry");
        return false;
    }
}
