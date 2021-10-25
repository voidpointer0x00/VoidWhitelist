package voidpointer.spigot.voidwhitelist.storage.json;

import lombok.*;
import voidpointer.spigot.voidwhitelist.storage.AbstractVwPlayer;

import java.util.Date;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@NoArgsConstructor
@AllArgsConstructor
final class JsonVwPlayerPojo extends AbstractVwPlayer {
    @NonNull
    @EqualsAndHashCode.Include
    private String name;
    private Date expiresAt;
}
