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
package voidpointer.bukkit.whitelist;

import java.util.Date;

/**
 * This interface provides logic for temporary whitelistable
 *      entities.
 * <p>
 * The entity may be whitelisted both until any date and
 *      forever.
 *
 * @author VoidPointer aka NyanGuyMF
 */
public interface Whitelistable {
    String getName();

    boolean isWhitelisted();

    boolean isExpired();

    boolean isWhitelistedAndNotExpired();

    void setWhitelisted(boolean isWhitelisted);

    Date getUntil();

    default boolean hasUntil() {
        return getUntil() != null;
    }

    void setUntil(Date until);
}
