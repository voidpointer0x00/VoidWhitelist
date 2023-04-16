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

@RequiredArgsConstructor
public enum WhitelistMessage implements Message {
    ADDED("&eИгрок &6«&c{player-details}&6» &eдобавлен в whitelist."),
    ADDED_TEMP("&eИгрок &6«&c{player-details}&6» &eдобавлен до &6«&c{date}&6»&e."),
    ADD_HELP("&eИспользуйте &6/whitelist add &cnickname"),
    ADD_FAIL("&cНе удалось добавить игрока &6{player-details}&c."),
    CONSOLE_WHITELISTED("&eКонсоль не может быть в whitelist, введите ник игрока."),
    CONFIG_RELOADED("&aОбщие настройки перезагружены."),
    DISABLED("&eWhitelist &cвыключен&e."),
    ENABLED("&eWhitelist &aвключен&e."),

    EXPORT_ONLY_FROM_DATABASE("&eЭкспорт доступен только из базы данных."),
    EXPORT_GATHERING("&eСобираем данные на экспорт..."),
    WHITELIST_EXPORT_PROCESSING("&eЭкспоритруем &6{total}&e игроков..."),
    WHITELIST_EXPORT_FINISHED("&eЭкспорт белого списка завершён за &d{ms-spent}ms&e."),
    WHITELIST_EXPORT_FAILURE("&cНе удалось сохранить экспортированных игроков."),
    AUTO_WHITELIST_EXPORT_PROCESSING("&eЭкспортируем данные об автоматическом занесении в белый список &6{total}&e игроков..."),
    AUTO_WHITELIST_EXPORT_FINISHED("&eЭкспорт данных об автоматическом занесении в белый список завершён за &d{ms-spent}ms&e."),
    AUTO_WHITELIST_EXPORT_FAILURE("&cНе удалось сохранить данные об автоматическом занесении игроков в белый список."),

    GUI_LOCALE_DOESNT_SUPPORT_RELOAD("&cТекущая GUI локаль не поддерживает перезагрузку."),
    GUI_LOCALE_RELOADED("&aGUI локаль перезагружена."),
    GUI_NOT_SUPPORTED("&cGUI не поддерживается на вашей версии MC. (минимально необходимая major версия: {major})"),

    IMPORT_LOADED("&eЗагружено &6{loaded}&e записей из &6{storage}&e, импорт начат."),
    IMPORT_ONLY_TO_DATABASE("&eИмпорт работает только из JSON в базу данных."),
    WHITELIST_IMPORT_RESULT("&eИмпортировано игроков: &6{imported}&e из &6{loaded}&e;\n" +
            "&eДанных об автоматическом добавлении в белый список: &6{auto-imported}&e из &6{auto-loaded}&e.\n" +
            "&eОбщее время импорта: &d{ms-spent}ms&e."),

    INFO_NOT_WHITELISTED("&eИгрок &6«&c{player-details}&6» &cотсутствует &eв whitelist."),
    INFO_WHITELISTED("&eИгрок &6«&c{player-details}&6» &aесть &eв whitelist."),
    INFO_WHITELISTED_TEMP("&eИгрок &6«{player-details}&6» &aесть &eв whitelist, истекает &d{date}&e."),
    LOGIN_DISALLOWED("&cВас нет в whitelist."),
    LOGIN_DISALLOWED_EXPIRED("&cВаше время в whitelist истекло."),
    NO_PERMISSION("&cУ Вас недостаточно прав."),
    PLAYER_DETAILS("\\(&c{player}) [hover{&eUUID: &6{uuid}}] [click.copy{{uuid}}]"),
    REMOVED("&eИгрок &6«&c{player-details}&6» &eудален из whitelist."),
    REMOVE_FAIL("&cНе удалось исключить игрока &6{player-details}&c."),
    REMOVE_HELP("&eИспользуйте &6/whitelist remove &cnickname"),
    REMOVE_NOT_WHITELISTED("&eИгрока &6«&c{player-details}&6» &eнет в whitelist."),
    RECONNECT_SUCCESS("&aПереподключение прошло успешно."),
    RECONNECT_FAIL("&cПроизошла ошибка в ходе переподключения. Сервис не будет работать до следующего переподключения."),
    RECONNECT_FAIL_KICK("&cМы больше не можем проверить, есть ли Вы в Whitelist"),
    STORAGE_METHOD_CHANGED("&eМетод хранения данных был изменён &6{old}&e->&6{new}&e."),
    UUID_FAIL_TRY_OFFLINE("&cНе удалось запросить online UUID, попробуйте " +
            "\\(&6-offline &8&o&nклик) [hover{&6/whitelist {cmd} -offline {player} {date}}]" +
            " [click.suggest{/whitelist {cmd} -offline {player} {date}}] "),
    LOCALE_DOESNT_SUPPORT_RELOAD("&cТекущая локаль не поддерживает перезагрузку."),
    LOCALE_RELOADED("&aЛокаль перезагружена."),
    WHITELIST_NOT_ENOUGH_ARGS("&cНедостаточно аргументов. \\(&6/whitelist help) [click.run{/whitelist help}]"),
    WHITELIST_UNKNOWN_COMMAND("&cНеизвестная команда. \\(&6/whitelist help) [click.run{/whitelist help}]"),
    WHITELIST_HELP("\\(&e/whitelist gui) [click.run{/whitelist gui}] &f— показть графический интерфейс\n"
            + "\\(&e/whitelist add &6<player> &o[duration] [-online,-offline]) [click.suggest{/whitelist add }] &f— добавить игрока\n"
            + "\\(&e/whitelist remove &6<player> &o[-online,-offline]) [click.suggest{/whitelist rem }] &f— исключить игрока\n"
            + "\\(&e/whitelist info &6<player> &o[-online,-offline]) [click.suggest{/whitelist info }] &f— отобразить информацию об игроке\n"
            + "\\(&e/whitelist status) [click.run{/whitelist status}] &f— узнать, включен ли whitelist\n"
            + "\\(&e/whitelist on|off) [click.suggest{/whitelist o}] &f— включить/выключить whitelist"),
    WRONG_DATE_FORMAT("&cНеверный формат времени. Пример на 1 день, 15 часов и 45 минут &a1d15h45m"),
    YOU_WERE_REMOVED("&cВас исключили из whitelist."),

