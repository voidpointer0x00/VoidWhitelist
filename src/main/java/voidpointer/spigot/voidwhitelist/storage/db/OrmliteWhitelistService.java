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
import com.j256.ormlite.misc.TransactionManager;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelistedNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

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

public final class OrmliteWhitelistService implements AutoWhitelistService {
    @AutowiredLocale private static LocaleLog log;
    private final OrmliteDatabase ormliteDatabase;

    /* !ONLY! for internal use in case the plugin got disconnected from dbms
     *  indicates whether it is needed to try reconnecting after failing a query
     *  or if we already tried that (in which case trying to reconnect once again will
     *  only lead to an infinite recursion: try query->fail->reconnect->repeat) */
    private boolean failedToReconnect = false;

    public OrmliteWhitelistService(final Plugin plugin) {
        ormliteDatabase = new OrmliteDatabase(plugin);
        ormliteDatabase.connect();
    }

    public ConnectionResult reconnect() {
        return ormliteDatabase.reconnect();
    }

    @Override public StorageMethod getStorageMethod() {
        return DATABASE;
    }

    @Override public void shutdown() {
        failedToReconnect = false;
        ormliteDatabase.shutdown();
    }

    public CompletableFuture<CloseableWrappedIterable<TimesAutoWhitelistedNumberModel>> findAllAuto() {
        return supplyAsync(() -> {
           try {
               requireConnection();
               return ormliteDatabase.getAutoWhitelistDao().getWrappedIterable();
           } catch (final SQLException sqlException) {
               log.warn("Unable to get all entities from auto whitelist: {0}", sqlException.getMessage());
               return null;
           }
        });
    }

    @Override public CompletableFuture<Optional<TimesAutoWhitelistedNumber>> getTimesAutoWhitelisted(final UUID uniqueId) {
        return supplyAsync(() -> {
            try {
                requireConnection();
                return Optional.ofNullable(ormliteDatabase.getAutoWhitelistDao().queryForId(uniqueId));
            } catch (final SQLException sqlException) {
                log.warn("Unable to get timesAutoWhitelisted for {0}: {1}", uniqueId, sqlException.getMessage());
                return Optional.empty();
            }
        });
    }

    public CompletableFuture<Optional<Long>> getTotalCountOfWhitelist() {
        return supplyAsync(() -> {
            try {
                requireConnection();
                return Optional.of(ormliteDatabase.getWhitelistDao().countOf());
            } catch (final SQLException sqlException) {
                log.warn("Unable to get the total number of whitelist entities: {}", sqlException.getMessage());
                return Optional.empty();
            }
        });
    }

    public CompletableFuture<Optional<Long>> getTotalCountOfAutoWhitelist() {
        return supplyAsync(() -> {
            try {
                requireConnection();
                return Optional.of(ormliteDatabase.getAutoWhitelistDao().countOf());
            } catch (final SQLException sqlException) {
                log.warn("Unable to get the total number of whitelist entities: {}", sqlException.getMessage());
                return Optional.empty();
            }
        });
    }

