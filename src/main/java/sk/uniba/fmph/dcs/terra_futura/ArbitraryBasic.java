package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;
import java.util.Objects;

/**
 * Effect that consumes an arbitrary set of resources of a given size
 * and produces a fixed list of output resources and pollution.
 */
public final class ArbitraryBasic implements Effect {
    private final int from;
    private final List<Resource> to;
    private final int pollution;

    /**
     * Creates an effect with arbitrary input of the given size.
     *
     * @param from      number of resources that will be paid
     * @param to        resources that will be gained as output
     * @param pollution the amount of pollution produced by this effect
     */
    public ArbitraryBasic(final int from, final List<Resource> to, final int pollution) {
        this.from = from;
        this.to = List.copyOf(Objects.requireNonNull(to, "Output cannot be null"));
        this.pollution = pollution;
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
        if (input == null || output == null) {
            return false;
        }

        return input.size() == from && output.equals(to) && this.pollution == pollution;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return String.format("ArbitraryBasic{from=%d, to=%s, pollution=%d}", from, to, pollution);
    }
}
