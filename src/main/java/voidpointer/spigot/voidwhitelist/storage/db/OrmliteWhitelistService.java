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
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.OrmliteConfig;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.DatabaseSyncTask;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

public final class OrmliteWhitelistService implements WhitelistService {
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

    public CompletableFuture<CloseableWrappedIterable<? extends Whitelistable>> findAll() {
        return supplyAsync(() -> {
            requireConnection();
            return dao.getWrappedIterable();
        });
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        return supplyAsync(() -> query(this::findAll0, null, (long) limit))
                .exceptionally(this::onFindAllException);
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        return supplyAsync(() -> query(this::findAll0, offset + 1L, (long) limit))
                .exceptionally(this::onFindAllException);
    }

    private Set<Whitelistable> findAll0(final Long offset, final Long limit) throws Exception {
        requireConnection();
        List<WhitelistableModel> result = dao.queryBuilder()
                .offset(offset)
                .limit(limit)
                .query();
        return unmodifiableSet(new HashSet<>(result));
    }

    private Set<Whitelistable> onFindAllException(final Throwable thrown) {
        log.warn("Couldn't execute find all query: {0}", thrown.getMessage());
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
            failedToReconnect = !reconnect().isSuccess();
            if (!failedToReconnect)
                return ifReconnected.get();
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
        log.warn("Couldn't find whitelistable: {0}", thrown.getMessage());
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
        requireConnection();
        Set<WhitelistableModel> added = new HashSet<>();
        try {
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

    @Override public CompletableFuture<Optional<Whitelistable>> add(final UUID uuid, final String name, final Date expiresAt) {
        return supplyAsync(() -> ofNullable(query(this::add0, new WhitelistableModel(uuid, name, expiresAt))))
                .exceptionally(this::onAddException);
    }

    private Whitelistable add0(final WhitelistableModel whitelistable) throws SQLException {
        requireConnection();
        dao.createOrUpdate(whitelistable);
        return whitelistable;
    }

    private Optional<Whitelistable> onAddException(final Throwable thrown) {
        log.warn("Couldn't add whitelistable: {0}", thrown.getMessage());
        return Optional.empty();
    }

    @Override public CompletableFuture<Optional<Whitelistable>> update(final @NonNull Whitelistable whitelistable) {
        return supplyAsync(() -> ofNullable(query(this::update0, whitelistable)))
                .exceptionally(this::onUpdateException);
    }

    private Whitelistable update0(final Whitelistable whitelistable) throws SQLException {
        requireConnection();
        if (whitelistable instanceof WhitelistableModel)
            dao.update((WhitelistableModel) whitelistable);
        else
            dao.update(WhitelistableModel.copyOf(whitelistable));
        return whitelistable;
    }

    private Optional<Whitelistable> onUpdateException(final Throwable thrown) {
        log.warn("Couldn't update whitelistable: {0}", thrown.getMessage());
        return Optional.empty();
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        return supplyAsync(() -> queryBool(this::remove0, whitelistable)).exceptionally(this::onRemoveException);
    }

    private Boolean remove0(final Whitelistable whitelistable) throws Exception {
        requireConnection();
        if (whitelistable instanceof WhitelistableModel)
            dao.delete((WhitelistableModel) whitelistable);
        else
            dao.delete(WhitelistableModel.copyOf(whitelistable));
        return true;
    }

    private Boolean onRemoveException(final Throwable thrown) {
        log.warn("Couldn't remove whitelistable: {0}", thrown.getMessage());
        return false;
    }

    private <T, U, R> R query(final CheckedBiFunction<T, U, R> function, T first, U second) {
        try {
            return function.apply(first, second);
        } catch (final Exception exception) {
            reportQueryException(exception);
            return null;
        }
    }

    private <T> Boolean queryBool(final CheckedFunction<T, Boolean> function, T argument) {
        try {
            return function.apply(argument);
        } catch (final Exception exception) {
            reportQueryException(exception);
            return Boolean.FALSE;
        }
    }

    private <T, R> R query(final CheckedFunction<T, R> function, T argument) {
        try {
            return function.apply(argument);
        } catch (final Exception exception) {
            reportQueryException(exception);
            return null;
        }
    }

    private void reportQueryException(final Throwable thrown) {
        log.warn("Unable to perform a database query: {0}", thrown.getMessage());
    }

    private void requireConnection() {
        Objects.requireNonNull(dao, "Database connection is not established");
    }

    private void disableOrmliteLogging() {
        Logger.setGlobalLogLevel(Level.OFF);
    }
}
