package sk.uniba.fmph.dcs.terra_futura;

import java.util.Optional;

/**
 * Performs a complex action: moves a card from a {@link Pile}
 * to the player's {@link Grid}.
 */
public final class MoveCard {
    private final int cardIndex;

    /**
     * Creates a MoveCard action for the given card index.
     *
     * @param cardIndex index of the card in the pile (1..{@link Pile#MAX_VISIBLE_CARDS})
     * @throws IllegalArgumentException if the index is outside the valid range
     */
    public MoveCard(final int cardIndex) {
        if (cardIndex < 1 || cardIndex > Pile.MAX_VISIBLE_CARDS) {
            throw new IllegalArgumentException("invalid card index: " + cardIndex);
        }
        this.cardIndex = cardIndex;
    }

    /**
     * Takes a card from the given pile and puts it onto the grid.
     *
     * @param pile           pile from which the card is taken
     * @param grid           player's grid
     * @param gridCoordinate coordinate where the card should be placed
     * @return {@code true} if the card was moved successfully,
     *         {@code false} otherwise
     */
    public boolean moveCard(
            final Pile pile,
            final GridPosition gridCoordinate,
            final Grid grid

    ) {
        if (pile == null || gridCoordinate == null || grid == null) {
            return false;
        }
        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        // check that the selected card exists in the pile
        final Optional<Card> cardOptional = pile.getCard(cardIndex);
        if (!cardOptional.isPresent()) {
            return false;
        }

        final Card card = cardOptional.get();
        pile.takeCard(cardIndex);
        grid.putCard(gridCoordinate, card);

        return true;
    }
}
