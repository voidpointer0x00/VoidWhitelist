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
package voidpointer.bukkit.whitelist.service;

import java.util.Collection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.whitelist.Whitelistable;
import voidpointer.bukkit.whitelist.dao.PlayerModelDao;
import voidpointer.bukkit.whitelist.model.PlayerModel;

/** @author VoidPointer aka NyanGuyMF */
@RequiredArgsConstructor
public final class DaoWhitelistableService implements WhitelistableService {
    @NonNull private final PlayerModelDao dao;

    @Override public Whitelistable newWhitelistalbe(final String whitelistableName) {
        return newModel(whitelistableName);
    }

    protected PlayerModel newModel(final String name) {
        return PlayerModel.builder().name(name).build();
    }

    @Override public void create(final Whitelistable whitelistable) {
        dao.createPlayer((PlayerModel) whitelistable);
    }

    @Override public Whitelistable findByName(final String whitelistableName) {
        return dao.findPlayerByName(newModel(whitelistableName));
    }

    @Override public Whitelistable findOrNew(final String whitelistableName) {
        PlayerModel matching = newModel(whitelistableName);
        Whitelistable found = dao.findPlayerByName(matching);
        if (found == null) {
            found = matching;
            create(found);
        }
        return found;
    }

    @Override public void update(final Whitelistable whitelistable) {
        dao.updatePlayer((PlayerModel) whitelistable);
    }

    @Override public void delete(final Whitelistable whitelistable) {
        dao.deletePlayer((PlayerModel) whitelistable);
    }

    @Override public Collection<? extends Whitelistable> findAll() {
        return dao.findAllPlayers();
    }
}
