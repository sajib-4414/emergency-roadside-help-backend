cannot query the read database during command handling for any verification,
you should only query event database like axon db.
if you need something like if client has any active bookings reject creating a booking,
then have a client aggregate, that can keep track of client's active bookings.
query that in the command handler
```java

@Aggregate
public class ClientBookingsAggregate {
    @AggregateIdentifier
    private Long clientId;
    private Set<Long> activeBookingIds = new HashSet<>();
    
    @CommandHandler
    public ClientBookingsAggregate(RegisterClientBookingCommand command) {
        // Check if client already has active bookings
        if (!activeBookingIds.isEmpty()) {
            throw new ClientHasActiveBookingsException("Client already has active bookings");
        }
        
        apply(new ClientBookingRegisteredEvent(command.getClientId(), command.getBookingId()));
    }
```
cannot use database long ids for aggregate idenfier
because there is a lag of writing to read database, i mean first it will be written to eventstoredb.
so here is eventual consistency. so with Long, database id often we cannto work, especially
for the field that is being used for aggreagate.
in this case, for booking i cannt use the long id anymore.

## Handling Errors from Command Handlers
When a command handler throws an exception, you have two options for handling it:

Option 1: Catch the Exception in the API Controller
If the command is sent directly from an API controller, you can catch the exception there and return an appropriate error response to the client.

Option 2: Catch the Exception in the Saga
If the command is sent from a Saga, you should catch the exception in the Saga and trigger compensating actions.

## Reset axon DB
goto axon dashboard->cotnext->delete context(with data).
now u will see spring boot cannot connect anymore, so create a context again.
for free tier the cotnext name is always default

## if you are getting sequence error
this happens if you use a field as aggregate id, and you keep creating commandss with 
same aggregate id. for example I have client aggregate, where client id is the target aggregate field.
so when i was requesting for booking with a single client, then axon was seeing its the same aggregate(based on id),
and some previous events, commands were having issues, not properly handled. so axon was expecting the previous sequence id, up until 
same aggregate is processed, but due to error, some events were missed, so i was getting sequence error.

One way is to just delete the context(event store), then everyting in the 3 tables used by axon
assocition_value_entry, saga_entryu, token_entry. then everything was resetted.
the sql tables contains some pointer to what was last processed sequence number or something.
if you mess up in development, u can delete those with context, and restart again everything.

But the best approach is, from axon community comments, dont design system like this, where
u will keep getting aggregates but with same aggreagate id. having a new unique aggreagate id
(like booking id, where each booking is unique) is the best usage of this aggreagate.

**More comments from claude**
The problem you described is a common issue when using a non-unique identifier as the aggregate ID. In your case, using the client ID means all operations for that client affect the same aggregate instance. This creates problems because:

- Multiple concurrent commands targeting the same client ID compete for the same aggregate
- If errors occur during event processing, the expected sequence gets disrupted
- The aggregate's event store gets polluted with unrelated events (bookings) that just happen to share the same client ID

Regarding the token: The token_entry table stores tracking tokens for event processors. These tokens track which events have been processed by each processor, 
essentially acting as offsets or bookmarks in the event stream. They help Axon know where to resume processing after a restart.

## if you are getting xstream serialization error after using command gateway
use transient and autowired command gateway. it happened in the saga class, using this solved the problem

## Data duplication in microservice
so first i designed with as minimal data duplication as possible, for example assignment
in the assignment service, and booking in the booking service has many data common like address
client, booking creation date. i can copy actually all data, but there will be lot of duplicaiton.
i kept booking id in the assignment service, assignment model, thought that frotnend, if need
further info, will call bookign service. however later i saw that frotnend for some info might need
to call backend a lot, i mean everytime calling assignment, then again booking. so i duplicated some
more fields, like which address was it, who is the client.
It did happen when i started to do the frotnend.
I can tell this in storytelling that when designing mciroservices with event driven, i faced this issue
a lot, i can tlel in the context of other projects like ticket booking, elearning

