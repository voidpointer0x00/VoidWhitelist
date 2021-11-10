package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public final class DateJsonDeserializer implements JsonDeserializer<Date> {
    @Override public Date deserialize(final JsonElement json, final Type typeOfT,
                                         final JsonDeserializationContext context)
            throws JsonParseException {
        return new Date(json.getAsLong());
    }
}
