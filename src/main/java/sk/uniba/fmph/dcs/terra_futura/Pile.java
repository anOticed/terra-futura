package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Very simple stub implementation of a pile of cards.
 * Only the operations required by MoveCard are implemented.
 */
public final class Pile {

    public static final int MAX_VISIBLE_CARDS = 3;

    private final List<Card> cards = new ArrayList<>();

    /**
     * Returns the card with the given (1-based) index if it exists.
     */
    public Optional<Card> getCard(final int index) {
        int realIndex = index - 1; // convert 1-based to 0-based
        if (realIndex < 0 || realIndex >= cards.size()) {
            return Optional.empty();
        }
        return Optional.of(cards.get(realIndex));
    }

    /**
     * Removes the card with the given (1-based) index from the pile.
     * If the index is invalid, nothing happens.
     */
    public void takeCard(final int index) {
        int realIndex = index - 1;
        if (realIndex >= 0 && realIndex < cards.size()) {
            cards.remove(realIndex);
        }
    }

    /**
     * Removes the last card from the pile, if any.
     */
    public void removeLastCard() {
        if (!cards.isEmpty()) {
            cards.remove(cards.size() - 1);
        }
    }

    /**
     * Returns a simple textual representation of the pile.
     */
    public String state() {
        return "Pile(size=" + cards.size() + ")";
    }

    // Helper method for tests
    public void addCard(final Card card) {
        cards.add(card);
    }
}
