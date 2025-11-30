package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Unit tests for the MoveCard class.
 */
public class MoveCardTest {


    // Helper method that creates a simple card
    private static Card card(final Resource resource) {
        return new Card(Collections.singletonList(resource), null, null, 0);
    }

    /**
     * Successful scenario: the selected card exists in the pile,
     * the target grid position is free, and the card is moved.
     */
    @Test
    public void moveCardMovesCardFromPileToGrid() {
        Card card1 = card(Resource.GREEN);
        Card card2 = card(Resource.RED);
        Card hidden = card(Resource.YELLOW);

        Pile pile = new Pile(Arrays.asList(card1, card2), Collections.singletonList(hidden));

        Grid grid = new Grid();
        GridPosition gridPosition = new GridPosition(1, 0);
        MoveCard moveCard = new MoveCard(2); // take card2

        boolean result = moveCard.moveCard(pile, gridPosition, grid);
        assertTrue(result);

        // card2 must be on the grid at the gridPosition
        Optional<Card> placed = grid.getCard(gridPosition);
        assertTrue(placed.isPresent());
        assertSame(card2, placed.get());

        // pile should no longer have card2 at index 2
        Optional<Card> visible1 = pile.getCard(1);
        Optional<Card> visible2 = pile.getCard(2);

        assertTrue(visible1.isPresent());
        assertNotSame(card2, visible1.get());
        // card2 must not be there
        visible2.ifPresent(card -> assertNotSame(card2, card));
    }

    /**
     * If the grid position is already occupied, nothing is changed
     * and the method returns {@code false}.
     */
    @Test
    public void moveCardFailsWhenGridPositionOccupied() {
        Card card1 = card(Resource.GREEN);
        Card card2 = card(Resource.RED);
        Pile pile = new Pile(Arrays.asList(card1, card2), Collections.emptyList());

        Grid grid = new Grid();
        GridPosition gridPosition = new GridPosition(0, 0);

        // put some card on the gridPosition beforehand
        Card existing = card(Resource.BULB);
        grid.putCard(gridPosition, existing);
        MoveCard moveCard = new MoveCard(1);

        boolean result = moveCard.moveCard(pile, gridPosition, grid);
        assertFalse(result);

        // the grid must still contain the original card
        Optional<Card> placed = grid.getCard(gridPosition);
        assertTrue(placed.isPresent());
        assertSame(existing, placed.get());

        // pile must still contain card1 at index 1
        Optional<Card> visible1 = pile.getCard(1);
        assertTrue(visible1.isPresent());
        assertSame(card1, visible1.get());
    }

    /**
     * If there is no card at the selected index in the pile,
     * the method returns {@code false} and the grid is unchanged.
     */
    @Test
    public void moveCardFailsWhenSelectedCardMissing() {
        Pile pile = new Pile(Collections.emptyList(), Collections.emptyList());
        Grid grid = new Grid();
        GridPosition gridPosition = new GridPosition(0, 1);
        MoveCard moveCard = new MoveCard(1);

        boolean result = moveCard.moveCard(pile, gridPosition, grid);
        assertFalse(result);
        assertFalse(grid.getCard(gridPosition).isPresent());
    }

    /**
     * Constructor must reject invalid card indices.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsInvalidIndex() {
        new MoveCard(0);
    }
}
