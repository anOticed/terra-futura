package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

/**
 * Executes resource and pollution transfers between cards on the grid.
 *
 * This class is responsible only for moving resources once a card effect
 * has already approved the activation.
 */
public final class CardTransactionExecutor {

    /**
     * Validates and applies a transaction on the grid.
     * If any operation is invalid, the transaction is rejected and
     * the grid should not be modified.
     *
     * @param grid      player's grid
     * @param inputs    resources to be taken from cards (resource, position)
     * @param outputs   resources to be placed on cards (resource, position)
     * @param pollution positions that receive pollution
     * @return {@code true} if the transaction was successfully applied, {@code false} otherwise
     */
    public boolean execute(
            final Grid grid,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution) {

        if (grid == null || inputs == null || outputs == null || pollution == null) {
            return false;
        }

        final Map<GridPosition, List<Resource>> resourcesToRemove = groupResourcesByPosition(inputs);
        final Map<GridPosition, List<Resource>> resourcesToAdd = groupResourcesByPosition(outputs);
        final Map<GridPosition, Integer> pollutionByPosition = groupPollutionByPosition(pollution);

        final Map<GridPosition, List<Resource>> resourcesToAddWithPollution = combineOutputsAndPollution(resourcesToAdd, pollutionByPosition);

        // 1) validate removals (paying resources)
        if (!validateRemovals(grid, resourcesToRemove)) {
            return false;
        }

        // 2) validate additions (gaining resources + pollution)
        if (!validateAdditions(grid, resourcesToAddWithPollution)) {
            return false;
        }

        // 3) apply changes: first remove, then add resources, then add pollution
        applyRemovals(grid, resourcesToRemove);
        applyAdditions(grid, resourcesToAdd);
        applyPollution(grid, pollutionByPosition);

        return true;
    }

    /**
     * Groups resource pairs by grip position.
     *
     * @param resourcePairs list of (resource, position) pairs
     * @return map from grid position to a list of resources
     */
    private static Map<GridPosition, List<Resource>> groupResourcesByPosition(final List<Pair<Resource, GridPosition>> resourcePairs) {
        final Map<GridPosition, List<Resource>> grouped = new HashMap<>();

        for (Pair<Resource, GridPosition> pair : resourcePairs) {
            final GridPosition position = pair.getRight();
            grouped.computeIfAbsent(position, key -> new ArrayList<>()).add(pair.getLeft());
        }

        return grouped;
    }

    /**
     * Counts pollution per grid position.
     *
     * @param pollution list of positions that receive pollution
     * @return map from grid position to amount of pollution
     */
    private static Map<GridPosition, Integer> groupPollutionByPosition(final List<GridPosition> pollution) {
        final Map<GridPosition, Integer> grouped = new HashMap<>();

        for (GridPosition position : pollution) {
            final int current = grouped.getOrDefault(position, 0);
            grouped.put(position, current + 1);
        }

        return grouped;
    }

    /**
     * Combines normal output resources and pollution into a single
     * multiset of resources per card.
     *
     * @param outputsByPosition   resources to add per position (without pollution)
     * @param pollutionByPosition amount of pollution per position
     * @return map from grid position to all resources to add, including {@link Resource#POLLUTION}
     */
    private static Map<GridPosition, List<Resource>> combineOutputsAndPollution(
            final Map<GridPosition, List<Resource>> outputsByPosition,
            final Map<GridPosition, Integer> pollutionByPosition) {
        final Map<GridPosition, List<Resource>> combined = new HashMap<>();

        // copy normal outputs
        for (Map.Entry<GridPosition, List<Resource>> entry : outputsByPosition.entrySet()) {
            combined.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // add pollution as Resources.POLLUTION
        for (Map.Entry<GridPosition, Integer> entry : pollutionByPosition.entrySet()) {
            final GridPosition position = entry.getKey();
            final int count = entry.getValue();

            final List<Resource> list = combined.computeIfAbsent(position, key -> new ArrayList<>());
            for (int i = 0; i < count; i++) {
                list.add(Resource.POLLUTION);
            }
        }
        return combined;
    }

    /**
     * Checks that all requested removals are possible.
     *
     * @param grid              player's grid
     * @param resourcesToRemove resources to remove per position
     * @return {@code true} if all removals are allowed, {@code false} otherwise
     */
    private static boolean validateRemovals(final Grid grid, final Map<GridPosition, List<Resource>> resourcesToRemove) {
        for (Map.Entry<GridPosition, List<Resource>> entry : resourcesToRemove.entrySet()) {
            final GridPosition position = entry.getKey();
            final List<Resource> toRemove = entry.getValue();

            final Optional<Card> cardOptional = grid.getCard(position);
            if (!cardOptional.isPresent()) {
                return false;
            }

            final Card card = cardOptional.get();
            if (!card.canGetResources(toRemove)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks that all requested additions (resources and pollution) are possible.
     *
     * @param grid           player's grid
     * @param resourcesToAdd resources to add per position
     * @return {@code true} if all additions are allowed, {@code false} otherwise
     */
    private static boolean validateAdditions(final Grid grid, final Map<GridPosition, List<Resource>> resourcesToAdd) {
        for (Map.Entry<GridPosition, List<Resource>> entry : resourcesToAdd.entrySet()) {
            final GridPosition position = entry.getKey();
            final List<Resource> toAdd = entry.getValue();

            final Optional<Card> cardOptional = grid.getCard(position);
            if (!cardOptional.isPresent()) {
                return false;
            }

            final Card card = cardOptional.get();
            if (!card.canPutResources(toAdd)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Applies resource removals (payments).
     *
     * @param grid              player's grid
     * @param resourcesToRemove resources to remove per position
     */
    private static void applyRemovals(final Grid grid, final Map<GridPosition, List<Resource>> resourcesToRemove) {
        for (Map.Entry<GridPosition, List<Resource>> entry : resourcesToRemove.entrySet()) {
            final GridPosition position = entry.getKey();
            final List<Resource> toAdd = entry.getValue();

            final Optional<Card> cardOptional = grid.getCard(position);
            if (cardOptional.isPresent()) {
                cardOptional.get().getResources(toAdd);
            }
        }
    }

    /**
     * Applies resource additions (gains), without pollution.
     *
     * @param grid           player's grid
     * @param resourcesToAdd resources to add per position
     */
    private static void applyAdditions(final Grid grid, final Map<GridPosition, List<Resource>> resourcesToAdd) {
        for (Map.Entry<GridPosition, List<Resource>> entry : resourcesToAdd.entrySet()) {
            final GridPosition position = entry.getKey();
            final List<Resource> toAdd = entry.getValue();

            final Optional<Card> cardOptional = grid.getCard(position);
            if (cardOptional.isPresent()) {
                cardOptional.get().putResources(toAdd);
            }
        }
    }

    /**
     * Applies pollution as {@link Resource#POLLUTION}.
     *
     * @param grid                player's grid
     * @param pollutionByPosition amount of pollution per position
     */
    private static void applyPollution(final Grid grid, final Map<GridPosition, Integer> pollutionByPosition) {
        for (Map.Entry<GridPosition, Integer> entry : pollutionByPosition.entrySet()) {
            final GridPosition position = entry.getKey();
            final int count = entry.getValue();

            final Optional<Card> cardOptional = grid.getCard(position);
            if (cardOptional.isPresent() && count > 0) {
                final List<Resource> pollutionResources = Collections.nCopies(count, Resource.POLLUTION);
                cardOptional.get().putResources(pollutionResources);
            }
        }
    }
}
