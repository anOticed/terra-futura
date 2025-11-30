package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.Map;

public class GameObserverTest {

    private static final class TestObserver implements TerraFuturaObserverInterface {
        String lastState;

        @Override
        public void notify(final String gameState) {
            lastState = gameState;
        }
    }

    // Verifies that each player receives the correct state string.
    @Test
    public void forwardsStringsToCorrectObservers() {
        GameObserver gameObserver = new GameObserver();

        Player p1 = new Player(1);
        Player p2 = new Player(2);

        TestObserver o1 = new TestObserver();
        TestObserver o2 = new TestObserver();

        gameObserver.addObserver(p1, o1);
        gameObserver.addObserver(p2, o2);

        gameObserver.notifyAll(Map.of(
                1, "state-for-1",
                2, "state-for-2"
        ));

        assertEquals("state-for-1", o1.lastState);
        assertEquals("state-for-2", o2.lastState);
    }

    // Ensures that missing observers do not cause failures or affect others.
    @Test
    public void ignoresPlayersWithoutObservers() {
        GameObserver gameObserver = new GameObserver();

        Player p1 = new Player(1);
        TestObserver o1 = new TestObserver();
        gameObserver.addObserver(p1, o1);

        // state is present for player 1 and 2, but observer only for 1
        gameObserver.notifyAll(Map.of(
                1, "state-for-1",
                2, "state-for-2"
        ));

        assertEquals("state-for-1", o1.lastState);
        // nothing to assert for player 2, we only check that no exception is thrown
    }

    // Checks that removed observers no longer receive notifications.
    @Test
    public void removeObserverStopsNotifications() {
        GameObserver gameObserver = new GameObserver();

        Player p1 = new Player(1);
        TestObserver o1 = new TestObserver();
        gameObserver.addObserver(p1, o1);

        gameObserver.removeObserver(1);
        gameObserver.notifyAll(Map.of(1, "new-state"));

        assertNull(o1.lastState);
    }
}
