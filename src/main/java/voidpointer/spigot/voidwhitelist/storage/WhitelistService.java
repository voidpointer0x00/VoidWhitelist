package voidpointer.spigot.voidwhitelist.storage;

import java.util.Date;
import java.util.List;

public interface WhitelistService {
    Date NEVER_EXPIRES = null;

    boolean addToWhitelist(final String nickname);

    boolean addToWhitelist(final String nickname, Date expiresAt);

    boolean isWhitelisted(final String nickname);

    Date getExpiresAt(final String nickname) throws NotWhitelistedException;

    List<String> getWhitelistedNicknames();

    boolean removeFromWhitelist(final String nickname);
}
