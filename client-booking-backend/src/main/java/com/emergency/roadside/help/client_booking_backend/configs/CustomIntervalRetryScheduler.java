package com.emergency.roadside.help.client_booking_backend.configs;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.messaging.MessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Slf4j
@Component
public class CustomIntervalRetryScheduler extends IntervalRetryScheduler {
    public CustomIntervalRetryScheduler(
            @Value("${retry.interval:5}") int retryInterval,
            @Value("${retry.maxCount:3}") int maxRetryCount
    ) {
        super(builder()
                .retryExecutor(Executors.newScheduledThreadPool(1))
                .retryInterval(retryInterval)
                .maxRetryCount(maxRetryCount)
        );
    }

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
