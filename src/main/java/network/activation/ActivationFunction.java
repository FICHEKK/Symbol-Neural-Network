package network.activation;

import math.Vector;

public interface ActivationFunction {
    Vector apply(Vector vector);
    double apply(double value);
}
