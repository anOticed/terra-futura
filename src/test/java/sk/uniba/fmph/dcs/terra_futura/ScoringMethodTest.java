package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Unit tests for the ScoringMethod class.
 */
public class ScoringMethodTest {

    // Helper method that creates a simple card
    private static Card card(final Resource... resources) {
        return new Card(Arrays.asList(resources), null, null, 0);
    }

    /**
     * Constructor must reject an empty resource pattern.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsEmptyResources() {
        Grid grid = new Grid();
        new ScoringMethod(Collections.emptyList(), new Points(1), grid);
    }

    /**
     * Constructor must reject null resources.
     */
    @Test(expected = NullPointerException.class)
    public void constructorRejectsNullResources() {
        Grid grid = new Grid();
        new ScoringMethod(null, new Points(1), grid);
    }

    /**
     * Constructor must reject null pointsPerCombination.
     */
    @Test(expected = NullPointerException.class)
    public void constructorRejectsNullPoints() {
        Grid grid = new Grid();
        new ScoringMethod(List.of(Resource.GREEN), null, grid);
    }

    /**
     * Constructor must reject null grid.
     */
    @Test(expected = NullPointerException.class)
    public void constructorRejectsNullGrid() {
        new ScoringMethod(List.of(Resource.GREEN), new Points(1), null);
    }

    /**
     * Calculates basic score from resources on the grid when
     * no full scoring combination can be formed.
     *
     * Resources:
     *  - (0,0): GREEN + CAR: 1 + 6 = 7
     *  - (1,0): BULB: 5
     * Total basic score: 12 points.
     *
     * Scoring pattern: [YELLOW] with some pointsPerCombination,
     * but there are no YELLOW resources, so the extra bonus is 0.
     */
    @Test
    public void calculatesBasicScoreWithoutCombinations() {
        Grid grid = new Grid();
        grid.putCard(new GridPosition(0, 0), card(Resource.GREEN, Resource.CAR));
        grid.putCard(new GridPosition(1, 0), card(Resource.BULB));

        Points perCombination = new Points(4);
        ScoringMethod method = new ScoringMethod(List.of(Resource.YELLOW), perCombination, grid);

        method.selectThisMethodAndCalculate();

        Optional<Points> totalOpt = method.getCalculatedTotal();
        assertTrue(totalOpt.isPresent());
        assertEquals(12, totalOpt.get().value());
    }

    /**
     * Adds extra points for each full combination of the pattern.
     *
     * Grid resources:
     *  - (0,0): GREEN, GREEN, RED
     *  - (1,0): GREEN
     * Totals: GREEN = 3, RED = 1.
     *
     * Basic score:
     *  GREEN: 3 * 1 = 3
     *  RED: 1 * 1 = 1
     *  Total: 4 points.
     *
     * Pattern: [GREEN, GREEN, RED]
     * pointsPerCombination = 4
     * We can form exactly 1 full combination → +4 points.
     *
     * Final total: 4 (basic) + 4 (combination) = 8.
     */
    @Test
    public void addsExtraPointsForFullCombinations() {
        Grid grid = new Grid();
        grid.putCard(new GridPosition(0, 0), card(Resource.GREEN, Resource.GREEN, Resource.RED));
        grid.putCard(new GridPosition(1, 0), card(Resource.GREEN));

        Points perCombination = new Points(4);
        ScoringMethod method = new ScoringMethod(List.of(Resource.GREEN, Resource.GREEN, Resource.RED), perCombination, grid);

        method.selectThisMethodAndCalculate();

        Optional<Points> totalOpt = method.getCalculatedTotal();
        assertTrue(totalOpt.isPresent());
        assertEquals(8, totalOpt.get().value());
    }

    /**
     * Pollution should reduce the score by -1 for each polluted card.
     *
     * Grid:
     *  - (0,0): POLLUTION (only)
     *  - (1,0): GREEN
     *
     * Basic score:
     *  POLLUTION: -1
     *  GREEN: 1
     *  Total: 0 total.
     *
     * Pattern: [BULB] (no BULB on grid), so no extra points.
     * Final total: 0.
     */
    @Test
    public void pollutionReducesScoreByOnePerCard() {
        Grid grid = new Grid();
        grid.putCard(new GridPosition(0, 0), card(Resource.POLLUTION));
        grid.putCard(new GridPosition(1, 0), card(Resource.GREEN));

        Points perCombination = new Points(3);
        ScoringMethod method = new ScoringMethod(List.of(Resource.BULB), perCombination, grid);

        method.selectThisMethodAndCalculate();

        Optional<Points> totalOpt = method.getCalculatedTotal();
        assertTrue(totalOpt.isPresent());
        assertEquals(0, totalOpt.get().value());
    }

    /**
     * Verifies that {@link ScoringMethod#state()} returns JSON with
     * required fields and includes calculatedTotal only after
     * calculation is performed.
     */
    @Test
    public void stateReflectsCalculatedTotal() {
        Grid grid = new Grid();
        grid.putCard(new GridPosition(0, 0), card(Resource.GREEN));

        Points perCombination = new Points(2);
        ScoringMethod method = new ScoringMethod(List.of(Resource.GREEN), perCombination, grid);

        // Before calculation: no calculatedTotal field.
        JSONObject jsonBefore = new JSONObject(method.state());
        assertTrue(jsonBefore.has("resources"));
        assertTrue(jsonBefore.has("pointsPerCombination"));
        assertFalse(jsonBefore.has("calculatedTotal"));

        // After calculation: calculatedTotal must be present.
        method.selectThisMethodAndCalculate();
        JSONObject jsonAfter = new JSONObject(method.state());
        assertTrue(jsonAfter.has("calculatedTotal"));
        assertEquals(method.getCalculatedTotal().orElseThrow().value(), jsonAfter.getInt("calculatedTotal"));
    }
}
