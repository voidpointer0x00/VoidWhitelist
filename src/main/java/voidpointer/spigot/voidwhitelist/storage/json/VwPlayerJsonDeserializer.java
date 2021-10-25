package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.lang.reflect.Type;
import java.util.Date;

final class VwPlayerJsonDeserializer implements JsonDeserializer<VwPlayer> {
    @Override public VwPlayer deserialize(final JsonElement json, final Type typeOfT,
                                            final JsonDeserializationContext context) throws JsonParseException {
        return JsonVwPlayerPojo.builder()
                .name(json.getAsJsonObject().get(VwPlayerJsonSerializer.NAME_FIELD).getAsString())
                .expiresAt(new Date(json.getAsJsonObject().get(VwPlayerJsonSerializer.EXPIRES_AT_FIELD).getAsLong()))
                .build();
    }
}
