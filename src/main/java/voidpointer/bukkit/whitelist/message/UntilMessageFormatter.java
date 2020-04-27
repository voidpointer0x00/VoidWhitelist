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
package voidpointer.bukkit.whitelist.message;

import java.util.Calendar;
import java.util.Date;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.framework.locale.Locale;

/** @author VoidPointer aka NyanGuyMF */
@RequiredArgsConstructor
public final class UntilMessageFormatter {
    @NonNull private final Locale locale;

    public String format(final Date until) {
        Calendar cal = new Calendar.Builder().setInstant(until).build();
        return locale.getLocalized(WhitelistMessage.UNTIL_FORMAT)
                .colorize()
                .set("hour", String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)))
                .set("minute", String.format("%02d", cal.get(Calendar.MINUTE)))
                .set("second", String.format("%02d", cal.get(Calendar.SECOND)))
                .set("millisecond", String.format("%03d", cal.get(Calendar.MILLISECOND)))
                .set("year", String.valueOf(cal.get(Calendar.YEAR)))
                /* adding one because JANUARY is 0 month and so on */
                .set("month", String.format("%02d", cal.get(Calendar.MONTH) + 1))
                .set("week", String.valueOf(cal.get(Calendar.WEEK_OF_MONTH)))
                .set("day", String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
                .getValue();

    }
}
