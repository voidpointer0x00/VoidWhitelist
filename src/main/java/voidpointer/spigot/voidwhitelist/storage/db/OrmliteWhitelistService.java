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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.unmodifiableSet;
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
        return supplyAsync(() -> query(this::findAll0, null, (long) limit));
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        return supplyAsync(() -> query(this::findAll0, offset + 1L, (long) limit));
    }

    private Set<Whitelistable> findAll0(final Long offset, final Long limit) throws Exception {
        List<WhitelistableModel> result = dao.queryBuilder()
                .offset(offset)
                .limit(limit)
                .query();
        return unmodifiableSet(new HashSet<>(result));
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return supplyAsync(() -> Optional.ofNullable(query(this::find0, uuid)));
    }

    private Whitelistable find0(final UUID uuid) throws SQLException {
        assert dao != null : "Cannot perform SQL query because DAO is null";
        return dao.queryForId(uuid);
    }

    @Override public CompletableFuture<Whitelistable> add(final UUID uuid, final String name, final Date expiresAt) {
        return supplyAsync(() -> query(this::add0, new WhitelistableModel(uuid, name, expiresAt)));
    }

    private WhitelistableModel add0(final WhitelistableModel whitelistable) throws SQLException {
        dao.createOrUpdate(whitelistable);
        return whitelistable;
    }

    @Override public CompletableFuture<Whitelistable> update(final Whitelistable whitelistable) {
        return supplyAsync(() -> query(this::update0, whitelistable));
    }

    private Whitelistable update0(final Whitelistable whitelistable) throws SQLException {
        if (whitelistable instanceof WhitelistableModel)
            dao.update((WhitelistableModel) whitelistable);
        else
            dao.update(WhitelistableModel.copyOf(whitelistable));
        return whitelistable;
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        return supplyAsync(() -> queryBool(this::remove0, whitelistable)).exceptionally(this::onRemoveException);
    }

    private Boolean remove0(final Whitelistable whitelistable) throws Exception {
        if (whitelistable instanceof WhitelistableModel)
            dao.delete((WhitelistableModel) whitelistable);
        else
            dao.delete(WhitelistableModel.copyOf(whitelistable));
        return true;
    }

    private Boolean onRemoveException(final Throwable thrown) {
        log.info("Couldn't remove whitelistable: {0}", thrown.getMessage());
        return false;
    }

    private <T, U, R> R query(final CheckedBiFunction<T, U, R> function, T first, U second) {
        try {
            return function.apply(first, second);
        } catch (Exception exception) {
            log.warn("Unable to perform a database query", exception);
            return null;
        }
    }

    private <T> Boolean queryBool(final CheckedFunction<T, Boolean> function, T argument) {
        try {
            return function.apply(argument);
        } catch (Exception exception) {
            log.warn("Unable to perform a database query", exception);
            return Boolean.FALSE;
        }
    }

    private <T, R> R query(final CheckedFunction<T, R> function, T argument) {
        try {
            return function.apply(argument);
        } catch (Exception exception) {
            log.warn("Unable to perform a database query", exception);
            return null;
        }
    }
}
