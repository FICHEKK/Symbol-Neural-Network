package network.initializers;

import math.Matrix;
import math.Vector;

import java.util.Random;

public class RandomWeightInitializer implements WeightInitializer {

    private static final Random RANDOM = new Random();

    private final double lowerBound;
    private final double upperBound;

    public RandomWeightInitializer(double lowerBound, double upperBound) {
        if (lowerBound > upperBound)
            throw new IllegalArgumentException("Lower bound cannot be greater than upper bound");

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public void initializeWeights(Matrix[] weights, int[] layers) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Matrix.zero(layers[i + 1], layers[i]);

            for (int row = 0; row < layers[i + 1]; row++) {
                for (int col = 0; col < layers[i]; col++) {
                    weights[i].set(row, col, generateRandomNumber());
                }
            }
        }
    }

    @Override
    public void initializeBiases(Vector[] biases, int[] layers) {
        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            double[] values = new double[neuronsInLayer];

            for (int i = 0; i < values.length; i++) {
                values[i] = generateRandomNumber();
            }

            biases[layer - 1] = Vector.of(values);
        }
    }

    private double generateRandomNumber() {
        return lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound);
    }
}
