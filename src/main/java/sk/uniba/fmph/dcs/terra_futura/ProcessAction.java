package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Performs activation of a single card without Assistance.
 *
 * The class checks the card's effect and, if valid, delegates the actual
 * resource movement to {@link CardTransactionExecutor}.
 */
public final class ProcessAction {
    private final CardTransactionExecutor transactionExecutor;

    /**
     * Creates a ProcessAction using the given transaction executor.
     *
     * @param transactionExecutor helper responsible for moving resources on the grid
     */
    public ProcessAction(final CardTransactionExecutor transactionExecutor) {
        this.transactionExecutor = Objects.requireNonNull(transactionExecutor, "Transaction executor cannot be null");
    }

    /**
     * Creates a ProcessAction using the default {@link CardTransactionExecutor}.
     */
    public ProcessAction() {
        this(new CardTransactionExecutor());
    }

    /**
     * Activates a card on the grid using the given input/output resources and pollution.
     *
     * First, the method checks the card's effect. If the effect does not allow
     * the requested activation, no changes are applied to the grid.
     * If the effect approves the activation, the resource movement is delegated
     * to {@link CardTransactionExecutor}
     *
     * @param card      card whose upper effect should be used
     * @param grid      player's grid
     * @param inputs    resources to be paid (resource, position)
     * @param outputs   resources to be gained (resource, position)
     * @param pollution positions that receive pollution
     * @return {@code true} if activation is valid and all changes were applied, {@code false} otherwise
     */
    public boolean activateCard(
            final Card card,
            final Grid grid,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution) {

        if (card == null || grid == null || inputs == null || outputs == null || pollution == null) {
            return false;
        }

        final List<Resource> inputResources = extractResources(inputs);
        final List<Resource> outputResources = extractResources(outputs);
        final int pollutionCount = pollution.size();

        // check if the card effect allows this activation
        if (!card.check(inputResources, outputResources, pollutionCount)) {
            return false;
        }

        // if the effect is valid, perform the actual transaction on the grid
        return transactionExecutor.execute(grid, inputs, outputs, pollution);
    }

    /**
     * Extracts only the resources from (resource, position) pairs.
     *
     * @param resourcePairs list of (resource, position) pairs
     * @return flat list containing just the resources from the pairs
     */
    private static List<Resource> extractResources(final List<Pair<Resource, GridPosition>> resourcePairs) {
        final List<Resource> resources = new ArrayList<>();

        for (Pair<Resource, GridPosition> pair : resourcePairs) {
            resources.add(pair.getLeft());
        }

        return resources;
    }
}
