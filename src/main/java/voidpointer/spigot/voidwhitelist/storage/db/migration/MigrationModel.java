package voidpointer.spigot.voidwhitelist.storage.db.migration;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName="migrations")
public final class MigrationModel {
    @DatabaseField(id=true)
    private String name;
    @DatabaseField(columnName="is_finished")
    private boolean isFinished;
}
