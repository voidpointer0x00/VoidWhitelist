/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.storage.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.AbstractWhitelistable;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@DatabaseTable(tableName="whitelist")
@EqualsAndHashCode(onlyExplicitlyIncluded=true, callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public final class WhitelistableModel extends AbstractWhitelistable {
    @DatabaseField(columnName="unique_id", id=true, dataType=DataType.UUID)
    @EqualsAndHashCode.Include
    private UUID uniqueId;
    @DatabaseField(columnName="name")
    private String name;
    @DatabaseField(columnName="expires_at", dataType=DataType.DATE)
    private Date expiresAt;
    // TODO rework OrmliteWhitelistableService and OrmliteConfig, so that
    //  it will run migrations to add this particular field and allow more
    //  flexibility later
    // At this stage database storage won't work
    @DatabaseField(columnName="times_auto_whitelisted")
    private int timesAutoWhitelisted = 0;

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }

    public static WhitelistableModel copyOf(final Whitelistable whitelistable) {
        return new WhitelistableModel(whitelistable.getUniqueId(), whitelistable.getName(),
                whitelistable.getExpiresAt(), whitelistable.getTimesAutoWhitelisted());
    }
}
