if you see error 

```declarative
Segment[0/0]
2025-08-05T10:11:23.858-06:00  WARN 2332 --- [Responder Assignment Backend] [ent-handlers]-0] o.a.e.TrackingEventProcessor             : Releasing claim on token and preparing for retry in 60s
2025-08-05T10:11:24.011-06:00  INFO 2332 --- [Responder Assignment Backend] [ent-handlers]-0] o.a.e.TrackingEventProcessor             : Released claim
2025-08-05T10:12:25.078-06:00  INFO 2332 --- [Responder Assignment Backend] [ent-handlers]-0] o.a.e.TrackingEventProcessor             : Fetched token: IndexTrackingToken{globalIndex=351} for segment: Segment[0/0]
2025-08-05T10:12:25.080-06:00  WARN 2332 --- [Responder Assignment Backend] [ent-handlers]-0] o.a.e.TrackingEventProcessor             : Releasing claim on token and preparing for retry in 60s
2025-08-05T10:12:25.233-06:00  INFO 2332 --- [Responder Assignment Backend] [ent-handlers]-0] o.a.e.TrackingEventProcessor             : Released claim
```

it means service is not able to connect to axon,
and you will also see handlers are sometime not invoked.

one development only solution is,
goto axon dashboard: localhost 8024 , then delete the default context
now go to axon folder and delete the data folder, 
now close the localhost 8024 tab and reopen, you will see it is asking to start, click start.
thats it, it resetted the axon servers all events, data everything  in the context. so like a fresh start.
