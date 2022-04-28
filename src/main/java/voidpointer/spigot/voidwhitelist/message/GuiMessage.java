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
    ANVIL_EDIT_TITLE("&eEnter new date"),
    ANVIL_EDIT_DATE_INVALID("&cInvalid date"),
    ANVIL_EDIT_DATE_VALID("&aValid date"),
    ANVIL_CLOCK_NAME("&eAccept changes"),
    ANVIL_REFRESH_NAME("&eRefresh"),
    NEVER("never"),
    PROFILE_BACK("&eBack to whitelist"),
    PROFILE_TITLE("&6{player}"),
    PROFILE_REMOVE("&cRemove from the whitelist"),
    PROFILE_EDIT_BOOK_NAME("&eEdit expire time"),
    PROFILE_REQUEST_DETAILS("&eMore info"),
    PROFILE_DETAILS_BOOK_NAME("&eDetails"),
    PROFILE_DETAILS_LORE("&eExpires at: {date}\n&eAllowed to join: {status}"),
    PROFILE_DETAILS_NEVER("&anever"),
    PROFILE_DETAILS_DATE("&d{date}"),
    PROFILE_DETAILS_TRUE("&atrue"),
    PROFILE_DETAILS_FALSE("&cfalse"),
    PROFILE_LORE_EDITED("&aEdited"),
    PROFILE_NOT_FOUND("&cPlayer not found"),
    PROFILE_REMOVE_FAIL("&cRemove operation failed"),
    PROFILE_INTERNAL("&cInternal error :("),
    PROFILE_INFO_NOT_FOUND("&cNothing found"),
    WHITELIST_LOADING("&8&oLoading {percentage}%"),
    WHITELIST_NEXT("&eNext page"),
    WHITELIST_PREVIOUS("&ePrevious page"),
    WHITELIST_REFRESH("&eRefresh"),
    WHITELIST_ENABLED("&aEnabled"),
    WHITELIST_DISABLED("&cDisabled"),
    ;
    private final String defaultMessage;

    @Override public String getPath() {
        return toString().toLowerCase().replace('_', '-');
    }
}
