package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

public class DateJsonSerializer implements JsonSerializer<Date> {
    @Override  public JsonElement serialize(final Date src, final Type typeOfSrc,
                                            final JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime());
    }
}
