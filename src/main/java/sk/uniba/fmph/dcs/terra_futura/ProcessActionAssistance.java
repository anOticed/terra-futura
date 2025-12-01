package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes a complex card activation that involves Assistance.
 *
 * The class validates the requested activation (card effect and
 * assistance parameters) and, if valid, delegates the actual
 * resource and pollution movement to {@link CardTransactionExecutor}.
 */
public final class ProcessActionAssistance {
    private final CardTransactionExecutor transactionExecutor;

    /**
     * Creates a ProcessActionAssistance using the given transaction executor.
     *
     * @param transactionExecutor helper responsible for moving resources on the grid
     */
    ProcessActionAssistance(final CardTransactionExecutor transactionExecutor) {
        this.transactionExecutor = Objects.requireNonNull(
                transactionExecutor, "transactionExecutor cannot be null"
        );
    }

    /**
     * Creates a ProcessActionAssistance using the default {@link CardTransactionExecutor}.
     */
    public ProcessActionAssistance() {
        this(new CardTransactionExecutor());
    }

    /**
     * Activates a card on the grid using the given input/output resources and pollution.
     *
     * The method performs three main steps:
     * - validate arguments and, if needed, assistance metadata
     * - check the card's upper effect with aggregated input/output
     * - resources and produced pollution
     * If the effect approves the activation, the resource movement is delegated
     * to {@link CardTransactionExecutor}
     *
     * @param card            the card being activated
     * @param grid            player's grid
     * @param assistingPlayer identifier of the helping player
     * @param assistingCard   the card that provides Assistance
     * @param inputs          resources to be paid (resource, position)
     * @param outputs         resources to be gained (resource, position)
     * @param pollution       positions that receive pollution
     * @return {@code true} if activation is valid and all changes were applied, {@code false} otherwise
     */
    public boolean activateCard(
            final Card card,
            final Grid grid,
            final int assistingPlayer,
            final Card assistingCard,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution) {

        if (card == null || grid == null || inputs == null || outputs == null || pollution == null) {
            return false;
        }

        // validate assistance metadata only if the card
        // effect actually provides Assistance
        if (card.hasAssistance()) {
            if (assistingCard == null || assistingPlayer < 0) {
                return false;
            }
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
