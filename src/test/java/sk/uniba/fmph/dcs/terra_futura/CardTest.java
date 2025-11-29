package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the Card class.
 */
public class CardTest {
    private static final class FakeEffect implements Effect {
        private final boolean checkResult;
        private final boolean assistance;
        private final String state;

        FakeEffect(final boolean checkResult, final boolean assistance, final String state) {
            this.checkResult = checkResult;
            this.assistance = assistance;
            this.state = state;
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
            return state;
        }
    }

    /**
     * pollutionSpaces must be within the allowed range.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativePollutionSpaces() {
        new Card(null, null, -1);
    }

    /**
     * pollutionSpaces must be within the allowed range.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsTooLargePollutionSpaces() {
        new Card(null, null, 4);
    }

    /**
     * canGetResources/getResources respect multiset semantics and inactivity.
     */
    @Test
    public void canGetAndGetResources() {
        Card card = new Card(Arrays.asList(Resource.GREEN, Resource.GREEN, Resource.RED), null, null, 1);

        // enough resources for 2x GREEN
        assertTrue(card.canGetResources(Arrays.asList(Resource.GREEN, Resource.GREEN)));
        card.getResources(Arrays.asList(Resource.GREEN, Resource.GREEN));

        // now only RED remains, cannot pay another GREEN
        assertFalse(card.canGetResources(Collections.singletonList(Resource.GREEN)));

        // make card inactive
        card.putResources(Arrays.asList(Resource.POLLUTION, Resource.POLLUTION));

        // once inactive, resources cannot be used
        assertFalse(card.canGetResources(Collections.singletonList(Resource.RED)));
    }

    /**
     * canPutResources blocks any new resources once the card becomes inactive.
     */
    @Test
    public void canPutResourcesBlockOnInactiveCard() {
        Card card = new Card(null, null, 1);

        // still active: can place pollution
        assertTrue(card.canPutResources(Collections.singletonList(Resource.POLLUTION)));
        card.putResources(Collections.singletonList(Resource.POLLUTION));

        // still active: can place second pollution, which will make the card inactive
        assertTrue(card.canPutResources(Collections.singletonList(Resource.POLLUTION)));
        card.putResources(Collections.singletonList(Resource.POLLUTION));

        // once inactive, no new resources can be placed
        assertFalse(card.canPutResources(Collections.singletonList(Resource.GREEN)));
    }

    /**
     * check() delegates to the upper effect and respects inactivity.
     */
    @Test
    public void checkUsesUpperEffectAndBlocksWhenInactive() {
        Effect upper = new FakeEffect(true, false, "Upper");
        Card card = new Card(upper, null, 1);

        // active card: effect returns true -> card.check() returns true
        assertTrue(card.check(Collections.singletonList(Resource.GREEN), Collections.singletonList(Resource.MONEY), 0));

        // make card inactive
        card.putResources(Arrays.asList(Resource.POLLUTION, Resource.POLLUTION));

        // inactive card: card.check() must return false regardless of the effect's result
        assertFalse(card.check(Collections.singletonList(Resource.GREEN), Collections.singletonList(Resource.MONEY), 0));
    }

    /**
     * hasAssistance() returns true if any of the effects has assistance.
     */
    @Test
    public void hasAssistanceTrueIfAnyEffectHasIt() {
        Effect upperWithAssistance = new FakeEffect(false, true, "Upper");
        Effect lowerWithoutAssistance = new FakeEffect(false, false, "Lower");

        Card card = new Card(upperWithAssistance, lowerWithoutAssistance, 1);
        assertTrue(card.hasAssistance());

        Card card2 = new Card(null, lowerWithoutAssistance, 1);
        assertFalse(card2.hasAssistance());
    }

    /**
     * state() contains basic information about the card.
     */
    @Test
    public void stateContainsBasicInfo() {
        Effect upper = new FakeEffect(false, false, "Upper");
        Card card = new Card(Arrays.asList(Resource.GREEN, Resource.POLLUTION), upper, null, 1);

        String state = card.state();
        System.out.println(state);

        assertTrue(state.contains("GREEN"));
        assertTrue(state.contains("POLLUTION"));
        assertTrue(state.contains("pollutionSpaces"));
        assertTrue(state.contains("Upper"));
    }
}
