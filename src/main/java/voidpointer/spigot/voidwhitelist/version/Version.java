package voidpointer.spigot.voidwhitelist.version;

import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.bukkit.Bukkit.getBukkitVersion;

public final class Version {
    public static final Pattern VERSION_PATTERN = Pattern.compile("\\d+\\.(?<major>\\d+)\\.(?<minor>\\d+)");

    private static Boolean supportsGui = null;

    public static boolean supportsGui() {
        if (supportsGui == null) {
            final Matcher versionMatcher = VERSION_PATTERN.matcher(getBukkitVersion());
            supportsGui = versionMatcher.matches()
                    && parseInt(versionMatcher.group("major")) < WhitelistGui.MAJOR_VERSION_REQUIRED;
        }
        return supportsGui;
    }
}
