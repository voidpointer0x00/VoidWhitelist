/*
 * Copyright (c) 2020 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package voidpointer.bukkit.whitelist.db;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import voidpointer.bukkit.framework.config.db.DatabaseConfig;
import voidpointer.bukkit.framework.db.OrmLiteDatabaseManager;
import voidpointer.bukkit.framework.dependency.DependencyManager;
import voidpointer.bukkit.whitelist.dao.OrmLitePlayerModelDao;
import voidpointer.bukkit.whitelist.dao.PlayerModelDao;
import voidpointer.bukkit.whitelist.model.PlayerModel;

/** @author VoidPointer aka NyanGuyMF */

public final class OrmLiteWhitelistableDatabaseManager
    extends OrmLiteDatabaseManager
    implements WhitelistableDatabaseManager
{
    public OrmLiteWhitelistableDatabaseManager(
            final DatabaseConfig config,
            final DependencyManager dependencyManager
    ) {
        super(config, dependencyManager);
    }

    private PlayerModelDao playerModelDao;

    @Override public PlayerModelDao getPlayerModelDao() {
        return playerModelDao;
    }

    @Override protected void onConnectionEstablished(final ConnectionSource connectionSource) {
        initializePlayerModelTable(connectionSource);
        initializePlayerModelDao(connectionSource);
    }

    private void initializePlayerModelDao(final ConnectionSource connectionSource) {
        Dao<PlayerModel, Integer> ormLiteDao;
        try {
            ormLiteDao = DaoManager.createDao(connectionSource, PlayerModel.class);
            playerModelDao = new OrmLitePlayerModelDao(ormLiteDao);
        } catch (SQLException ex) {
            ex.printStackTrace();
            playerModelDao = null;
        }
    }

    private void initializePlayerModelTable(final ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, PlayerModel.class);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
