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
package voidpointer.bukkit.whitelist.dao;

import java.util.Collection;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import voidpointer.bukkit.framework.db.OrmLiteDao;
import voidpointer.bukkit.whitelist.model.PlayerModel;

/** @author VoidPointer aka NyanGuyMF */
public final class OrmLitePlayerModelDao
    extends OrmLiteDao<PlayerModel, Integer>
    implements PlayerModelDao
{
    private static final int FIRST_RESULT_INDEX = 0;

    public OrmLitePlayerModelDao(final Dao<PlayerModel, Integer> dao) {
        super(dao);
    }

    @Override public boolean createPlayer(final PlayerModel model) {
        return super.create(model);
    }

    @Override public boolean updatePlayer(final PlayerModel model) {
        return super.update(model);
    }

    @Override public boolean deletePlayer(final PlayerModel model) {
        return super.delete(model);
    }

    @Override public Collection<? extends PlayerModel> findAllPlayers() {
        return super.findAll();
    }

    @Override public PlayerModel findPlayerByName(final PlayerModel model) {
        List<PlayerModel> result = super.findMatching(model);
        if (result.isEmpty())
            return null;
        return result.get(FIRST_RESULT_INDEX);
    }
}
