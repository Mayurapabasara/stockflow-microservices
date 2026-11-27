package com.stockflow.notification.event.consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
@Component
public class EventPayloadReader {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Validator validator;
    public EventPayloadReader(Validator validator) { this.validator = validator; }
    public <T> T read(String payload, Class<T> type) {
        final T event;
        try { event = mapper.readValue(payload, type); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid event JSON", e); }
        if (event == null || !validator.validate(event).isEmpty()) throw new IllegalArgumentException("Invalid event fields");
        return event;
    }
}
