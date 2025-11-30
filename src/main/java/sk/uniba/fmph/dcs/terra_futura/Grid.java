package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Minimal public stub of the player's grid used by blue classes.
 * Real game logic (activation rules, patterns, etc.) lives in the
 * private red implementation.
 */
public final class Grid {
    private final Map<GridPosition, Card> cards = new HashMap<>();

    public Optional<Card> getCard(final GridPosition coordinate) {
        if (coordinate == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cards.get(coordinate));
    }

    public boolean canPutCard(final GridPosition coordinate) {
        if (coordinate == null) {
            return false;
        }
        return !cards.containsKey(coordinate);
    }

    public void putCard(final GridPosition coordinate, final Card card) {
        Objects.requireNonNull(coordinate, "coordinate cannot be null");
        Objects.requireNonNull(card, "card cannot be null");

        if (!canPutCard(coordinate)) {
            throw new IllegalStateException("Position already occupied: " + coordinate);
        }
        cards.put(coordinate, card);
    }

    public boolean canBeActivated(final GridPosition coordinate) {
        return coordinate != null && cards.containsKey(coordinate);
    }

    public void setActivated(final GridPosition coordinate) {
        // no-op in stub
    }

    public void endTurn() {
        // no-op in stub
    }

    public void setActivationPattern(final List<GridPosition> pattern) {
        // no-op in stub
    }

    public String state() {
        final JSONArray cardsArray = new JSONArray();
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            GridPosition pos = entry.getKey();
            Card card = entry.getValue();

            JSONObject obj = new JSONObject();
            obj.put("x", pos.getX());
            obj.put("y", pos.getY());
            obj.put("card", card.state());
            cardsArray.put(obj);
        }

        JSONObject result = new JSONObject();
        result.put("cards", cardsArray);
        return result.toString();
    }
}
