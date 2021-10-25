package voidpointer.spigot.voidwhitelist.storage;

import lombok.*;
import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
public final class SimpleVwPlayer extends AbstractVwPlayer {
    @NonNull
    @EqualsAndHashCode.Include
    private final String name;
    private Date expiresAt;
}
