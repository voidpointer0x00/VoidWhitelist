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
package voidpointer.bukkit.whitelist.config;

import org.bukkit.plugin.Plugin;

import voidpointer.bukkit.framework.config.PluginConfig;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistConfig extends PluginConfig {
    public static final String MAIN_CONFIG_FILENAME = "config.yml";
    private static final String LANG_PATH = "lang";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String IS_ENABLED_PATH = "is-enabled";
    private static final boolean DEFAULT_IS_ENABLED = false;

    private Boolean isCurrentlyEnabled;

    public WhitelistConfig(final Plugin pluginOwner) {
        super(pluginOwner, MAIN_CONFIG_FILENAME);
    }

    public String getLanguage() {
        if (!isLoaded())
            return DEFAULT_LANGUAGE;
        return super.getConfig().getString(LANG_PATH, DEFAULT_LANGUAGE);
    }

    public boolean isEnabled() {
        if (!isLoaded())
            return (isCurrentlyEnabled != null) ? isCurrentlyEnabled : DEFAULT_IS_ENABLED;

        if (isCurrentlyEnabled == null)
            isCurrentlyEnabled = super.getConfig().getBoolean(IS_ENABLED_PATH, DEFAULT_IS_ENABLED);
        return isCurrentlyEnabled.booleanValue();
    }

    public void setEnabled(final boolean isEnabled) {
        isCurrentlyEnabled = isEnabled;
        if (!isLoaded())
            return;
        super.getConfig().set(IS_ENABLED_PATH, isEnabled);
    }
}
