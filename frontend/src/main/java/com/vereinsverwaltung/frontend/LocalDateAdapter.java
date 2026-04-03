package com.vereinsverwaltung.frontend;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        return LocalDate.parse(json.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(date.toString());
    }
}