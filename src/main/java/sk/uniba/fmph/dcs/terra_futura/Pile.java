package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Minimal public stub of the Pile concept used by blue classes.
 * Real game logic lives in the private red implementation.
 */
public final class Pile {
    public static final int MAX_VISIBLE_CARDS = 4;
    private final List<Card> visibleCards;
    private final List<Card> hiddenCards;


    public Pile(final List<Card> visibleCards, final List<Card> hiddenCards) {
        this.visibleCards = new ArrayList<>(visibleCards == null ? Collections.emptyList() : visibleCards);
        this.hiddenCards = new ArrayList<>(hiddenCards == null ? Collections.emptyList() : hiddenCards);
    }

    public Optional<Card> getCard(final int index) {
        if (index < 1 || index > visibleCards.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(visibleCards.get(index - 1));
    }

    public void takeCard(final int index) {
        if (index < 1 || index > visibleCards.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        visibleCards.remove(index - 1);
    }

    public void removeLastCard() {
        if (!visibleCards.isEmpty()) {
            visibleCards.remove(visibleCards.size() - 1);
        }
    }

    public String state() {
        return "Pile{visible=" + visibleCards.size() + ", hidden=" + hiddenCards.size() + "}";
    }
}
