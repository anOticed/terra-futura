package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents one end–game scoring option chosen from a scoring card.
 */
public final class ScoringMethod {
    private final List<Resource> resources;
    private final Points pointsPerCombination;
    private Optional<Points> calculatedTotal = Optional.empty();
    private final Grid grid;

    private static final int MIN_COORD = -2;
    private static final int MAX_COORD = 2;

    /**
     * Creates a scoring method with the given resource pattern and points.
     *
     * @param resources            multiset of required resources for one set
     * @param pointsPerCombination points awarded for each complete set
     * @param grid                 grid used to read final resources for scoring
     * @throws NullPointerException if {@code resources} or {@code pointsPerCombination} is {@code null}
     * @throws IllegalArgumentException if {@code resources} is empty
     */
    public ScoringMethod(final List<Resource> resources, final Points pointsPerCombination, final Grid grid) {
        Objects.requireNonNull(resources, "resources cannot be null");
        Objects.requireNonNull(pointsPerCombination, "pointsPerCombination cannot be null");
        Objects.requireNonNull(grid, "grid cannot be null");

        if (resources.isEmpty()) {
            throw new IllegalArgumentException("resources cannot be empty");
        }

        this.resources = List.copyOf(resources);
        this.pointsPerCombination = pointsPerCombination;
        this.grid = grid;
    }

    /**
     * Calculates the total points for this scoring method.
     */
    public void selectThisMethodAndCalculate() {
        // 1) collect all resources from the grid into a multiset.
        final Map<Resource, Integer> totalResources = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            totalResources.put(resource, 0);
        }

        for (int x = MIN_COORD; x <= MAX_COORD; x++) {
            for (int y = MIN_COORD; y <= MAX_COORD; y++) {
                final Optional<Card> cardOptional = grid.getCard(new GridPosition(x, y));
                // check if the card is present
                if (!cardOptional.isPresent()) {
                    continue;
                }

                // the card is present, calculate its resources
                final Card card = cardOptional.get();
                final List<Resource> cardResources = card.getResources();
                boolean cardHasPollution = false;

                if (card.isActive()) {
                    // active cards: all non-pollution resources count, pollution only flags the card.
                    for (Resource resource : cardResources) {
                        if (resource == Resource.POLLUTION) {
                            cardHasPollution = true;
                        } else {
                            totalResources.put(resource, totalResources.get(resource) + 1);
                        }
                    }
                } else {
                    cardHasPollution = true;
                }

                if (cardHasPollution) {
                    totalResources.put(Resource.POLLUTION, totalResources.get(Resource.POLLUTION) + 1);
                }
            }
        }

        // 2) calculate basic score from all collected resources
        int basicPointValue = 0;
        for (Map.Entry<Resource, Integer> entry : totalResources.entrySet()) {
            final Resource resource = entry.getKey();
            final int count = entry.getValue();
            basicPointValue += resource.getValue() * count;
        }

        // 3) extra points for full scoring combinations defined by 'this.resources'
        int totalPointValue = basicPointValue;

        if (!resources.isEmpty()) {
            // work on a mutable copy of the resource multiset
            final Map<Resource, Integer> remaining = new EnumMap<>(totalResources);
            int combinations = 0;

            // treat 'resources' as a multiset pattern; each loop tries to take one full pattern
            while (true) {
                boolean canFormCombination = true;

                for (Resource required : resources) {
                    int available = remaining.getOrDefault(required, 0);
                    if (available <= 0) {
                        canFormCombination = false;
                        break;
                    }
                    remaining.put(required, available - 1);
                }

                if (!canFormCombination) {
                    break;
                }

                combinations++;
            }

            totalPointValue += combinations * pointsPerCombination.value();
        }

        Points total = new Points(totalPointValue);
        calculatedTotal = Optional.of(total);
    }

    /**
     * @return calculated total points for this scoring method, if available.
     */
    public Optional<Points> getCalculatedTotal() {
        return calculatedTotal;
    }

    /**
     * @return the state of the scoring method as a JSON string
     */
    public String state() {
        final JSONObject json = new JSONObject();

        final JSONArray resourcesArray = new JSONArray();
        for (Resource resource : resources) {
            resourcesArray.put(resource.name());
        }
        json.put("resources", resourcesArray);
        json.put("pointsPerCombination", pointsPerCombination.value());
        calculatedTotal.ifPresent(points -> json.put("calculatedTotal", points.value()));

        return json.toString();
    }
}
