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

import com.j256.ormlite.dao.Dao;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.OrmliteConfig;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class OrmliteWhitelistService implements WhitelistService {
    @AutowiredLocale private static LocaleLog log;
    private final OrmliteConfig ormliteConfig;
    private final Dao<WhitelistableModel, UUID> dao;

    public OrmliteWhitelistService(final Plugin plugin) {
        ormliteConfig = new OrmliteConfig(plugin);
        dao = ormliteConfig.getWhitelistableDao();
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return supplyAsync(() -> query(this::find0, uuid));
    }

    private Whitelistable find0(final UUID uuid) throws SQLException {
        assert dao != null : "Cannot perform SQL query because DAO is null";
        return dao.queryForId(uuid);
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

    private <T, R> Optional<R> query(final CheckedFunction<T, R> function, T argument) {
        try {
            return Optional.of(function.apply(argument));
        } catch (Exception exception) {
            log.warn("Unable to perform a database query", exception);
            return Optional.empty();
        }
    }
}
