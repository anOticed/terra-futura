package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Composite effect representing a logical OR of several effects.
 */
public final class EffectOr implements Effect {
    private final List<Effect> effects;

    /**
     * Creates a composite from a non-empty list of effects.
     *
     * @param effects child effects
     */
    public EffectOr(final List<Effect> effects) {
        Objects.requireNonNull(effects, "Effects list cannot be null");
        if (effects.isEmpty()) {
            throw new IllegalArgumentException("Effects list cannot be empty");
        }

        this.effects = List.copyOf(effects);
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
        return effects.stream().anyMatch(effect -> effect.check(input, output, pollution));
    }

    @Override
    public boolean hasAssistance() {
        return effects.stream().anyMatch(Effect::hasAssistance);
    }

    @Override
    public String state() {
        String joinedEffects = effects.stream().map(Effect::state).collect(Collectors.joining(", "));
        return String.format("EffectOr{%s}", joinedEffects);
    }
}
