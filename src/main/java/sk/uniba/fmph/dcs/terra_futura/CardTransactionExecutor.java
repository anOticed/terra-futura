package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

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

        // implementation will be added later
        return false;
    }
}
