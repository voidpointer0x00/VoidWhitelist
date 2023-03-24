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
package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;

public enum StorageMethod {
    /**
     * Based on JSON serialization libraries. Considered to be more
     *  human-friendly than serialization and fast enough.
     *
     * @see JsonWhitelistService
     */
    JSON,
    /**
     * Based on SQL DBMS.
     * @see OrmliteWhitelistService
     */
    DATABASE,
}