    // TODO add unique id display on hover effects

    AUTO_UUID_FAIL_TRY_OFFLINE("&cНе удалось запросить UUID, попробуйте " +
            "\\(&6-offline &8&o&n(клик)) [hover{&6/auto-whitelist {cmd} -offline {player}}]" +
            " [click.suggest{/auto-whitelist {cmd} -offline {player}}] "),
    AUTO_WHITELIST_NOT_ENOUGH_ARGS("&cНедостаточно аргументов. \\(&6/auto-whitelist help) [click.run{/auto-whitelist help}]"),
    AUTO_WHITELIST_UNKNOWN_COMMAND("&cНеизвестная команда. \\(&6/auto-whitelist help) [click.run{/auto-whitelist help}]"),
    AUTO_WHITELIST_HELP("\\(&e/auto-whitelist on|off) [click.suggest{/auto-whitelist o}] &f— включить/выключить auto-whitelist\n" +
            "\\(&e/auto-whitelist is-on) [click.run{/auto-whitelist status}] &f— узнать, включен ли auto-whitelist\n" +
            "\\(&e/auto-whitelist info &6<player>) [click.suggest{/auto-whitelist info }] &f— узнать лимит игрока\n" +
            "\\(&e/auto-whitelist set &6<player> <int>) [click.suggest{/auto-whitelist set }] &f— установить лимит для игрока\n" +
            "\\(&e/auto-whitelist reset &6<player>) [click.suggest{/auto-whitelist reset }] &f— сбросить лимит для игрока\n"),
    AUTO_WHITELIST_ENABLED("&eАвтоматическое добавление в whitelist &aвключено&e."),
    AUTO_WHITELIST_DISABLED("&eАвтоматическое добавление в whitelist &cвыключено&e."),
    AUTO_WHITELIST_RESET("&eКол-во авто. добавлений в whitelist для игрока &6{player-details}&e сброшено."),
    AUTO_WHITELIST_RESET_FAIL("&сНе удалось сбросить кол-во авто. добавлений в whitelist для игрока &6{player-details}."),
    AUTO_WHITELIST_RESET_ARGS("&cВы не указали игрока &6/auto-whitelist reset &n<player>&c."),
    AUTO_WHITELIST_SET("&eКол-во авто. добавлений для игрока &6{player-details}&e изменено на &c{new}&e."),
    AUTO_WHITELIST_SET_FAIL("&eНе удалось обновить кол-во авто. добавлений для игрока &6{player-details}&e."),
    AUTO_WHITELIST_SET_ARGS("&cВы указали недостаточно параметров &8({given}/{needed}) &6/auto-whitelist set &n<player>&r &6&n<int>&c."),
    AUTO_WHITELIST_SET_INVALID_INT("&cНеобходимо указать целочисленное значение &8({given}) &6/auto-whitelist set <player> &6&n&l<int>&c."),
    AUTO_WHITELIST_INFO("&eТекущее кол-во авто. добавлений для игрока &6{player-details}&e: &c{times-auto-whitelisted}&e."),
    AUTO_WHITELIST_INFO_ARGS("&cВы не указали игрока &6/auto-whitelist info &n<player>&c."),
    AUTO_WHITELIST_INFO_NOT_FOUND("&eИнформация для игрока &6{player-details} &eотсутствует."),
    AUTO_WHITELIST_GET_LIMIT("&eТекущий лимит для auto-whitelist: &c{limit}&e."),
    AUTO_WHITELIST_SET_LIMIT("&eЛимит для auto-whitelist изменён: &c{old}&6->&c{new}&e."),
    AUTO_WHITELIST_SET_LIMIT_ARGS("&cВы не указали лимит &6/auto-whitelist set-limit &n<limit>&c."),
    AUTO_WHITELIST_GET_STRATEGY("&eТекущая стратегия выбора игроков для auto-whitelist: &c{strategy}&e."),
    AUTO_WHITELIST_SET_STRATEGY("&eСтратегия выбора игроков для auto-whitelist изменена: &c{old}&6->&c{new}&e."),
    AUTO_WHITELIST_SET_STRATEGY_ARGS("&cВы не указали стратегию &6/auto-whitelist set-strategy &n<strategy>&c."),
    ;

    public static Message of(final KickReason kickReason) {
        switch (kickReason) {
            case EXPIRED:
                return LOGIN_DISALLOWED_EXPIRED;
            case NOT_ALLOWED:
            default:
                return LOGIN_DISALLOWED;
        }
    }

    @Getter private final String defaultMessage;

    @Override public String getPath() {
        return toString().replace('_', '-').toLowerCase();
    }
}
