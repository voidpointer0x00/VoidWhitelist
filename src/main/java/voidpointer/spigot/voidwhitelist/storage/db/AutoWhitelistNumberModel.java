package voidpointer.spigot.voidwhitelist.storage.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;

import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@DatabaseTable(tableName="auto_whitelist")
public class AutoWhitelistNumberModel implements AutoWhitelistNumber {
    @EqualsAndHashCode.Include
    @DatabaseField(id=true, columnName="unique_id", dataType=DataType.UUID)
    private UUID uniqueId;
    @DatabaseField(columnName="times_auto_whitelisted")
    private int timesAutoWhitelisted;

    @Override public int get() {
        return timesAutoWhitelisted;
    }
}
