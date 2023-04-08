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

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.OrmliteConfig;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;
import voidpointer.spigot.voidwhitelist.task.DatabaseSyncTask;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static voidpointer.spigot.voidwhitelist.storage.StorageMethod.DATABASE;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ReconnectResult.FAIL;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ReconnectResult.SUCCESS;

public final class OrmliteWhitelistService implements AutoWhitelistService {
    private final Plugin plugin;
    @AutowiredLocale private static LocaleLog log;
    private Dao<WhitelistableModel, UUID> dao;
    private final OrmliteConfig ormliteConfig;
    private DatabaseSyncTask syncTask;
    /* !ONLY! for internal use in case the plugin got disconnected from dbms
     *  indicates whether it is needed to try reconnecting after failing a query
     *  or if we already tried that (in which case trying to reconnect once again will
     *  only lead to an infinite recursion: try query->fail->reconnect->repeat) */
    private boolean failedToReconnect = false;

    public OrmliteWhitelistService(final Plugin plugin) {
        this.plugin = plugin;
        log.info("Establishing database connection...");
        ormliteConfig = new OrmliteConfig(plugin);
        disableOrmliteLogging();
        dao = ormliteConfig.getWhitelistableDao();
        if (dao != null) {
            log.info("Connection established.");
            scheduleSync();
        }
    }

    public ReconnectResult reconnect() {
        shutdown();
        if (ormliteConfig.reload() && ((dao = ormliteConfig.getWhitelistableDao()) != null)) {
            scheduleSync();
            return SUCCESS;
        }
        return FAIL;
    }

    private void scheduleSync() {
        if (ormliteConfig.isSyncEnabled()) {
            syncTask = new DatabaseSyncTask();
            syncTask.runTaskTimerAsynchronously(plugin, 0, ormliteConfig.getSyncTimerInTicks());
        }
    }

    @Override public StorageMethod getStorageMethod() {
        return DATABASE;
    }

    @Override public void shutdown() {
        failedToReconnect = false;
        if (syncTask != null)
            syncTask.cancel();
        if (ormliteConfig.getConnectionSource() != null)
            ormliteConfig.getConnectionSource().closeQuietly();
    }

