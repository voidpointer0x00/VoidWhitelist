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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.storage.AbstractWhitelistable;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded=true, callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public final class HibernateWhitelistable extends AbstractWhitelistable {
    @Id
    @EqualsAndHashCode.Include
    @Column(nullable=false, unique=true, columnDefinition="BINARY(16)")
    private UUID uniqueId;
    @Column private String name;
    @Column private Date expiresAt;

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }
}
