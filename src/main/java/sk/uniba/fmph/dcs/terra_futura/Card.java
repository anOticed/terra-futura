package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private static final int MIN_POLLUTION_SPACES = 0;
    private static final int MAX_POLLUTION_SPACES = 3;

    /**
     * Creates a card with the given resources, effects, and pollution capacity.
     *
     * @param resources       resources stored on the card
     * @param upperEffect     effect on the upper part of the card, may be null
     * @param lowerEffect     effect on the lower part of the card, may be null
     * @param pollutionSpaces number of safe pollution spaces on the card (0..3)
     */
    public Card(final List<Resource> resources, final Effect upperEffect, final Effect lowerEffect, final int pollutionSpaces) {
        if (pollutionSpaces < MIN_POLLUTION_SPACES || pollutionSpaces > MAX_POLLUTION_SPACES) {
            throw new IllegalArgumentException("Invalid pollution spaces number");
        }
        this.resources = new ArrayList<>(Objects.requireNonNull(resources, "Resources cannot be null"));
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
        this(new ArrayList<>(), upperEffect, lowerEffect, pollutionSpaces);
    }

    /**
     * Checks whether the card currently has all requested resources and is usable.
     * Used before removing resources from the card.
     *
     * @param resources resources to be taken from the card
     * @return {@code true} if the card is active and contains all requested resources, {@code false} otherwise
     */
    public boolean canGetResources(final List<Resource> resources) {
        if (resources == null) {
            return false;
        }
        if (resources.isEmpty()) {
            return false;
        }
        if (isInactive()) {
            return false;
        }

        return containsMultiset(this.resources, resources);
    }

    /**
     * Removes requested resources from the card.
     * Assumes that {@link #canGetResources(List)} was checked before and returned {@code true}.
     *
     * @param resources resources to be taken from the card
     * @throws IllegalArgumentException if resources cannot be taken
     */
    public void getResources(final List<Resource> resources) {
        if (!canGetResources(resources)) {
            throw new IllegalArgumentException("Cannot get requested resources from the card");
        }

        for (Resource resource : resources) {
            // containsMultiset() guarantees that this will succeed
            this.resources.remove(resource);
        }
    }

    /**
     * Checks whether it is allowed to place the given resources on the card.
     *
     * @param resources resources to be placed on the card
     * @return {@code true} if the card is active and putting resources is allowed, {@code false} otherwise
     */
    public boolean canPutResources(final List<Resource> resources) {
        if (resources == null) {
            return false;
        }

        // On inactive cards we do not place any resources at all
        return !isInactive();
    }

    /**
     * Adds the given resources to the card.
     * Assumes that {@link #canPutResources(List)} was checked before and returned {@code true}.
     *
     * @param resources resources to be placed on the card
     * @throws IllegalArgumentException if resources cannot be placed
     */
    public void putResources(final List<Resource> resources) {
        if (!canPutResources(resources)) {
            throw new IllegalArgumentException("Cannot put resources on the card");
        }

        this.resources.addAll(resources);
    }

    /**
     * Checks whether the upper effect of the card can be applied with the
     * given input, output, and produced pollution.
     *
     * @param input     resources the player wants to pay
     * @param output    resources the player wants to gain
     * @param pollution pollution produced by the effect
     * @return {@code true} if the card is active, has an upper effect and
     * the effect can be applied, {@code false} otherwise
     */
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
        return checkEffect(input, output, pollution, upperEffect);
    }

    /**
     * Checks whether the lower effect of the card can be applied with the
     * given input, output, and produced pollution.
     *
     * @param input     resources the player wants to pay
     * @param output    resources the player wants to gain
     * @param pollution pollution produced by the effect
     * @return {@code true} if the card is active, has a lower effect and
     * the effect can be applied, {@code false} otherwise
     */
    public boolean checkLower(final List<Resource> input, final List<Resource> output, final int pollution) {
        return checkEffect(input, output, pollution, lowerEffect);
    }

    // Shared implementation for upper/lower effect checks
    private boolean checkEffect(final List<Resource> input, final List<Resource> output, final int pollution, final Effect effect) {
        if (effect == null) {
            return false;
        }
        if (input == null || output == null) {
            return false;
        }
        if (isInactive()) {
            return false;
        }

        return effect.check(input, output, pollution);
    }

    /**
     * @return {@code true} if the card has an upper or lower effect that provides assistance, {@code false} otherwise
     */
    public boolean hasAssistance() {
        return (upperEffect != null && upperEffect.hasAssistance()) || (lowerEffect != null && lowerEffect.hasAssistance());
    }

    /**
     * @return the state of the card as a JSON string
     */
    public String state() {
        JSONObject result = new JSONObject();

        JSONArray resourcesArray = new JSONArray();
        for (Resource resource : resources) {
            resourcesArray.put(resource.name());
        }
        result.put("resources", resourcesArray);
        result.put("inactive", isInactive());
        result.put("hasAssistance", hasAssistance());

        if (upperEffect != null) {
            result.put("upperEffect", upperEffect.state());
        }
        if (lowerEffect != null) {
            result.put("lowerEffect", lowerEffect.state());
        }

        result.put("pollutionSpaces", pollutionSpaces);

        return result.toString();
    }

    /**
     * @return {@code true} if the card is inactive (blocked), {@code false} otherwise
     */
    private boolean isInactive() {
        int pollutionCount = 0;
        for (Resource resource : resources) {
            if (resource == Resource.POLLUTION) {
                pollutionCount++;
            }
        }

        return pollutionCount > pollutionSpaces;
    }

    /**
     * @return {@code true} if the card is active (not polluted), {@code false} otherwise.
     */
    public boolean isActive() {
        return !isInactive();
    }

    /**
     * @return resources currently stored on the card
     */
    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    /**
     * Helper method to check whether 'available' contains all resources from 'requested'
     * as a multiset (with duplicates).
     *
     * @param available resources currently on the card
     * @param requested resources we want to pay
     * @return {@code true} if all requested resources can be found in available
     */
    private static boolean containsMultiset(final List<Resource> available, final List<Resource> requested) {
        List<Resource> copy = new ArrayList<>(available);
        for (Resource resource : requested) {
            if (!copy.remove(resource)) {
                return false;
            }
        }

        return true;
    }
}
