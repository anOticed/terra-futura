package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the ProcessActionAssistance class.
 */
public class ProcessActionAssistanceTest {

    /**
     * Fake effect used to control the result of {@link Effect#check}
     * and {@link Effect#hasAssistance} in tests.
     */
    private static final class FakeEffect implements Effect {
        private final boolean assistance;
        private final boolean checkResult;

        FakeEffect(final boolean assistance, final boolean checkResult) {
            this.assistance = assistance;
            this.checkResult = checkResult;
        }

        @Override
        public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
            return checkResult;
        }

        @Override
        public boolean hasAssistance() {
            return assistance;
        }

        @Override
        public String state() {
            return "FakeEffect";
        }
    }

    /**
     * If any of the mandatory arguments is {@code null},
     * activation should fail.
     */
    @Test
    public void returnsFalseWhenArgumentsAreNull() {
        ProcessActionAssistance action = new ProcessActionAssistance();

        boolean result = action.activateCard(
                null,
                null,
                0,
                null,
                null,
                null,
                null
        );

        assertFalse(result);
    }

    /**
     * If the card effect rejects the activation, the transaction
     * must not be considered successful.
     */
    @Test
    public void effectRejectionPreventsTransaction() {
        Effect effect = new FakeEffect(false, false);
        Card card = new Card(Collections.emptyList(), effect, null, 3);

        ProcessActionAssistance action = new ProcessActionAssistance();

        List<Pair<Resource, GridPosition>> emptyPairs = Collections.emptyList();
        List<GridPosition> noPollution = Collections.emptyList();

        boolean result = action.activateCard(
                card,
                new Grid(),
                0,
                null,
                emptyPairs,
                emptyPairs,
                noPollution
        );

        assertFalse(result);
    }

    /**
     * If the card has Assistance but assisting player/card are missing
     * or invalid, activation must fail.
     */
    @Test
    public void missingAssistanceDataFailsForAssistanceCard() {
        Effect effect = new FakeEffect(true, true);
        Card card = new Card(Collections.emptyList(), effect, null, 3);

        ProcessActionAssistance action = new ProcessActionAssistance();

        List<Pair<Resource, GridPosition>> emptyPairs = Collections.emptyList();
        List<GridPosition> noPollution = Collections.emptyList();

        boolean result = action.activateCard(
                card,
                new Grid(),
                -1,          // invalid player id
                null,        // missing assisting card
                emptyPairs,
                emptyPairs,
                noPollution
        );

        assertFalse(result);
    }

    /**
     * Effect accepts the activation, Assistance data is valid,
     * and the method returns {@code true}.
     *
     * We use empty resource/pollution lists so that the underlying
     * CardTransactionExecutor can succeed without extra setup.
     */
    @Test
    public void successfulActivationReturnsTrue() {
        Effect effect = new FakeEffect(true, true);
        Card card = new Card(Collections.emptyList(), effect, null, 3);

        ProcessActionAssistance action = new ProcessActionAssistance();

        Grid grid = new Grid();

        List<Pair<Resource, GridPosition>> inputs = Collections.emptyList();
        List<Pair<Resource, GridPosition>> outputs = Collections.emptyList();
        List<GridPosition> pollution = Collections.emptyList();

        boolean result = action.activateCard(
                card,
                grid,
                1,          // valid assisting player id
                card,       // some non-null assisting card
                inputs,
                outputs,
                pollution
        );

        assertTrue(result);
    }
}
