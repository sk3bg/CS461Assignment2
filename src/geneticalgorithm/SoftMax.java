package geneticalgorithm;

import java.util.List;

public class SoftMax {
    public static List<Double> normalize(List<Double> inputs) {
        double totalExponentiation = calculateTotalExponentiation(inputs);
        return inputs.stream()
                .map(input -> Math.exp(input) / totalExponentiation)
                .toList();
    }

    private static double calculateTotalExponentiation(List<Double> inputs) {
        return inputs.stream()
                .mapToDouble(Math::exp)
                .sum();
    }
}