## individual microservice e aggreagate fields must be string
If we are in booking microservice, there will be booking aggraate, wehre aggregate identifier
is bookingId, this should highly be recommended as string. 
one reason i found is, till the event is not being persisted as database we dont have a
Long/integer primary key id, but once the booking is creatd in eventstore, we should assume that
our write database has the event, and asyncronously the read database will also have the booking.
so what we do is, when we write asyncrhonously we still use the same booking id(string) to find a booking.
that way event's copy of booking and read model's copy of booking is same.

same in assignment mciroservice, the assignment id should be string,
forget about db id, that will be still there.

## command hanlders are inherently transactional
```declarative
@CommandHandler
    public AssignmentAggregate(FindResponderCommand command, ResponderRepository responderRepository){

        try{
            //validate the command before storing it in eventstore, which is primary write DB
            //check things like if status is good, priority is good, if applicable
            // Check if the aggregate already exists, handle it in saga
            //this check
            if(this.assignStatus == AssignStatus.ASSIGNED){
                throw new IllegalStateException("Order already assigned");
            }
            //read the database to see who is available, see the avaiablity, city etc.
            //for simplicity lets assume we give to the first driver, and we keep assigning, we are not checking if someone is free

            Responder responder = responderRepository.findById(1L).orElseThrow(()->new ItemNotFoundException("Responder not found"));
            String uniqueBookingId = UUID.randomUUID().toString();

            //if all good persist to event db that booking created
            ResponderReservedAndNotifiedEvent event = ResponderReservedAndNotifiedEvent
                    .builder()
                    .bookingId(command.getBookingId())
                    .description(command.getDescription())
                    .priority(command.getPriority())
                    .assignStatus(AssignStatus.RESERVED)
                    .startTime(LocalDateTime.now())
                    .endTime(null)
                    .responderId(responder.getId())
                    .serviceType(command.getServiceType())
                    .build();
            AggregateLifecycle.apply(event);
            log.info("just dispatched the ResponderReservedAndNotifiedEvent as soon as i reserved a responder in the responder service");
        } catch (ItemNotFoundException e) {
            log.error("driver not found in command handler of responder microservice");
            log.info("sending event that booking cancelled");
            ResponderNotFoundEvent responderNotFoundEvent = ResponderNotFoundEvent
                    .builder()
                    .bookingId(command.getBookingId())
                    .build();
            AggregateLifecycle.apply(responderNotFoundEvent);

        }
```
this is transactional


### Aggregate not found error: how to solve

for any aggreagate in the microservice often u need to make the first command handler constructor
a cosntructor returning the aggregate, sometime if you define first command with other name
with createpolicy `@CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)`, works
but often does not.

without this u will see error, aggregate does not exist.
so what happens is the first command handlder creates the aggregator, this creats the aggreagator with
sequence number zero(this sequence is indside the event db, only you can query with axon api endpoint to see the sequnece number)
then when you add more command hanlder, each command handler is applying events, and with those u
update the aggtreagate with event sourcing handler, they incrtement the sequence number, this is to track how many
events happened with this aggregate.

## event r command er field gula bar bar update kora lage
next microservice er ki lagbe, but saga handler ki korbe, ki info lagbe etar upore vitti kore
bar bar update kora lage event, command. eta ekta common challange


### saga korle koi theke koi, fail hoile koi theke koi eta onek complex
ejonno diagram eke dhore dhore age piche jaya debug kora lage

## individual mciroservice's aggegate class's identifier must be different from other.
although they are relating to same booking flow, but aggreagate is different in each microservice.
otherwise you will get error that sequence number error, expecting a forward number(like 1 but gttig 0)
it means the second microservice is getting some same id as aggregate field, like another microservice(who is comming this second microservice)
when it gets the same id, it tries to construct a aggreater as new with sequence starting from zero.
but axon is seeing aggregate already exists (created by other microservice)  where sequence is zero, now sequence can be only incrememnted to 1,2,3 as more events come and event source handler is updating the aggregate

for example
for the same booking
in booking microservice: booking aggregate(aggegate identifier booking id)
in assignment mciroservice: assignment aggregat(aggegate identifier assignment id)
in assistance microservice: assistance aggregat(aggegate identifier assistance id)


