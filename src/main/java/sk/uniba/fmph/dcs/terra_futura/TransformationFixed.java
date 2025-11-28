package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;
import java.util.Objects;

/**
 * Fixed transformation effect:
 * a specific list of input resources is exchanged for a specific list
 * of output resources and a fixed amount of pollution.
 */
public final class TransformationFixed implements Effect {
    private final List<Resource> from;
    private final List<Resource> to;
    private final int pollution;

    /**
     * Creates a fixed transformation effect.
     *
     * @param from      resources that will be paid as input
     * @param to        resources that will be gained as output
     * @param pollution the amount of pollution produced by this effect
     */
    public TransformationFixed(final List<Resource> from, final List<Resource> to, final int pollution) {
        this.from = Objects.requireNonNull(from, "Output cannot be null");
        this.to = Objects.requireNonNull(to, "Output cannot be null");
        this.pollution = pollution;
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
        if (input == null || output == null) {
            return false;
        }

        return input.equals(from) && output.equals(to) && this.pollution == pollution;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return String.format("TransformationFixed{from=%s, to=%s, pollution=%s}", from, to, pollution);
    }
}
