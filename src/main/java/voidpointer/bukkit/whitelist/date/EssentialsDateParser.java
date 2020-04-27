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
package voidpointer.bukkit.whitelist.date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author VoidPointer aka NyanGuyMF */
public final class EssentialsDateParser implements MinecraftDateParser {
    private static Pattern timePattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
        Pattern.CASE_INSENSITIVE
    );

    @Override public long parseDate(final String date) {
        /*
         * Just formatted copy-paste from Essentials DateUtil class
         *
         * Now method returns only Unix-time or -1 if not found
         *      instread of Exception throwing.
         * * in this case we don't need past time so..
         */
        Matcher m = timePattern.matcher(date);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if ((m.group() == null) || m.group().isEmpty())
                continue;
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty())
                    years = Integer.parseInt(m.group(1));
                if (m.group(2) != null && !m.group(2).isEmpty())
                    months = Integer.parseInt(m.group(2));
                if (m.group(3) != null && !m.group(3).isEmpty())
                    weeks = Integer.parseInt(m.group(3));
                if (m.group(4) != null && !m.group(4).isEmpty())
                    days = Integer.parseInt(m.group(4));
                if (m.group(5) != null && !m.group(5).isEmpty())
                    hours = Integer.parseInt(m.group(5));
                if (m.group(6) != null && !m.group(6).isEmpty())
                    minutes = Integer.parseInt(m.group(6));
                if (m.group(7) != null && !m.group(7).isEmpty())
                    seconds = Integer.parseInt(m.group(7));
                break;
            }
        }
        if (!found)
            return -1;
        Calendar c = new GregorianCalendar();
        if (years > 0)
            c.add(Calendar.YEAR, years);
        if (months > 0)
            c.add(Calendar.MONTH, months);
        if (weeks > 0)
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        if (days > 0)
            c.add(Calendar.DAY_OF_MONTH, days);
        if (hours > 0)
            c.add(Calendar.HOUR_OF_DAY, hours);
        if (minutes > 0)
            c.add(Calendar.MINUTE, minutes);
        if (seconds > 0)
            c.add(Calendar.SECOND, seconds);
        Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);
        if (c.after(max))
            return max.getTimeInMillis();
        return c.getTimeInMillis();
    }
}
