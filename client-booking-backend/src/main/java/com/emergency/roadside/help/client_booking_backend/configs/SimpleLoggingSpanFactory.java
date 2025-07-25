package com.emergency.roadside.help.client_booking_backend.configs;

import org.axonframework.messaging.Message;
import org.axonframework.tracing.Span;
import org.axonframework.tracing.SpanAttributesProvider;
import org.axonframework.tracing.SpanFactory;
import org.axonframework.tracing.SpanScope;

import java.util.function.Supplier;

public class SimpleLoggingSpanFactory implements SpanFactory {

    @Override
    public Span createRootTrace(Supplier<String> operationNameSupplier) {
        String name = operationNameSupplier.get();
        System.out.println("[TRACE START] " + name + " (root)");
        return new SimpleSpan(name, true);
    }

    @Override
    public <M extends Message<?>> M propagateContext(M message) {
        // Not tracing context propagation here
        return message;
    }

    @Override
    public Span createInternalSpan(Supplier<String> operationNameSupplier) {
        String name = operationNameSupplier.get();
        System.out.println("[SPAN START] " + name + " (internal)");
        return new SimpleSpan(name, false);
    }

    @Override
    public Span createInternalSpan(Supplier<String> operationNameSupplier, Message<?> message) {
        String name = operationNameSupplier.get();
        System.out.println("[SPAN START] " + name + " (internal) for message " +
                (message != null ? message.getIdentifier() : "null"));
        return new SimpleSpan(name, false);
    }

    @Override
    public void registerSpanAttributeProvider(SpanAttributesProvider provider) {
        // No-op for simple logging implementation
    }

    @Override
    public Span createHandlerSpan(Supplier<String> operationNameSupplier, Message<?> parentMessage,
                                  boolean isChildTrace, Message<?>... linkedParents) {
        String name = operationNameSupplier.get();
        System.out.println("[HANDLER START] " + name +
                (isChildTrace ? " (child trace)" : "") + " for message " +
                (parentMessage != null ? parentMessage.getIdentifier() : "null"));
        return new SimpleSpan(name, false);
    }

    @Override
    public Span createDispatchSpan(Supplier<String> operationNameSupplier, Message<?> parentMessage,
                                   Message<?>... linkedParents) {
        String name = operationNameSupplier.get();
        System.out.println("[DISPATCH START] " + name + " from " +
                (parentMessage != null ? parentMessage.getIdentifier() : "null"));
        return new SimpleSpan(name, false);
    }

    private static class SimpleSpan implements Span {
        private final String name;
        private final boolean root;
        private boolean started = false;

        SimpleSpan(String name, boolean root) {
            this.name = name;
            this.root = root;
        }

        @Override
        public Span start() {
            if (!started) {
                started = true;
                System.out.println("[SPAN STARTED] " + name + (root ? " (root)" : ""));
            }
            return this;
        }

        @Override
        public SpanScope makeCurrent() {
            // No-op scope for simple logging implementation
            return () -> {
                // Cleanup when scope is closed (if needed)
            };
        }

        @Override
        public void end() {
            if (started) {
                System.out.println("[SPAN END] " + name + (root ? " (root)" : ""));
            }
        }

        @Override
        public Span recordException(Throwable t) {
            System.out.println("[SPAN EXCEPTION] " + name + ": " + t.getMessage());
            return this; // Return this for method chaining
        }
    }
}