## idempotency is the key
dekhsilam tracking event processor er karone, kisu event retry hoto, r event gula jodi erkm hoi je
createbooking, jekhane db booking create kora hoto, dekhsi je duplicate data dhuke jeto or
jodi amar unique booking id thake, taile error khaitam je duplicate data dhuktese. then idempotency dhukai
je jodi ei jinis create hoye thake taile skip this.

## what is subscriptions, types of subscriptions and event processors

In event sourcing systems, subscriptions are mechanisms that allow clients or services to receive notifications about new events as they occur in the event store.
Here's what subscriptions typically provide:

**Actually ami je event listener diye event gula dekhte pari, listen korte pari, etai subscription**

Real-time updates: Services can subscribe to specific event types or streams and receive events as they're appended to the event store.
Catch-up capability: When a service starts or reconnects after downtime, it can receive all events it missed since its last checkpoint.
Filtering options: Most subscription systems let you filter by event type, stream, or other criteria to receive only relevant events.
Durability guarantees: Subscriptions often provide at-least-once delivery semantics to ensure events are processed even through failures.

this is a sample subscription, which is just event handler
```java
// 2. Create a subscription with an event processor for the read model
@Component
public class ProductInventoryProjection {
    private final ProductInventoryRepository repository;
    
    @Autowired
    public ProductInventoryProjection(ProductInventoryRepository repository) {
        this.repository = repository;
    }
    
    @EventHandler
    public void on(ProductCreatedEvent event) {
        repository.save(new ProductInventory(
            event.getProductId(),
            event.getName(),
            event.getInitialStock()
        ));
    }
    
    @EventHandler
    public void on(StockAdjustedEvent event) {
        repository.findById(event.getProductId())
            .ifPresent(inventory -> {
                inventory.setStock(inventory.getStock() + event.getAdjustment());
                repository.save(inventory);
            });
    }
}
```
Axon provides two types of event processors for subscriptions:

Subscribing processors: Real-time, direct subscription to the event bus
Tracking processors: Persistent subscription that tracks position and can catch up after downtime

Subscribing Processors
Subscribing processors receive events immediately as they're published, working in the same thread that publishes the event.
Key characteristics:

Real-time event processing (synchronous)
No persistence of processing state
Cannot recover missed events after restart
Simple, with minimal configuration

```java
@Configuration
public class EventProcessingConfig {
    @Bean
    public EventProcessingConfigurer subscribingProcessorConfig(EventProcessingConfigurer configurer) {
        // Configure "notificationProcessor" as a subscribing processor
        return configurer.registerSubscribingEventProcessor("notificationProcessor");
    }
}

// A component using the subscribing processor
@Component
@ProcessingGroup("notificationProcessor")
public class NotificationService {
    
    private final EmailSender emailSender;
    
    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    @EventHandler
    public void on(OrderPlacedEvent event) {
        // This executes in the same thread that published the event
        // and happens immediately after the event is published
        emailSender.sendOrderConfirmation(event.getCustomerEmail(), event.getOrderId());
    }
}
```
Use case: Ideal for notifications, validations, or other operations that should happen immediately and don't need to be durable or recoverable.

Tracking Processors
Tracking processors poll for events from the event store, track their position, and can resume processing from where they left off after a restart.
**If you dont override or specify the event processor, by default axon uses tracking processor, which is more reliable**
Key characteristics:

Asynchronous event processing
Maintains processing position (tracks which events have been processed)
Can recover and catch up after downtime
Supports parallel processing
More complex, with configurable options


