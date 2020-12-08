package structures;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Random;

public class NeuralNetwork {

    private static final Random RANDOM = new Random();

    // Current weights of the network.
    private RealMatrix[] weights;

    // Storage of weight changes that need to be applied.
    private RealMatrix[] deltaWeights;

    // Biases of all hidden layer and output neurons.
    private RealVector[] biases;

    // Storage of bias changes that need to be applied.
    private RealVector[] deltaBiases;

    // Storage of all the output values of the current iteration.
    private RealVector[] outputs;

    // Storage of all the neuron errors of the current iteration.
    private RealVector[] errors;

    private final int[] layers;
    private double learningRate;
    private double minAcceptableError;
    private int maxIterations;
    private int batchSize;

    public NeuralNetwork(int... layers) {
        this.layers = layers;
        outputs = new RealVector[layers.length];
        errors = new RealVector[layers.length];
        initWeightsToRandomValues();
        initBiasesToRandomValues();
    }

    private void initWeightsToRandomValues() {
        weights = new RealMatrix[layers.length - 1];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = MatrixUtils.createRealMatrix(layers[i + 1], layers[i]);

            for (int row = 0; row < layers[i + 1]; row++) {
                for (int col = 0; col < layers[i]; col++) {
                    weights[i].setEntry(row, col, RANDOM.nextDouble());
                }
            }
        }
    }

    private void initBiasesToRandomValues() {
        biases = new RealVector[layers.length - 1];

        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            double[] values = new double[neuronsInLayer];

            for (int i = 0; i < values.length; i++) {
                values[i] = RANDOM.nextDouble();
            }

            biases[layer - 1] = MatrixUtils.createRealVector(values);
        }
    }

    public NeuralNetwork withLearningRate(double learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    public NeuralNetwork withMinAcceptableError(double minAcceptableError) {
        this.minAcceptableError = minAcceptableError;
        return this;
    }

    public NeuralNetwork withMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public NeuralNetwork withBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public NeuralNetwork fit(double[][] X, double[][] y) {
        for (int i = 1; i <= maxIterations; i++) {
            resetDeltaWeightsAndBiases();

            for (int j = 0; j < X.length; j++) {
                calculateOutputLayerError(y[j], predict(X[j]));

                for (int layer = errors.length - 2; layer >= 1; layer--) {
                    calculateHiddenLayerError(layer);
                }

                for (int layer = 0; layer < layers.length - 1; layer++) {
                    calculateDeltaWeightsAndBiases(layer);
                }
            }

            updateWeightsAndBiases();
            calculateNetworkError(X, y);
        }

        return this;
    }

    private void calculateNetworkError(double[][] X, double[][] y) {
        var error = 0.0;

        for (int i = 0; i < X.length; i++) {
            error += calculateSampleError(y[i], predict(X[i]));
        }

        System.out.println("Network error: " + error);
    }

    private double calculateSampleError(double[] y, double[] prediction) {
        double error = 0.0;

        for (int i = 0; i < y.length; i++) {
            var delta = y[i] - prediction[i];
            error += delta * delta;
        }

        return error;
    }

    private void updateWeightsAndBiases() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i].add(deltaWeights[i]);
            biases[i] = biases[i].add(deltaBiases[i]);
        }
    }

    private void calculateDeltaWeightsAndBiases(int layer) {
        var rows = weights[layer].getRowDimension();
        var cols = weights[layer].getColumnDimension();
        var dW = deltaWeights[layer];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                var neuronOutput = outputs[layer].getEntry(col);
                var neuronError = errors[layer + 1].getEntry(row);
                dW.setEntry(row, col, dW.getEntry(row, col) + learningRate * neuronOutput * neuronError);
            }
        }

        var dB = deltaBiases[layer];

        for (int row = 0; row < rows; row++) {
            dB.setEntry(row, dB.getEntry(row) + learningRate * errors[layer + 1].getEntry(row));
        }
    }

    private void calculateHiddenLayerError(int layer) {
        var neuronErrors = new double[layers[layer]];
        var W = weights[layer];
        var rows = W.getRowDimension();

        for (int i = 0; i < neuronErrors.length; i++) {
            var neuronOutput = outputs[layer].getEntry(i);

            double error = 0.0;
            for (int row = 0; row < rows; row++) {
                error += W.getEntry(row, i) * errors[layer + 1].getEntry(row);
            }

            neuronErrors[i] = neuronOutput * (1 - neuronOutput) * error;
        }

        errors[layer] = MatrixUtils.createRealVector(neuronErrors);
    }

    private void calculateOutputLayerError(double[] y, double[] prediction) {
        double[] neuronErrors = new double[y.length];

        for (int i = 0; i < neuronErrors.length; i++) {
            neuronErrors[i] = prediction[i] * (1 - prediction[i]) * (y[i] - prediction[i]);
        }

        errors[errors.length - 1] = MatrixUtils.createRealVector(neuronErrors);
    }

    public double[] predict(double[] sample) {
        var input = MatrixUtils.createRealVector(sample);
        outputs[0] = input;

        for (int layer = 0; layer < layers.length - 1; layer++) {
            input = sigmoid(weights[layer].operate(input).add(biases[layer]));
            outputs[layer + 1] = input;
        }

        return input.toArray();
    }

    private static RealVector sigmoid(RealVector vector) {
        var size = vector.getDimension();

        for (int i = 0; i < size; i++) {
            vector.setEntry(i, sigmoid(vector.getEntry(i)));
        }

        return vector;
    }

    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private void resetDeltaWeightsAndBiases() {
        deltaWeights = new RealMatrix[layers.length - 1];
        deltaBiases = new RealVector[layers.length - 1];

        for (int i = 0; i < deltaWeights.length; i++) {
            deltaWeights[i] = MatrixUtils.createRealMatrix(layers[i + 1], layers[i]);
        }

        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            deltaBiases[layer - 1] = MatrixUtils.createRealVector(new double[neuronsInLayer]);
        }
    }
}
