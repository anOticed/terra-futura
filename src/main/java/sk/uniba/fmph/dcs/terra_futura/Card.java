package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single card in the player's grid.
 * Stores resources placed on the card,
 * upper/lower effects and pollution capacity
 */
public final class Card {
    private final List<Resource> resources;
    private final Effect upperEffect;
    private final Effect lowerEffect;
    private final int pollutionSpaces;

    /**
     * Creates a card with the given resources, effects and pollution capacity.
     *
     * @param resources       resources stored on the card
     * @param upperEffect     effect on the upper part of the card, may be null
     * @param lowerEffect     effect on the lower part of the card, may be null
     * @param pollutionSpaces number of safe pollution spaces on the card (0..3)
     */
    public Card(final List<Resource> resources, final Effect upperEffect, final Effect lowerEffect, final int pollutionSpaces) {
        this.resources = List.copyOf(Objects.requireNonNull(resources, "Resources cannot be null"));
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
        this.pollutionSpaces = pollutionSpaces;
    }

    /**
     * Creates a card with no resources.
     *
     * @param upperEffect     effect on the upper part of the card, may be null
     * @param lowerEffect     effect on the lower part of the card, may be null
     * @param pollutionSpaces number of safe pollution spaces on the card (0..3)
     */
    public Card(final Effect upperEffect, final Effect lowerEffect, final int pollutionSpaces) {
        this(List.of(), upperEffect, lowerEffect, pollutionSpaces);
    }

    private boolean isInactive() {
        int pollutionCount = 0;
        for (Resource resource : resources) {
            if (resource == Resource.POLLUTION) {
                pollutionCount++;
            }
        }

        return pollutionCount > pollutionSpaces;
    }
}
