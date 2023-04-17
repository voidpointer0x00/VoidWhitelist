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

    public static Optional<Long> exactMillis(final @NotNull String essentialsDate) {
        final long millis = EssentialsDateParser.parseDate(essentialsDate, EssentialsDateParser.EXACT_MILLIS);
        if (millis == EssentialsDateParser.WRONG_DATE_FORMAT)
            return Optional.empty();
        return Optional.of(millis);
    }
}
