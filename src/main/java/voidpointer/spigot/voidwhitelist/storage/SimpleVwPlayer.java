package voidpointer.spigot.voidwhitelist.storage;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
