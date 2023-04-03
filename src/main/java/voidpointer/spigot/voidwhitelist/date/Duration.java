package voidpointer.spigot.voidwhitelist.date;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Optional;

public final class Duration {
    public static Optional<Date> ofEssentialsDate(final @NotNull String essentialsDate) {
        //noinspection deprecation
        final long millis = EssentialsDateParser.parseDate(essentialsDate);
        if (millis == EssentialsDateParser.WRONG_DATE_FORMAT)
            return Optional.empty();
        return Optional.of(new Date(millis));
    }
}
