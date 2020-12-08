package network.initializers;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface WeightInitializer {
    void initializeWeights(RealMatrix[] weights, int[] layers);
    void initializeBiases(RealVector[] biases, int[] layers);
}
