package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

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
     * Creates a ProcessAction using the default {@link CardTransactionExecutor}
     */
    public ProcessAction() {
        this(new CardTransactionExecutor());
    }

    public boolean activateCard(
            final Card card,
            final Grid grid,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution) {

        // a real implementation will be added later
        return false;
    }
}
