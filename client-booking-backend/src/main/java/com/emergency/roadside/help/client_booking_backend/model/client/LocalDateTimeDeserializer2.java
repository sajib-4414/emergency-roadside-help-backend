package com.emergency.roadside.help.client_booking_backend.model.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeDeserializer2 implements JsonDeserializer< LocalDateTime > {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String dateString = json.getAsString();
        if (dateString.contains(".")) {
            dateString = dateString.substring(0, dateString.indexOf("."));
        }

        // ✅ Now parse without milliseconds
        return LocalDateTime.parse(dateString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withLocale(Locale.ENGLISH));

    }
}