```java
@Configuration
public class EventProcessingConfig {
    @Bean
    public EventProcessingConfigurer trackingProcessorConfig(EventProcessingConfigurer configurer) {
        return configurer.registerTrackingEventProcessor("inventoryProcessor",
                // Configure with 4 threads for parallel processing
                configuration -> TrackingEventProcessorConfiguration.forParallelProcessing(4)
                        .andBatchSize(100));
    }
}

// A component using the tracking processor
@Component
@ProcessingGroup("inventoryProcessor")
public class InventoryProjection {
    
    private final InventoryRepository repository;
    
    public InventoryProjection(InventoryRepository repository) {
        this.repository = repository;
    }
    
    @EventHandler
    public void on(ProductCreatedEvent event) {
        // This runs in a separate thread, possibly later than when the event was published
        repository.save(new InventoryItem(event.getProductId(), event.getInitialStock()));
    }
    
    @EventHandler
    public void on(StockAdjustedEvent event) {
        // If the service restarts, it will pick up where it left off
        // and process all events it missed during downtime
        repository.findById(event.getProductId())
                .ifPresent(item -> {
                    item.setStock(item.getStock() + event.getAdjustment());
                    repository.save(item);
                });
    }
}
```
Use case: Ideal for building read models, maintaining projections, or any process that needs durability and the ability to recover after failures.



## Challange-> data lost due to event propagation failure
i see some events were lost somehow, and due to that one microservice has some data of a flow(say like booking), but other microservcie does not have.
istributed systems like microservices architecture using EDA (Event-Driven Architecture) with Axon Framework, data inconsistency due to lost events is a common challenge. 

Recovery Options
* You can try to replay -axon provides an endpoint to set the tracking processor's sequence id to the back,
so axon will replay from that sequence. It involves altertign the sequence with axon's built in method

In Axon, tracking event processors keep track of which events they've processed using tracking tokens. You reset these tokens to force reprocessing:
As events are replayed, the event handlers in your microservice process them, rebuilding the aggregates and projections.

```java
@Component
public class EventReplayService {
    private final EventProcessingConfiguration eventProcessingConfiguration;
    
    @Autowired
    public EventReplayService(EventProcessingConfiguration eventProcessingConfiguration) {
        this.eventProcessingConfiguration = eventProcessingConfiguration;
    }
    
    public void replayEvents(String processorName) {
        eventProcessingConfiguration.resetTokens(processorName);
    }
    
    public void replayEventsFromDate(String processorName, Instant fromDate) {
        eventProcessingConfiguration.resetTokens(processorName, 
            streamableMessageSource -> streamableMessageSource.createTokenAt(fromDate));
    }
}
```
However replaying all events can do unintended consequence like people get email that order
conrifmed, order delivered again, for orders which they already received. thats why axon provides a 
`isReplayed` variable in event handlers to know if right now event is being replayed, you need to have 
logic to make them idempotentn now, so that event replaying dos not cause any side effects.


more suggestions by claude
* Reconciliation Process: Implement a scheduled reconciliation job that compares data between microservices and identifies discrepancies.
* Compensating Transactions: Create new events that correct the inconsistency. For example, if a booking exists in one service but not another, create a "BookingRecoveredEvent" that the second service can process.

To prevent further claude suggested
* Guaranteed Delivery Patterns:

Use a reliable message broker with persistence and acknowledgment mechanisms
Implement the Outbox Pattern to ensure events are sent even if initial publication fails
* Idempotent Event Handlers: Make your event handlers idempotent so that receiving the same event multiple times doesn't cause issues. So event can be replayed, rebuilt, anything, without causing any problems.


**MY NEXT TASKs** 
* is to have an api to list down all events by bookingId 
* make an an api to rerun all events of an aggregate. i saw for some booking there is no aggregate or events in other microservices.
i will make an endpoint to run all the events of a certain booking aggregate to trigger event and aggregate creeation on other services.
* will all the commands and event handlers idempotent, while doing this, this is also a practice. This is a great interview story to tell that i debugged event being lost, i replayed and rebuilt the events and aggregates in other microservices. i caught that some events were lost by querying eventstore with query all events by bookingId, from there i saw that some events were missed.
* i guess i can run a background job to test this automatically, also i guess i can use a tracing tool to find this. have to see these, especially tracing.
* dead letter configuration: https://docs.axoniq.io/axon-framework-reference/4.11/events/event-processors/dead-letter-queue/
axon does not do dead letter for commands,saga handlers, aggregates,it only do for event handlers which are mostly used for projection.
there is another way of dead letter handling: htere is a handler axon gives, try that out too, that is for manual handling of dead letter to some own defined table: https://discuss.axoniq.io/t/dead-letter-error-handling/5769/3
* (**done**) update the retry mechanism with axon built in, axon built in - only retry with exponential backoff for transient errros defined in axon dictionary, all others it treats as non retryable, have to customize if i want
* add open telemetry+jaeger+axon: somebody said axon has built in support, so just use the agent and use open telemetry
https://opentelemetry.io/docs/languages/java/getting-started/
https://docs.axoniq.io/axon-framework-reference/4.10/monitoring/tracing/?utm_source=chatgpt.com
someone in axon community says, due to built in support he only needed to add a otel service name something like this property in all microservixes, it was enough, have to check that as well.
* do the assignment update saga as well.


