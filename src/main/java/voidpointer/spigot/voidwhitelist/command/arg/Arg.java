package voidpointer.spigot.voidwhitelist.command.arg;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@RequiredArgsConstructor
final class Arg implements Comparable<Arg> {
    final int orderIndex;
    @EqualsAndHashCode.Include
    @NonNull final String value;

    public boolean isOption() {
        return value.startsWith("-");
    }

    public void ifOptionOrElse(final Consumer<Arg> optionAction, final Consumer<Arg> elseAction) {
        if (isOption())
            optionAction.accept(this);
        else
            elseAction.accept(this);
    }

    @Override public int compareTo(@NonNull final Arg o) {
        return Integer.compare(orderIndex, o.orderIndex);
    }
}
