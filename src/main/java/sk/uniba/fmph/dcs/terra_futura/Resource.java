package sk.uniba.fmph.dcs.terra_futura;

/**
 * Enumeration of all resource types used in the game.
 */
public enum Resource {
    GREEN,
    RED,
    YELLOW,
    BULB,
    GEAR,
    CAR,
    MONEY,
    POLLUTION;

    private static final int VALUE_BASIC_RESOURCE = 1;      // GREEN, RED, YELLOW
    private static final int VALUE_ADVANCED_RESOURCE = 5;   // BULB, GEAR
    private static final int VALUE_CAR_RESOURCE = 6;        // CAR
    private static final int VALUE_MONEY_RESOURCE = 0;      // MONEY
    private static final int VALUE_POLLUTION_RESOURCE = -1; // POLLUTION

    /**
     * @return point value of this resource
     */
    public int getValue() {
        return switch (this) {
            case GREEN, RED, YELLOW -> VALUE_BASIC_RESOURCE;
            case BULB, GEAR -> VALUE_ADVANCED_RESOURCE;
            case CAR -> VALUE_CAR_RESOURCE;
            case MONEY -> VALUE_MONEY_RESOURCE;
            case POLLUTION -> VALUE_POLLUTION_RESOURCE;
        };
    }
}
