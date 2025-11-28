package sk.uniba.fmph.dcs.terra_futura;

/**
 * Coordinates of a card in the grid.
 *
 * Coordinates are in the range [-2, 2] for both axes.
 * The (0, 0) position denotes the starting card.
 */

public final class GridPosition {
    private final int x;
    private final int y;

    /**
     * Creates a new grid position.
     *
     * @param x x-coordinate of the position.
     * @param y y-coordinate of the position.
     * @throws IllegalArgumentException if either coordinate is out of range.
     */
    public GridPosition(final int x, final int y) {
        if (x < -2 || x > 2 || y < -2 || y > 2) {
            throw new IllegalArgumentException("GridPosition must be in range [-2, 2] for both x and y coordinates.");
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}