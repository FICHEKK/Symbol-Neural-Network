package network.initializers;

import math.Matrix;
import math.Vector;

public interface WeightInitializer {
    void initializeWeights(Matrix[] weights, int[] layers);
    void initializeBiases(Vector[] biases, int[] layers);
}
