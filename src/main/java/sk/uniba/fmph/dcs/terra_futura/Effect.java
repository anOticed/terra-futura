package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

/**
 * Common interface for all card effects.
 */
public interface Effect {

    /**
     * Checks if the effect can be applied to the given parameters.
     *
     * @param input     resources the player wants to pay
     * @param output    resources the player should gain
     * @param pollution pollution produced by this effect
     * @return {@code true} if the effect can be applied, {@code false} otherwise.
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Indicates if the effect grants an Assistance reward.
     *
     * @return {@code true} if the effect has Assistance, {@code false} otherwise.
     */
    boolean hasAssistance();

    /**
     * @return a textual representation of the effect's state.
     */
    String state();
}
