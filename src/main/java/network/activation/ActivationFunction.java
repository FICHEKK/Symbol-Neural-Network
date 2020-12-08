package network.activation;

import org.apache.commons.math3.linear.RealVector;

public interface ActivationFunction {
    RealVector apply(RealVector vector);
    double apply(double value);
}