    public CompletableFuture<CloseableWrappedIterable<? extends Whitelistable>> findAll() {
        return supplyAsync(() -> {
            try {
                requireConnection();
            } catch (final SQLException sqlException) {
                return null;
            }
            return ormliteDatabase.getWhitelistDao().getWrappedIterable();
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
        List<WhitelistableModel> result = ormliteDatabase.getWhitelistDao().queryBuilder()
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
        return ofNullable(ormliteDatabase.getWhitelistDao().queryForId(uuid));
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

    public CompletableFuture<Integer> addAllIfNotExists(final Collection<Whitelistable> all) {
        if (all.isEmpty())
            return completedFuture(0);
        return supplyAsync(() -> addAll((whitelistableModel, addedInTotal) -> {
            if (!ormliteDatabase.getWhitelistDao().idExists(whitelistableModel.getUniqueId())) {
                ormliteDatabase.getWhitelistDao().create(whitelistableModel);
                addedInTotal.increment();
            }
        }, all));
    }

    public CompletableFuture<Integer> addAllReplacing(final Collection<Whitelistable> all) {
        if (all.isEmpty())
            return completedFuture(0);
        return supplyAsync(() -> addAll((whitelistableModel, addedInTotal) -> {
            ormliteDatabase.getWhitelistDao().createOrUpdate(whitelistableModel);
            addedInTotal.increment();
        }, all));
    }

    private int addAll(
            final CheckedBiConsumer<WhitelistableModel, MutableInt> addFunction,
            final Collection<Whitelistable> all) {
        final MutableInt addedInTotal = new MutableInt();
        try {
            requireConnection();
            return ormliteDatabase.getWhitelistDao().callBatchTasks(() -> {
                for (final Whitelistable whitelistable : all) {
                    if (whitelistable instanceof WhitelistableModel)
                        addFunction.consume((WhitelistableModel) whitelistable, addedInTotal);
                    else
                        addFunction.consume(WhitelistableModel.copyOf(whitelistable), addedInTotal);
                }
                return addedInTotal.intValue();
            });
        } catch (final Exception exception) {
            log.warn("Unable to add all whitelistable entities: {0}", exception.getMessage());
            return addedInTotal.intValue();
        }
    }

    public CompletableFuture<Integer> addAllAutoIfNotExists(final Collection<TimesAutoWhitelistedNumber> all) {
        if (all.isEmpty())
            return completedFuture(0);
        return supplyAsync(() -> addAllAuto((timesAutoWhitelistedModel, addedInTotal) -> {
            if (!ormliteDatabase.getAutoWhitelistDao().idExists(timesAutoWhitelistedModel.getUniqueId())) {
                ormliteDatabase.getAutoWhitelistDao().create(timesAutoWhitelistedModel);
                addedInTotal.increment();
            }
        }, all));
    }

    public CompletableFuture<Integer> addAllAutoReplacing(final Collection<TimesAutoWhitelistedNumber> all) {
        if (all.isEmpty())
            return completedFuture(0);
        return supplyAsync(() -> addAllAuto((timesAutoWhitelistedModel, addedInTotal) -> {
            ormliteDatabase.getAutoWhitelistDao().createOrUpdate(timesAutoWhitelistedModel);
            addedInTotal.increment();
        }, all));
    }

    private int addAllAuto(
            final CheckedBiConsumer<TimesAutoWhitelistedNumberModel, MutableInt> addFunction,
            final Collection<TimesAutoWhitelistedNumber> allAuto) {
        final MutableInt addedInTotal = new MutableInt();
        try {
            requireConnection();
            return ormliteDatabase.getAutoWhitelistDao().callBatchTasks(() -> {
                for (final TimesAutoWhitelistedNumber timesAutoWhitelisted : allAuto) {
                    if (timesAutoWhitelisted instanceof TimesAutoWhitelistedNumberModel)
                        addFunction.consume((TimesAutoWhitelistedNumberModel) timesAutoWhitelisted, addedInTotal);
                    else
                        addFunction.consume(TimesAutoWhitelistedNumberModel.copyOf(timesAutoWhitelisted), addedInTotal);
                }
                return addedInTotal.intValue();
            });
        } catch (final Exception exception) {
            log.warn("Unable to add all whitelistable entities: {0}", exception.getMessage());
            return addedInTotal.intValue();
        }
    }

    @Override public CompletableFuture<Optional<Whitelistable>> add(
            final UUID uuid, final String name, final Date expiresAt, final int timesAutoWhitelisted) {
        return supplyAsync(() -> {
            try {
                requireConnection();
                return add0(uuid, name, expiresAt, timesAutoWhitelisted);
            } catch (final SQLException sqlException) {
                log.warn("Unable to add {0} to whitelist: {1}", uuid, sqlException.getMessage());
                return Optional.empty();
            }
        });
    }

    private Optional<Whitelistable> add0(final UUID uuid, final String name, final Date expiresAt,
                                         final int timesAutoWhitelisted) throws SQLException {
        return TransactionManager.callInTransaction(ormliteDatabase.getConnectionSource(), () -> {
            ormliteDatabase.getAutoWhitelistDao().createOrUpdate(new TimesAutoWhitelistedNumberModel(uuid, timesAutoWhitelisted));
            final WhitelistableModel whitelistable = new WhitelistableModel(uuid, name, expiresAt);
            ormliteDatabase.getWhitelistDao().createOrUpdate(whitelistable);
            return Optional.of(whitelistable);
        });
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
        ormliteDatabase.getWhitelistDao().createOrUpdate(whitelistable);
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
            ormliteDatabase.getWhitelistDao().update((WhitelistableModel) whitelistable);
        else
            ormliteDatabase.getWhitelistDao().update(WhitelistableModel.copyOf(whitelistable));
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

    private Boolean removeQuietly(final Whitelistable whitelistable) {
        try {
            return remove0(whitelistable);
        } catch (final SQLException sqlException) {
            return onRemoveException(sqlException);
        }
    }

    private Boolean remove0(final Whitelistable whitelistable) throws SQLException {
        requireConnection();
        if (whitelistable instanceof WhitelistableModel)
            ormliteDatabase.getWhitelistDao().delete((WhitelistableModel) whitelistable);
        else
            ormliteDatabase.getWhitelistDao().delete(WhitelistableModel.copyOf(whitelistable));
        return true;
    }

    private Boolean onRemoveException(final Throwable thrown) {
        log.warn("Could not remove whitelistable: {0}", thrown.getMessage());
        return false;
    }

    private void requireConnection() throws SQLException {
        if (ormliteDatabase.isNotConnected())
            throw new SQLException("Database connection is not established");
    }
}
