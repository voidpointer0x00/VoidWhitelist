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
package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.InstanceCreator;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;

final class WhitelistableInstanceCreator implements InstanceCreator<Whitelistable> {
    @Override public Whitelistable createInstance(final Type type) {
        return new JsonWhitelistablePojo();
    }
}
