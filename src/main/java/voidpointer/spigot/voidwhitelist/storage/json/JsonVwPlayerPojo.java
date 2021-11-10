package voidpointer.spigot.voidwhitelist.storage.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
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
