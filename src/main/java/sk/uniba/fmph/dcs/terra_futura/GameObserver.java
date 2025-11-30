package sk.uniba.fmph.dcs.terra_futura;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages observers interested in game state updates.
 * <p>
 * Each observer is registered under a player identifier and receives
 * string representations of the game state relevant to that player.
 */
public final class GameObserver {

    /**
     * Mapping from player id to the corresponding observer.
     */
    private final Map<Integer, TerraFuturaObserverInterface> observers = new HashMap<>();

    /**
     * Registers an observer for the given player identifier.
     * <p>
     * If an observer is already registered for this id, it will be replaced.
     *
     * @param playerId the identifier of the player
     * @param observer the observer to register; must not be {@code null}
     */
    public void addObserver(final int playerId, final TerraFuturaObserverInterface observer) {
        observers.put(playerId, Objects.requireNonNull(observer, "Observer cannot be null"));
    }

    /**
     * Registers an observer for the given player.
     * <p>
     * This is a convenience overload that extracts the id from the {@link Player}
     * record and delegates to {@link #addObserver(int, TerraFuturaObserverInterface)}.
     *
     * @param player   the player whose identifier is used for registration;
     *                 must not be {@code null}
     * @param observer the observer to register; must not be {@code null}
     */
    public void addObserver(final Player player, final TerraFuturaObserverInterface observer) {
        Objects.requireNonNull(player, "Player cannot be null");
        addObserver(player.id(), observer);
    }

    /**
     * Unregisters the observer associated with the given player identifier.
     * <p>
     * If no observer is registered for this id, the method has no effect.
     *
     * @param playerId the identifier of the player whose observer should be removed
     */
    public void removeObserver(final int playerId) {
        observers.remove(playerId);
    }

    /**
     * Forwards game state strings to registered observers.
     * <p>
     * For each entry in the provided map, the method looks up the observer
     * registered under the given player id and, if present, calls
     * {@link TerraFuturaObserverInterface#notify(String)} with the corresponding
     * state string. Player ids without a registered observer are ignored.
     *
     * @param newState a mapping from player id to the game state string
     *                 intended for that player; may be {@code null},
     *                 in which case the method does nothing
     */
    public void notifyAll(final Map<Integer, String> newState) {
        if (newState == null) {
            return;
        }

        for (Map.Entry<Integer, String> entry : newState.entrySet()) {
            final int playerId = entry.getKey();
            final String state = entry.getValue();

            final TerraFuturaObserverInterface observer = observers.get(playerId);
            if (observer != null) {
                observer.notify(state);
            }
        }
    }
}
