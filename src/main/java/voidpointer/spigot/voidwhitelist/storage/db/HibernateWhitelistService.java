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

import org.bukkit.plugin.Plugin;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.HibernateConfig;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class HibernateWhitelistService implements WhitelistService {
    private final HibernateConfig hibernateConfig;

    public HibernateWhitelistService(final Plugin plugin) {
        hibernateConfig = new HibernateConfig(plugin);
        hibernateConfig.addEntity(HibernateWhitelistable.class);
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Whitelistable> add(final UUID uuid, final String name, final Date expiresAt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Whitelistable> update(final Whitelistable whitelistable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
