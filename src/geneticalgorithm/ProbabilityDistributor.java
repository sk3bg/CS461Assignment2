package geneticalgorithm;

import java.util.*;

public class ProbabilityDistributor<T extends Probability> {

    private int startIndex;
    private final List<Double> probabilities;
    private final List<T> items;
    private final static SplittableRandom random = new SplittableRandom();

    public ProbabilityDistributor(List<T> items) {
        startIndex = 0;
        this.items = items;
        probabilities = items.stream()
                .map(Probability::getProbability)
                .toList();
    }

    public T pickAndRemove(int failSafe) {
        for (int i = startIndex; i < probabilities.size(); i++) {
            if (random.nextDouble(1) <= probabilities.get(i)) {
                startIndex++;
                return items.get(i);
            }
        }
        return items.get(failSafe);
    }

    public void restoreRemoved() {
        startIndex = 0;
    }

}
