package voidpointer.spigot.voidwhitelist.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.framework.localemodule.Message;

@RequiredArgsConstructor
public enum WhitelistMessage implements Message {
    ADDED("&eИгрок &6«&c{player}&6» &eдобавлен в whitelist."),
    ADDED_TEMP("&eИгрок &6«&c{player}&6» &eдобавлен до &6«&c{time}&6»&e."),
    ADD_HELP("&eИспользуйте &6/whitelist add &cnickname"),
    CONSOLE_WHITELISTED("&eКонсоль не может быть в whitelist, введите ник игрока для проверки."),
    DISABLED("&eWhitelist &cвыключен&e."),
    ENABLED("&eWhitelist &aвключен&e."),
    INFO_NOT_WHITELISTED("&eИгрок &6«&c{player}&6» &cотсутствует &eв whitelist."),
    INFO_WHITELISTED("&eИгрок &6«&c{player}&6» &aесть &eв whitelist."),
    INFO_WHITELISTED_TEMP("&eИгрок &6«&c{player}&6» &aесть &eв whitelist, истекает &d{time}&e."),
    LOGIN_DISALLOWED("&cВас нет в whitelist."),
    NO_PERMISSION("&cУ Вас недостаточно прав."),
    REMOVED("&eИгрок &6«&c{player}&6» &eудален из whitelist."),
    REMOVE_HELP("&eИспользуйте &6/whitelist remove &cnickname"),
    REMOVE_NOT_WHITELISTED("&eИгрока &6«&c{player}&6» &eнет в whitelist."),
    WHITELIST_HELP("&eИспользуйте &6/whitelist &cadd&6|&cremove&6|&cinfo&6|&con&6|&coff"),
    WRONG_DATE_FORMAT("&cНеверный формат времени. Пример на 1 день, 15 часов и 45 минут: &a1d15h45m"),
    YOU_WERE_REMOVED("&cВас исключили из whitelist."),
    ;

    @Getter private final String defaultMessage;

    @Override public String getPath() {
        return toString().replace('_', '-').toLowerCase();
    }
}