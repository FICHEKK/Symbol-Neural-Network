package network.activation;

import math.Vector;

public class Sigmoid implements ActivationFunction {

    @Override
    public Vector apply(Vector vector) {
        for (int i = 0, size = vector.size(); i < size; i++) {
            vector.set(i, apply(vector.get(i)));
        }

        return vector;
    }

    @Override
    public double apply(double value) {
        return 1 / (1 + Math.exp(-value));
    }
}