    @Override public CompletableFuture<Optional<AutoWhitelistNumber>> getAutoWhitelistNumberOf(final UUID uniqueId) {
        // FIXME implement the thing
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public CompletableFuture<CloseableWrappedIterable<? extends Whitelistable>> findAll() {
        return supplyAsync(() -> {
            try {
                requireConnection();
            } catch (final SQLException sqlException) {
                return null;
            }
            return dao.getWrappedIterable();
        });
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        return supplyAsync(() -> findAll(null, (long) limit)); // rewrite using null
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        return supplyAsync(() -> findAll(offset + 1L, (long) limit));
    }

    private Set<Whitelistable> findAll(final Long offset, final Long limit) {
        try {
            return findAll0(offset, limit);
        } catch (final SQLException sqlException) {
            return tryToReconnectIfDisconnected(sqlException, () -> findAllQuietly(offset, limit),
                    () -> onFindAllException(sqlException));
        }
    }

    private Set<Whitelistable> findAll0(final Long offset, final Long limit) throws SQLException {
        requireConnection();
        List<WhitelistableModel> result = dao.queryBuilder()
                .offset(offset)
                .limit(limit)
                .query();
        return unmodifiableSet(new HashSet<>(result));
    }

    private Set<Whitelistable> findAllQuietly(final Long offset, final Long limit) {
        try {
            return findAll0(offset, limit);
        } catch (SQLException sqlException) {
            return onFindAllException(sqlException);
        }
    }

    private Set<Whitelistable> onFindAllException(final Throwable thrown) {
        log.warn("Could not execute find all query: {0}", thrown.getMessage());
        return emptySet();
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return supplyAsync(() -> {
            try {
                return find0(uuid);
            } catch (final SQLException sqlException) {
                return tryToReconnectIfDisconnected(sqlException, () -> findQuietly(uuid),
                        () -> onFindException(sqlException));
            }
        });
    }

    private <T> T tryToReconnectIfDisconnected(final SQLException sqlException, final Supplier<T> ifReconnected,
                                               final Supplier<T> ifFailed) {
        if (isDisconnectedAndCanReconnect(sqlException.getMessage())) {
            log.warn("Lost database connection! Trying to reconnect...");
            failedToReconnect = !reconnect().isSuccess();
            if (!failedToReconnect) {
                log.info("Reconnected successfully!");
                return ifReconnected.get();
            } else {
                log.severe("Failed to reconnect!");
            }
        }
        return ifFailed.get();
    }

    private boolean isDisconnectedAndCanReconnect(final String exceptionMessage) {
        return !failedToReconnect && (exceptionMessage.contains("wait_timeout")
                || exceptionMessage.contains("Communications link failure"));
    }

    private Optional<Whitelistable> find0(final UUID uuid) throws SQLException {
        requireConnection();
        return ofNullable(dao.queryForId(uuid));
    }

    private Optional<Whitelistable> findQuietly(final UUID uuid) {
        try {
            return find0(uuid);
        } catch (final SQLException sqlException) {
            return onFindException(sqlException);
        }
    }

    private Optional<Whitelistable> onFindException(final Throwable thrown) {
        log.warn("Could not find whitelistable: {0}", thrown.getMessage());
        return Optional.empty();
    }

    public CompletableFuture<Set<Whitelistable>> addAllIfNotExists(final Collection<Whitelistable> all) {
        if (all.isEmpty())
            return completedFuture(emptySet());
        return supplyAsync(() -> addAll((model, addedSet) -> {
            if (!dao.idExists(model.getUniqueId())) {
                dao.create(model);
                addedSet.add(model);
            }
        }, all));
    }

    public CompletableFuture<Set<Whitelistable>> addAllReplacing(final Collection<Whitelistable> all) {
        if (all.isEmpty())
            return completedFuture(emptySet());
        return supplyAsync(() -> addAll((model, addedSet) -> {
            dao.createOrUpdate(model);
            addedSet.add(model);
        }, all));
    }

    private Set<Whitelistable> addAll(
            final CheckedBiConsumer<WhitelistableModel, Set<WhitelistableModel>> addFunction,
            final Collection<Whitelistable> all) {
        Set<WhitelistableModel> added = new HashSet<>();
        try {
            requireConnection();
            return dao.callBatchTasks(() -> {
                for (final Whitelistable whitelistable : all) {
                    if (whitelistable instanceof WhitelistableModel)
                        addFunction.consume((WhitelistableModel) whitelistable, added);
                    else
                        addFunction.consume(WhitelistableModel.copyOf(whitelistable), added);
                }
                return unmodifiableSet(added);
            });
        } catch (final Exception exception) {
            return unmodifiableSet(added);
        }
    }

    @Override public CompletableFuture<Optional<Whitelistable>> add(
            final UUID uuid, final String name, final Date expiresAt, final int timesAutoWhitelisted) {
        // FIXME implement the thing
        throw new UnsupportedOperationException();
    }

    @Override public CompletableFuture<Optional<Whitelistable>> add(
            final UUID uuid, final String name, final Date expiresAt) {
        return supplyAsync(() -> {
            try {
                return add0(uuid, name, expiresAt);
            } catch (final SQLException sqlException) {
                return tryToReconnectIfDisconnected(sqlException,
                        () -> addQuietly(uuid, name, expiresAt),
                        () -> onAddException(sqlException));
            }
        });
    }

    private Optional<Whitelistable> addQuietly(final UUID uuid, final String name, final Date expiresAt) {
        try {
            return add0(uuid, name, expiresAt);
        } catch (SQLException sqlException) {
            return onAddException(sqlException);
        }
    }

    private Optional<Whitelistable> add0(final UUID uuid, final String name, final Date expiresAt) throws SQLException {
        final WhitelistableModel whitelistable = new WhitelistableModel(uuid, name, expiresAt);
        requireConnection();
        dao.createOrUpdate(whitelistable);
        return Optional.of(whitelistable);
    }

    private Optional<Whitelistable> onAddException(final Throwable thrown) {
        log.warn("Could not add whitelistable: {0}", thrown.getMessage());
        return Optional.empty();
    }

    @Override public CompletableFuture<Optional<Whitelistable>> update(final @NonNull Whitelistable whitelistable) {
        return supplyAsync(() -> {
            try {
                return update0(whitelistable);
            } catch (final SQLException sqlException) {
                return tryToReconnectIfDisconnected(sqlException, () -> updateQuietly(whitelistable),
                        () -> onUpdateException(sqlException));
            }
        });
    }

    private Optional<Whitelistable> updateQuietly(final Whitelistable whitelistable) {
        try {
            return update0(whitelistable);
        } catch (final SQLException sqlException) {
            return onUpdateException(sqlException);
        }
    }

    private Optional<Whitelistable> update0(final Whitelistable whitelistable) throws SQLException {
        requireConnection();
        if (whitelistable instanceof WhitelistableModel)
            dao.update((WhitelistableModel) whitelistable);
        else
            dao.update(WhitelistableModel.copyOf(whitelistable));
        return Optional.of(whitelistable);
    }

    private Optional<Whitelistable> onUpdateException(final Throwable thrown) {
        log.warn("Could not update whitelistable: {0}", thrown.getMessage());
        return Optional.empty();
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        return supplyAsync(() -> {
            try {
                return remove0(whitelistable);
            } catch (final SQLException sqlException) {
                return tryToReconnectIfDisconnected(sqlException, () -> removeQuietly(whitelistable),
                        () -> onRemoveException(sqlException));
            }
        });
    }

    private <T> Boolean removeQuietly(final Whitelistable whitelistable) {
        try {
            return remove0(whitelistable);
        } catch (final SQLException sqlException) {
            return onRemoveException(sqlException);
        }
    }

    private Boolean remove0(final Whitelistable whitelistable) throws SQLException {
        requireConnection();
        if (whitelistable instanceof WhitelistableModel)
            dao.delete((WhitelistableModel) whitelistable);
        else
            dao.delete(WhitelistableModel.copyOf(whitelistable));
        return true;
    }

    private Boolean onRemoveException(final Throwable thrown) {
        log.warn("Could not remove whitelistable: {0}", thrown.getMessage());
        return false;
    }

    private void requireConnection() throws SQLException {
        if (dao == null)
            throw new SQLException("Database connection is not established: dao == null");
    }

    private void disableOrmliteLogging() {
        Logger.setGlobalLogLevel(Level.OFF);
    }
}
