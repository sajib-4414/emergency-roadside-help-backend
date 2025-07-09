## To trace axon cqrs:
**axon extension tracing is deprecated and uses opentracing protocol**: there is a way to use axon extension tracing dependency(org.axonframework.extensions.tracing) in conjuction with jaeger web dependency mentioned in some axon doc, 
but that one uses open tracing standard, which is being deprecated, only support traces, but open telemetry supports logs, traces,
matrices. open telemtry has inbuilt support for all the things that opentracing had. it is suggested to upgrade from there to open
telemetry. the below is one i am talking about deprecated
```xml
<dependency>
  <groupId>org.axonframework.extensions.tracing</groupId>
  <artifactId>axon-tracing-spring-boot-starter</artifactId>
  <version>4.4</version>
</dependency>

<dependency>
  <groupId>io.opentracing.contrib</groupId>
  <artifactId>opentracing-spring-jaeger-web-starter</artifactId>
  <version>3.2.2</version>
</dependency>
```
### Tutorial of using open telemetry the right way

better way is to use open telemtry. now to use open telemtry to trace axon cqrs flow, u need to have the io.opentelemetry.instrumentation
must as it is mentioned on the Normal open telemtry integration with spring boot in open telemtry doc, and u also need 
something for axon which is axon-tracing-opentelemetry. this one works in collaboration with the io.opentelemetry.instrumentation.
u also need this config in the application yml
```yml
otel:
  exporter:
    otlp:
      endpoint: http://localhost:4317
      protocol: grpc
  metrics:
    exporter: none #this is to stop sending metrics, as it will fail, jaeger cannot handle metrics.
  logs:
    exporter: none #this is to stop sending logs, as it will fail, jaeger cannot handle logs.
```

often jaeger accepts traffic in both 4317 grpc and 4318 protobuf, so you can use either configuration. use windows/linux commands to see which port is being listened.

beside this u also need to give a spanfactory implementation  if you don't configure a SpanFactory, 
Axon defaults to this factory—meaning no spans will ever be created or traced NoOpSpanFactory.

With the simple logging span factory i created, it just prints to terminal. if u use this your span traces wont be sent anywhere but will be
just printed.  Most likely you will need a OpenTelemetrySpanFactory to send traces to open telemetry backend like jaeger. 
ok OpenTelemetrySpanFactory worked, sometime chatgpt and other AI says i need some other dependency, but not, the axon tracing open telemetry is all what i needed, maybe for earleier versions i needed some extra dpeenecy but not anymore.

open telemtry doc says u can use the add a dependencyManagement like this, however i saw even just including as normal dependency also works.
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.opentelemetry.instrumentation</groupId>
      <artifactId>opentelemetry-instrumentation-bom</artifactId>
      <version>2.17.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

you will also see an open telemetry api dependency, as of jul2025, u dont need it. if u use the io.opentelemetry.instrumentation, that now
already has the open telemetry api dependency. that API can be used to custom spans, parent spans etc.

there is also open telemetry java agent, this is the best way(but cumbersome!!) way as per open telemtry doc. I will not use it.

u will also see jaeger needs to be passed some parameters such as otlp enabled true something like that, but if u run the jaeger windows
exe file, these parameters including port mapping are already done. 

## Errors and how to solve:
**Failed to export logs**:
u can see an error in terminal that
[alhost:4318/...] i.o.exporter.internal.http.HttpExporter  : Failed to export logs. Server responded with HTTP status code 404. Error message: Unable to parse response body, HTTP status message: Not Found

Jaeger only supports trace data, not metrics or logs via OTLP

That’s why your log export attempts hit a 404 — the endpoint exists but isn’t implemented.
What OTLP Supports
Signal Type	OTLP Support	Notes
Traces	✅ Yes	Fully supported and widely adopted.
Metrics	✅ Yes	Supported with growing adoption.
Logs	✅ Yes	Supported via OTLP/HTTP and OTLP/gRPC.

What Jaeger Supports (and Doesn’t)
Signal Type	Jaeger Support	Notes
Traces	✅ Yes	Jaeger’s core functionality.
Metrics	❌ No	Not supported natively.
Logs	❌ No	Not supported at all.

as per chatgpt it can be a solution to turn off the feature to send logs and matrices at all
```yml
otel:
    logs:
        exporter: none
```
to turn off error message that metrics cannt be sent to (jaeer backend), to solve it pass it in command argument/env variables.
in intellije pass it in the edit configurations-> environment variables
OTEL_METRICS_EXPORTER=none