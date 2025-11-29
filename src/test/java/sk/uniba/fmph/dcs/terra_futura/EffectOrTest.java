package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the EffectOr composite effect.
 */
public class EffectOrTest {

    /**
     * check() should return true if at least one of the sub-effects matches.
     */
    @Test
    public void checkReturnsTrueIfAnySubEffectMatches() {
        Effect effect1 = new TransformationFixed(List.of(Resource.GREEN), List.of(Resource.MONEY), 0);
        Effect effect2 = new ArbitraryBasic(0, List.of(Resource.CAR), 0);

        Effect orEffect = new EffectOr(List.of(effect1, effect2));

        assertTrue(orEffect.check(List.of(), List.of(Resource.CAR), 0));
    }

    /**
     * check() should return false if none of the sub-effects matches.
     */
    @Test
    public void checkReturnsFalseIfNoSubEffectMatches() {
        Effect effect1 = new TransformationFixed(List.of(Resource.GREEN), List.of(Resource.MONEY), 0);
        Effect effect2 = new ArbitraryBasic(0, List.of(Resource.CAR), 0);

        Effect orEffect = new EffectOr(List.of(effect1, effect2));

        assertFalse(orEffect.check(List.of(Resource.RED), List.of(Resource.GEAR), 1));
    }

    /**
     * hasAssistance() should return false if no sub-effect has assistance.
     */
    @Test
    public void hasAssistanceReturnsFalseIfNoSubEffectHasAssistance() {
        Effect effect1 = new TransformationFixed(List.of(Resource.GREEN), List.of(Resource.MONEY), 0);
        Effect effect2 = new ArbitraryBasic(0, List.of(Resource.CAR), 0);

        Effect orEffect = new EffectOr(List.of(effect1, effect2));

        assertFalse(orEffect.hasAssistance());
    }

    /**
     * state() should return a string representation of all sub-effects.
     */
    @Test
    public void stateReturnsStringRepresentationOfAllSubEffects() {
        Effect effect1 = new TransformationFixed(List.of(Resource.GREEN), List.of(Resource.MONEY), 0);
        Effect effect2 = new ArbitraryBasic(0, List.of(Resource.CAR), 0);

        Effect orEffect = new EffectOr(List.of(effect1, effect2));

        assertEquals("EffectOr{TransformationFixed{from=[GREEN], to=[MONEY], pollution=0}, ArbitraryBasic{from=0, to=[CAR], pollution=0}}", orEffect.state());
    }
}
