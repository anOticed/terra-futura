package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

/**
 * Common interface for all card effects.
 */
public interface Effect {
    boolean check(List<Resource> input, List<Resource> output, int pollution);
    boolean hasAssistance();
    String state();
}
