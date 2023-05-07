package voidpointer.spigot.voidwhitelist.storage.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelisted;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@DatabaseTable(tableName="auto_whitelist")
public final class TimesAutoWhitelistedModel implements TimesAutoWhitelisted {
    @EqualsAndHashCode.Include
    @DatabaseField(id=true, columnName="unique_id", dataType=DataType.UUID)
    private UUID uniqueId;
    @DatabaseField(columnName="times_auto_whitelisted")
    private int timesAutoWhitelisted;

    public static TimesAutoWhitelistedModel copyOf(final TimesAutoWhitelisted timesAutoWhitelisted) {
        return new TimesAutoWhitelistedModel(timesAutoWhitelisted.getUniqueId(), timesAutoWhitelisted.get());
    }

    @Override public int get() {
        return timesAutoWhitelisted;
    }
}