## SAGA error Handling
### Handling Error In Commands:
Commands are lost and saga is in orphan state without command error handling

in saga when i am sending a command to next mcirosercice, if i just do send command(which is a syntacic sugar over event )
and dont handle error with timeout, then if the other mciroservice is down and not getting a command at the moment,
then even if the other mciroservice who is supposed to receive command, wakes up, will miss this command, then the saga is failed.

Here are two problems, 
-> **commands can be not sent**: maybe the command receiver is down.

1. it can be handled by 
`commandGateway.sendAndWait(command, 15,TimeUnit.SECONDS);` this is a blocking mechansim, if there is no command
handler receiving it in 15s, you will get error in the try catch. WHile this is straightforward, but this blocks the thread.

2. a better way is to use the async handler provided in the `commandGateway.send` you can listen to command sending error from here, withot blocking thread
```
commandGateway.send(command, (commandMessage, commandResultMessage) -> {
    if (commandResultMessage.isExceptional()) {
        Throwable cause = commandResultMessage.exceptionResult();
        System.err.println("Command failed: " + cause.getMessage());
        // Log or take action based on the failure
    } else {
        System.out.println("Command processed successfully: " + commandResultMessage.getPayload());
    }
});
```
3. Another thing is sometime if commands are failed, it is ok to retry this command a few more times, we can do that
with a time delayed execution block. One approach is using CompletableFuture.delayedExecutor.
this is a raw way of doing, but see next way, thats better for axon to do a centralized config.
```
CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
        if (!commentRepository.existsById(commentId)) {
            System.err.println("Comment failed, retrying...");
            commandGateway.send(new PostCommentCommand(commentId, "Retrying comment"));
        }
    });
  //complete example is in my saga class
```
4. Actually for axon, its better to use the built in retry exector by axon, which retries for transient errrors, with a 
set maximum retry, and exponential backoff. however if i want to customize that beyond transient errors
i want to retry, then i have to customize that error handler

-> **no error in command sending, but response not coming**: To handle this, use deadline manager, and deadline handler
to have a timeout that if the next response is not coming in 15 minutes say, we do another commad inside our microservice to
cancel/compensate.


### Handling event not coming in Saga with timeout

use deadline manager and deadline handler to have timeout in saga

say if your order payment processed event is not coming in 15 minutes you have to cancel the order,
to do that, you can use deadline manager to create a deadline, and if deadline is passed and event is not coming
as response like payment response, then deadline handler will trigger a compensating command

```java
@StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        // starting a timer that, upon process  payment command, we must receive the response in 15 minutes
        deadlineId = deadlineManager.schedule(
            Duration.ofMinutes(15), 
            "paymentDeadline", 
            event.getOrderId()
        );
        
        // Send command to payment service
        commandGateway.send(new ProcessPaymentCommand(event.getOrderId(), event.getAmount()));
    }
    
//if we do get any event, then we cancel the deadline timer    
@SagaEventHandler(associationProperty = "orderId")
public void on(PaymentProcessedEvent event) {
    // Cancel the deadline since payment was successful
    deadlineManager.cancelSchedule("paymentDeadline", deadlineId);

    // Continue with order processing
    commandGateway.send(new ShipOrderCommand(event.getOrderId()));
}

//if we did not get evetn response and did not cancel the timer, this deadline handler will be invoked
@DeadlineHandler(deadlineName = "paymentDeadline")
public void handlePaymentTimeout(String orderId) {
    // Handle the timeout - compensating transaction
    commandGateway.send(new CancelOrderCommand(orderId, "Payment timeout"));
}
```

