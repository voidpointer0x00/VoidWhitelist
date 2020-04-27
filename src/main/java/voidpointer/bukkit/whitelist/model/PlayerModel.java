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
package voidpointer.bukkit.whitelist.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import voidpointer.bukkit.whitelist.Whitelistable;

/** @author VoidPointer aka NyanGuyMF */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable
public final class PlayerModel implements Whitelistable {
    @DatabaseField(generatedId=true)
    private long id;

    @DatabaseField(canBeNull=false)
    private String name;
    @DatabaseField
    @Builder.Default
    private boolean isWhitelisted = false;

    @DatabaseField(dataType=DataType.DATE)
    private Date until;

    @Override public boolean isExpired() {
        if (until == null)
            return false;
        return new Date().after(until);
    }

    @Override public boolean isWhitelistedAndNotExpired() {
        return isWhitelisted() && !isExpired();
    }
}
