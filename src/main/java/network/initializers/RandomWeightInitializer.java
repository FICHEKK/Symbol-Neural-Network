package network.initializers;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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
    public void initializeWeights(RealMatrix[] weights, int[] layers) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = MatrixUtils.createRealMatrix(layers[i + 1], layers[i]);

            for (int row = 0; row < layers[i + 1]; row++) {
                for (int col = 0; col < layers[i]; col++) {
                    weights[i].setEntry(row, col, generateRandomNumber());
                }
            }
        }
    }

    @Override
    public void initializeBiases(RealVector[] biases, int[] layers) {
        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            double[] values = new double[neuronsInLayer];

            for (int i = 0; i < values.length; i++) {
                values[i] = generateRandomNumber();
            }

            biases[layer - 1] = MatrixUtils.createRealVector(values);
        }
    }

    private double generateRandomNumber() {
        return lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound);
    }
}
