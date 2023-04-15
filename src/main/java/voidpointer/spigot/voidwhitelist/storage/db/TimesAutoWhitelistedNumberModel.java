package voidpointer.spigot.voidwhitelist.storage.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelistedNumber;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@DatabaseTable(tableName="auto_whitelist")
public final class TimesAutoWhitelistedNumberModel implements TimesAutoWhitelistedNumber {
    @EqualsAndHashCode.Include
    @DatabaseField(id=true, columnName="unique_id", dataType=DataType.UUID)
    private UUID uniqueId;
    @DatabaseField(columnName="times_auto_whitelisted")
    private int timesAutoWhitelisted;

    public static TimesAutoWhitelistedNumberModel copyOf(final TimesAutoWhitelistedNumber timesAutoWhitelisted) {
        return new TimesAutoWhitelistedNumberModel(timesAutoWhitelisted.getUniqueId(), timesAutoWhitelisted.get());
    }

    @Override public int get() {
        return timesAutoWhitelisted;
    }
}