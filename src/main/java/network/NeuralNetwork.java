package network;

import network.activation.ActivationFunction;
import network.initializers.WeightInitializer;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private final RealMatrix[] weights;
    private final RealMatrix[] deltaWeights;
    private final RealVector[] biases;
    private final RealVector[] deltaBiases;
    private final RealVector[] outputs;
    private final RealVector[] errors;

    private final ActivationFunction function;
    private final int[] layers;

    private double learningRate;
    private double minAcceptableError;
    private int maxIterations;
    private int batchSize;

    private final List<NeuralNetworkListener> listeners = new ArrayList<>();

    public NeuralNetwork(WeightInitializer initializer, ActivationFunction function, int... layers) {
        this.function = function;
        this.layers = layers;

        outputs = new RealVector[layers.length];
        errors = new RealVector[layers.length];
        weights = new RealMatrix[layers.length - 1];
        biases = new RealVector[layers.length - 1];
        deltaWeights = new RealMatrix[layers.length - 1];
        deltaBiases = new RealVector[layers.length - 1];

        initializer.initializeWeights(weights, layers);
        initializer.initializeBiases(biases, layers);
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

    public void addListener(NeuralNetworkListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NeuralNetworkListener listener) {
        listeners.remove(listener);
    }

    public void fit(double[][] X, double[][] y) {
        int sampleIndex = 0;

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            resetDeltaWeightsAndBiases();

            for (int j = 0; j < batchSize; j++) {
                calculateOutputLayerError(y[sampleIndex], predict(X[sampleIndex]));

                for (int layer = errors.length - 2; layer >= 1; layer--) {
                    calculateHiddenLayerError(layer);
                }

                for (int layer = 0; layer < layers.length - 1; layer++) {
                    calculateDeltaWeights(layer);
                    calculateDeltaBiases(layer);
                }

                sampleIndex = (sampleIndex + 1) % X.length;
            }

            updateWeightsAndBiases();
            var error = calculateNetworkError(X, y);
            System.out.println(iteration + "#: Network error: " + error);

            if (error <= minAcceptableError) {
                System.out.println("Finished learning - error within acceptable range!");
                break;
            }
        }

        listeners.forEach(NeuralNetworkListener::onFitFinish);
    }

    public double[] predict(double[] sample) {
        var input = MatrixUtils.createRealVector(sample);
        outputs[0] = input;

        for (int layer = 0; layer < layers.length - 1; layer++) {
            input = function.apply(weights[layer].operate(input).add(biases[layer]));
            outputs[layer + 1] = input;
        }

        return input.toArray();
    }

    private double calculateNetworkError(double[][] X, double[][] y) {
        var error = 0.0;
        var N = X.length;

        for (int i = 0; i < N; i++) {
            error += calculateSampleError(y[i], predict(X[i]));
        }

        return 1.0 / (2 * N) * error;
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

    private void calculateDeltaWeights(int layer) {
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
    }

    private void calculateDeltaBiases(int layer) {
        int neuronsInLayer = layers[layer + 1];
        var dB = deltaBiases[layer];

        for (int i = 0; i < neuronsInLayer; i++) {
            dB.setEntry(i, dB.getEntry(i) + learningRate * errors[layer + 1].getEntry(i));
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

    private void resetDeltaWeightsAndBiases() {
        for (int i = 0; i < deltaWeights.length; i++) {
            deltaWeights[i] = MatrixUtils.createRealMatrix(layers[i + 1], layers[i]);
        }

        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            deltaBiases[layer - 1] = MatrixUtils.createRealVector(new double[neuronsInLayer]);
        }
    }

    public RealMatrix[] getWeights() {
        return weights;
    }

    public RealVector[] getBiases() {
        return biases;
    }

    public int[] getLayers() {
        return layers;
    }
}
