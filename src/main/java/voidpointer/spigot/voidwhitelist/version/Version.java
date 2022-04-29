package voidpointer.spigot.voidwhitelist.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.bukkit.Bukkit.getBukkitVersion;
import static voidpointer.spigot.voidwhitelist.gui.WhitelistGui.MAJOR_VERSION_REQUIRED;

public final class Version {
    public static final Pattern VERSION_PATTERN = Pattern.compile("\\d+\\.(?<major>\\d+)(\\.(?<minor>\\d+))?");

    private static Boolean supportsGui = null;

    public static boolean supportsGui() {
        if (supportsGui == null) {
            final Matcher versionMatcher = VERSION_PATTERN.matcher(getBukkitVersion());
            supportsGui = versionMatcher.find() && parseInt(versionMatcher.group("major")) < MAJOR_VERSION_REQUIRED;
        }
        return supportsGui;
    }
}
