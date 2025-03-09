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