### Handling events failed.
chatgpt says,
when an event is listed to in saga, eventsourcing handler, and event handler(for db),
axon provides a dlq for the events that failed in the event handler with uncaught exception.
axon does not provide a way to move events to dlq from saga event handler and also event sourcing handler.
i have to design the system robustly to handle all events from there


i saw in axon iq discussion that, if you dont implement dlq, you can get a listener that is invoked when an event is failed, so you do your own custom intervention.
also i saw in axon video that if you imeplemetn dlq, you can take jpasequenced dlq configutaiton, which stores the failed events in the dead letter entry table in the database like this
![img_1.png](img_1.png)
you can see there is lot of events without message, these events are all the events after one events failed,
dlq implementation blocks all subsequent events from goinmg to handler and are stored here
you will be able to see they have sequence numbers
![img_2.png](img_2.png)

Also when you want an event to be in the dlq, and giving a name to a dlq is also possible in axon with enque policy.
**configuring a dlq**
![img_3.png](img_3.png)
**example of defining dlq enquing policy**
![img_4.png](img_4.png)
so enqueue,reenque works similar way i saw,
so for the first time when dlq is involved maybe call enqueue to that event is lited on the dlq
now event will not go from here to event handler automatically, you have to trigger the deadletter
processing event(most often when u fixed a bug that prevent processing an event). now event will be picked from
the dlq, if the event is now processed successfully, it will be evicted from dlq, otherwisebased on my logic
it willbe   requeued with an incremented retry count(which is stored inside metadata to the database). 
Now there culd be some events which after many retry trigger, still sitting, you can define condition to evict also.
the enqueue policy class is called everytime when you trigger a retry, so if you have some logic like
retry maximum time done or some known error, dont reenqueue it again, something like that, you can define.
but take caution, without doing anything like alert or proper managmenet dont just evict message from dlq, usually dlq is the place u will inspect, retry.


## Axon do dual write with event publishing, which introduces issue that GET call doesnt see data
This is similar to shryansh jain said, how listen to yourself dual write solution,
where instead of write to db, message is put into outbox, then from there by poller, message is published to event bus,
then from bus, this service listens and write to db. and other service also listens the event now.
now in the meantime if a get request comes before db is written, there will be no item in DB, so this delayed write issue,
he said cache can solve it.
![dual_write_delayed_write_issue.png](dual_write_delayed_write_issue.png)
https://www.youtube.com/watch?v=QaH7r4V4RmE&ab_channel=Concept%26%26Coding-byShrayansh
I also planned to solve it by writing to cache first, although for now i am just reqeusting again and again from
backedn if db item is not created.

### how to do command failure retry with interval, max retry and also exponential backoff

the below example is for retry with exponential backoff, but you can just override intervalretryschduler i think class name
that will allow to just specify the interval and max retry without complex exponential backoff

```declarative
@Bean
    public DefaultCommandGateway commandGateway(CommandBus commandBus, CustomIntervalRetryScheduler retryScheduler) {
        return DefaultCommandGateway.builder()
                .commandBus(commandBus)
                .retryScheduler(retryScheduler)
                .build();
    }
```
```declarative
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
```

