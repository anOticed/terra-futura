package sk.uniba.fmph.dcs.terra_futura;

public final class GridPosition {
    private final int x;
    private final int y;

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