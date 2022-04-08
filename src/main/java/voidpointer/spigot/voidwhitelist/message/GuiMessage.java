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
package voidpointer.spigot.voidwhitelist.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.framework.localemodule.Message;

@Getter
@RequiredArgsConstructor
public enum GuiMessage implements Message {
    PROFILE_TITLE("&6{player}"),
    PROFILE_LORE_EDITED("&aEdited"),
    PROFILE_NOT_FOUND("&cPlayer not found"),
    PROFILE_REMOVE_FAIL("&cRemove operation failed"),
    PROFILE_INTERNAL("&cInternal error :("),
    PROFILE_EDIT_DATE_INVALID("&cInvalid date"),
    PROFILE_EDIT_DATE_VALID("&aValid date"),
    PROFILE_INFO_NOT_FOUND("&cNothing found"),
    ANVIL_EDIT_TITLE("&eEdit"),
    ;
    private final String defaultMessage;

    @Override public String getPath() {
        return toString().toLowerCase().replace('_', '-');
    }
}
