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

