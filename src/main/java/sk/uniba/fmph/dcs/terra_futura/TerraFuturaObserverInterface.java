package sk.uniba.fmph.dcs.terra_futura;

/**
 * Observer of Terra Futura game state changes.
 */
public interface TerraFuturaObserverInterface {

    /**
     * Notifies this observer with the current game state.
     *
     * @param gameState string representation of the game state
     */
    void notify(String gameState);
}