### solving the dual write with delayed write problem with cache
```java
public ResponseEntity<?> createSagaBooking(@Validated @RequestBody BookingRequestDTO payload) {
   String uniqueBookingId = UUID.randomUUID().toString();
   User user = getCurrentUser();
   Client client = clientRepository.findByUser(user).orElseThrow(()->new ItemNotFoundException("client not found"));

   Vehicle vehicle;
   if(payload.getVehicleId() !=null)
      vehicle = vehicleRepository.findById(payload.getVehicleId()).orElseThrow(()->new ItemNotFoundException("vehcile not found"));
   else if(payload.getVehicle()!=null){
      Vehicle dbPayload = modelMapper.map(payload.getVehicle(), Vehicle.class);
      vehicle = vehicleService.addVehicle(dbPayload);
   }
   else{
      throw new BadDataException("vehicle data is needed");
   }
   try{
      RegisterClientBookingCommand command = RegisterClientBookingCommand.builder()
              .bookingId(uniqueBookingId)
              .clientId(client.getId())
              .status(BookingStatus.QUEUED)
              .dateCreated(LocalDateTime.now())
              .vehicleId(vehicle.getId())
              .address(payload.getAddress())
              .description(payload.getDetailDescription())
              .priority(payload.getPriority()==null? Priority.NEXT_BUSINESS_DAY: payload.getPriority()) //should default to next business day
              .serviceType(payload.getServiceType())
              .build();
      commandGateway.sendAndWait(command);

      BookingRequest dummyUnwrittenBookingRequest = new BookingRequest();
      dummyUnwrittenBookingRequest.setRequestedBy(client);
      dummyUnwrittenBookingRequest.setBookingId(command.getBookingId());
      dummyUnwrittenBookingRequest.setStatus(command.getStatus());
      dummyUnwrittenBookingRequest.setDateCreated(command.getDateCreated());
      dummyUnwrittenBookingRequest.setVehicle(vehicle);
      dummyUnwrittenBookingRequest.setAddress(command.getAddress());
      dummyUnwrittenBookingRequest.setDescription(command.getDescription());
      dummyUnwrittenBookingRequest.setPriority(command.getPriority());
      dummyUnwrittenBookingRequest.setServiceType(command.getServiceType());
      //solving the dual write problem, just writing to cache, and get booking status method will also return from cache always
      //when the booking will be created in db, we will update the cache also.
      BookingStatusResponse statusResponse = new BookingStatusResponse(dummyUnwrittenBookingRequest);
      cacheService.putBookingToCache(statusResponse);
      return ResponseEntity.ok(command);
   } catch (Exception e) {
      System.out.println("exception happened"+e.getMessage());
      System.out.println(e.getStackTrace());
      throw new BadDataException("Client already has a booking in progress");
   }


}
```

but solving the dual write problem like this created a problem, so do i just return the booking
from cache at the time of creation and maybe not yet written to DB(but in the process of writing to eventstore, then db)
or we always return from cache.
so actually in our system, i for now designed in a way that getting booking details is a high 
call API, so it is worth always returning response from cache. but for that, everytime booking is updated
we have to update cache as well. so we are doing a write through cache.

## Another lesson:
booking service sent command to responder service that find me a responder.
now responder was assigned, but as responder was not accepting, then booking saga had a deadline that
if no responder accept event is coming in 50s, then mark the booking as cancelled,
so the booking was marked cancel, but in the responder service it was staying as resevered still, 
what i need to do is, when i cancel a booking on the booking service, also need to cancel the booking assignment
on the responder service, otherwise, responder can later accept it, which is not the correct business case.
Also if that somehow happens, in booking service when such event comes, we should check if the booking has reached
any terminus state like cancelled/completed, then no further update will be applied, like responder accepted, all should be rejcted.
actually here i got a problem that to order the responder service that cancel this assignment,
i have cancelled the booking, i need the assignment id, afor the assignment service this is an existing
aggregate, to command a aggreegate handler with existing aggregate i need to provide the aggegateid(in this case assignment id)
in the command, this is different than first time creating aggregate int he responder service(as it was first time).
so i had to get an event to booking service that assignment created with reserved status but rseerved person did not accept yet,
with this, i got the assignment id. Now this also needed to be done syncrhonously,
like first i did was i threw the command to find responder command and immediately started timer 
that in 15s i must get a yes respionder accepted command, if not i will throw a cancel the reservaton command.,
in the meantime when assignment service would create assignment but in reserved state, it would send the event that
i created this assignment, and here is the assignment id, so i would have the assignment id saved, and after 15s i will use it.
but it did not work, i saw that due to parallel processing, byt th etime 15s went, i saw that i did not have the assignment id,
so i had to do this syncrhonously, so i started the timer of 15s only after i got the event that an assignment was created
with reserved state, and i updated the assignment id on the booking service.