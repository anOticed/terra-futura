package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the ProcessAction class.
 */
public class ProcessActionTest {

    /**
     * Fake Effect implementation used to capture parameters passed
     * from ProcessAction and control the returned result.
     */
    private static final class FakeEffect implements Effect {
        private final boolean result;

        private List<Resource> lastInput;
        private List<Resource> lastOutput;
        private int lastPollution;

        FakeEffect(final boolean result) {
            this.result = result;
        }

        @Override
        public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
            lastInput = input;
            lastOutput = output;
            lastPollution = pollution;
            return result;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public String state() {
            return "FakeEffect{" + result + "}";
        }
    }

    // Helper method to create a card with the given upper effect.
    private static Card createCardWithEffect(final Effect upperEffect) {
        return new Card(Collections.emptyList(), upperEffect, null, 1);
    }

    /**
     * If any of the arguments is null, activateCard() should return false.
     */
    @Test
    public void activateCardReturnsFalseWhenArgumentsAreNull() {
        ProcessAction action = new ProcessAction();
        Card card = createCardWithEffect(new FakeEffect(true));
        Grid grid = new Grid();

        List<Pair<Resource, GridPosition>> emptyPairs = Collections.emptyList();
        List<GridPosition> emptyPositions = Collections.emptyList();

        assertFalse(action.activateCard(null, grid, emptyPairs, emptyPairs, emptyPositions));
        assertFalse(action.activateCard(card, null, emptyPairs, emptyPairs, emptyPositions));
        assertFalse(action.activateCard(card, grid, null, emptyPairs, emptyPositions));
        assertFalse(action.activateCard(card, grid, emptyPairs, null, emptyPositions));
        assertFalse(action.activateCard(card, grid, emptyPairs, emptyPairs, null));
    }

    /**
     * If the card effect rejects the activation, no transaction is performed
     * and the method returns false.
     */
    @Test
    public void activateCardReturnsFalseWhenEffectRejectsActivation() {
        FakeEffect effect = new FakeEffect(false);
        Card card = createCardWithEffect(effect);
        ProcessAction action = new ProcessAction();
        Grid grid = new Grid();

        List<Pair<Resource, GridPosition>> inputs = Collections.singletonList(Pair.of(Resource.GREEN, null));
        List<Pair<Resource, GridPosition>> outputs = Collections.singletonList(Pair.of(Resource.MONEY, null));
        List<GridPosition> pollution = Collections.singletonList(null);

        boolean result = action.activateCard(card, grid, inputs, outputs, pollution);

        assertFalse(result);

        // effect must still see the aggregated resources and pollution count
        assertEquals(1, effect.lastInput.size());
        assertEquals(Resource.GREEN, effect.lastInput.get(0));

        assertEquals(1, effect.lastOutput.size());
        assertEquals(Resource.MONEY, effect.lastOutput.get(0));

        assertEquals(1, effect.lastPollution);
    }

    /**
     * If the effect approves the activation and the transaction can be performed,
     * the method returns true.
     *
     * In this test, no resources or pollution are moved, so the grid is not used
     * by CardTransactionExecutor.
     */
    @Test
    public void activateCardReturnsTrueWhenEffectApprovesAndTransactionSucceeds() {
        FakeEffect effect = new FakeEffect(true);
        Card card = createCardWithEffect(effect);
        ProcessAction action = new ProcessAction();
        Grid grid = new Grid();

        List<Pair<Resource, GridPosition>> emptyPairs = Collections.emptyList();
        List<GridPosition> emptyPositions = Collections.emptyList();

        boolean result = action.activateCard(card, grid, emptyPairs, emptyPairs, emptyPositions);

        assertTrue(result);

        // the effect should see empty input/output and zero pollution
        assertNotNull(effect.lastInput);
        assertNotNull(effect.lastOutput);
        assertEquals(0, effect.lastInput.size());
        assertEquals(0, effect.lastOutput.size());
        assertEquals(0, effect.lastPollution);
    }

    /**
     * If the effect approves the activation but the transaction fails
     * (for example, because the grid does not contain the required cards),
     * the method returns false.
     */
    @Test
    public void activateCardReturnsFalseWhenTransactionFails() {
        FakeEffect effect = new FakeEffect(true);
        Card card = createCardWithEffect(effect);
        ProcessAction action = new ProcessAction();
        Grid grid = new Grid();

        List<Pair<Resource, GridPosition>> inputs = Collections.singletonList(Pair.of(Resource.GREEN, null));
        List<Pair<Resource, GridPosition>> outputs = Collections.emptyList();
        List<GridPosition> pollution = Collections.emptyList();

        boolean result = action.activateCard(card, grid, inputs, outputs, pollution);

        // effect approves, but CardTransactionExecutor will fail (no card on the grid)
        assertFalse(result);
    }
}
