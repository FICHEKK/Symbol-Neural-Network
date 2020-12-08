package network.activation;

import org.apache.commons.math3.linear.RealVector;

public class Sigmoid implements ActivationFunction {

    private static final Sigmoid INSTANCE = new Sigmoid();

    private Sigmoid() {
    }

    public static Sigmoid getInstance() {
        return INSTANCE;
    }

    @Override
    public RealVector apply(RealVector vector) {
        var size = vector.getDimension();

        for (int i = 0; i < size; i++) {
            vector.setEntry(i, apply(vector.getEntry(i)));
        }

        return vector;
    }

    @Override
    public double apply(double value) {
        return 1 / (1 + Math.exp(-value));
    }
}
