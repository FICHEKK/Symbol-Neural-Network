package network;

import math.Matrix;
import math.Vector;
import network.activation.ActivationFunction;
import network.initializers.WeightInitializer;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private final Matrix[] weights;
    private final Matrix[] deltaWeights;
    private final Vector[] biases;
    private final Vector[] deltaBiases;
    private final Vector[] outputs;
    private final Vector[] errors;

    private final ActivationFunction function;
    private final int[] layers;

    private double learningRate = 0.01;
    private double minAcceptableError = 0.05;
    private int maxIterations = Integer.MAX_VALUE;
    private int batchSize = 1;

    private final List<NeuralNetworkFitFinishListener> fitFinishListeners = new ArrayList<>();
    private final List<NeuralNetworkFitUpdateListener> fitUpdateListeners = new ArrayList<>();

    public NeuralNetwork(WeightInitializer initializer, ActivationFunction function, int... layers) {
        this.function = function;
        this.layers = layers;

        outputs = new Vector[layers.length];
        errors = new Vector[layers.length];
        weights = new Matrix[layers.length - 1];
        biases = new Vector[layers.length - 1];
        deltaWeights = new Matrix[layers.length - 1];
        deltaBiases = new Vector[layers.length - 1];

        initializer.initializeWeights(weights, layers);
        initializer.initializeBiases(biases, layers);
    }

    public void fit(double[][] X, double[][] Y) {
        if (X.length != Y.length) throw new IllegalArgumentException("X.length != Y.length");

        int i, sampleIndex = 0;

        for (i = 1; i <= maxIterations; i++) {
            resetDeltaWeightsAndBiases();

            for (int j = 0; j < batchSize; j++) {
                calculateOutputLayerError(Y[sampleIndex], predict(X[sampleIndex]));

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

            final var error = calculateNetworkError(X, Y);

            if (!fitUpdateListeners.isEmpty()) {
                final var iteration = i;
                fitUpdateListeners.forEach(listener -> listener.onFitUpdate(iteration, error));
            }

            if (error <= minAcceptableError) break;
        }

        fitFinishListeners.forEach(NeuralNetworkFitFinishListener::onFitFinish);
    }

    public double[] predict(double[] sample) {
        var input = Vector.of(sample);
        outputs[0] = input;

        for (int layer = 0; layer < layers.length - 1; layer++) {
            input = function.apply(weights[layer].times(input).plus(biases[layer]));
            outputs[layer + 1] = input;
        }

        return input.toArray();
    }

    public double calculateNetworkError(double[][] X, double[][] y) {
        var error = 0.0;
        var N = X.length;

        for (int i = 0; i < N; i++) {
            error += calculateSampleError(y[i], predict(X[i]));
        }

        return 1.0 / (2 * N) * error;
    }

    public double calculateSampleError(double[] y, double[] prediction) {
        double error = 0.0;

        for (int i = 0; i < y.length; i++) {
            var delta = y[i] - prediction[i];
            error += delta * delta;
        }

        return error;
    }

    private void updateWeightsAndBiases() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i].plus(deltaWeights[i]);
            biases[i] = biases[i].plus(deltaBiases[i]);
        }
    }

    private void calculateDeltaWeights(int layer) {
        var rows = weights[layer].getRows();
        var cols = weights[layer].getColumns();
        var dW = deltaWeights[layer];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                var neuronOutput = outputs[layer].get(col);
                var neuronError = errors[layer + 1].get(row);
                dW.set(row, col, dW.get(row, col) + learningRate * neuronOutput * neuronError);
            }
        }
    }

    private void calculateDeltaBiases(int layer) {
        int neuronsInLayer = layers[layer + 1];
        var dB = deltaBiases[layer];

        for (int i = 0; i < neuronsInLayer; i++) {
            dB.set(i, dB.get(i) + learningRate * errors[layer + 1].get(i));
        }
    }

    private void calculateHiddenLayerError(int layer) {
        var neuronErrors = new double[layers[layer]];
        var W = weights[layer];
        var rows = W.getRows();

        for (int i = 0; i < neuronErrors.length; i++) {
            var neuronOutput = outputs[layer].get(i);

            double error = 0.0;
            for (int row = 0; row < rows; row++) {
                error += W.get(row, i) * errors[layer + 1].get(row);
            }

            neuronErrors[i] = neuronOutput * (1 - neuronOutput) * error;
        }

        errors[layer] = Vector.of(neuronErrors);
    }

    private void calculateOutputLayerError(double[] y, double[] prediction) {
        double[] neuronErrors = new double[y.length];

        for (int i = 0; i < neuronErrors.length; i++) {
            neuronErrors[i] = prediction[i] * (1 - prediction[i]) * (y[i] - prediction[i]);
        }

        errors[errors.length - 1] = Vector.of(neuronErrors);
    }

    private void resetDeltaWeightsAndBiases() {
        for (int i = 0; i < deltaWeights.length; i++) {
            deltaWeights[i] = Matrix.zero(layers[i + 1], layers[i]);
        }

        for (int layer = 1; layer < layers.length; layer++) {
            int neuronsInLayer = layers[layer];
            deltaBiases[layer - 1] = Vector.zero(neuronsInLayer);
        }
    }

    // =============================================================================================
    //                                       Listeners
    // =============================================================================================

    public void addFitFinishListener(NeuralNetworkFitFinishListener listener) {
        fitFinishListeners.add(listener);
    }

    public void removeFitFinishListener(NeuralNetworkFitFinishListener listener) {
        fitFinishListeners.remove(listener);
    }

    public void addFitUpdateListener(NeuralNetworkFitUpdateListener listener) {
        fitUpdateListeners.add(listener);
    }

    public void removeFitUpdateListener(NeuralNetworkFitUpdateListener listener) {
        fitUpdateListeners.remove(listener);
    }

    // =============================================================================================
    //                                       Getters
    // =============================================================================================

    public Matrix[] getWeights() {
        return weights.clone();
    }

    public Vector[] getBiases() {
        return biases.clone();
    }

    public int[] getLayers() {
        return layers.clone();
    }

    public int getInputNeuronCount() {
        return layers[0];
    }

    public int getOutputNeuronCount() {
        return layers[layers.length - 1];
    }

    // =============================================================================================
    //                                       Setters
    // =============================================================================================

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setMinAcceptableError(double minAcceptableError) {
        this.minAcceptableError = minAcceptableError;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
