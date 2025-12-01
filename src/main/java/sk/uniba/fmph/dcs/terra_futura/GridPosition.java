package sk.uniba.fmph.dcs.terra_futura;

import java.util.Objects;

/**
 * Coordinates of a card in the grid.
 *
 * Coordinates are in the range [-2, 2] for both axes.
 * The (0, 0) position denotes the starting card.
 */
public final class GridPosition {
    private final int x;
    private final int y;

    private final int leftConstraint = -2;
    private final int rightConstraint = 2;

    /**
     * Creates a new grid position.
     *
     * @param x x-coordinate of the position.
     * @param y y-coordinate of the position.
     * @throws IllegalArgumentException if either coordinate is out of range.
     */
    public GridPosition(final int x, final int y) {
        if (x < leftConstraint || x > rightConstraint || y < leftConstraint || y > rightConstraint) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GridPosition)) {
            return false;
        }

        GridPosition other = (GridPosition) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
