package network;

import math.Matrix;
import math.Vector;
import network.activation.ActivationFunction;
import network.initializers.WeightInitializer;
import structures.Dataset;

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

    private boolean isBeingFitted;

    private final List<NeuralNetworkFitStartListener> fitStartListeners = new ArrayList<>();
    private final List<NeuralNetworkFitUpdateListener> fitUpdateListeners = new ArrayList<>();
    private final List<NeuralNetworkFitFinishListener> fitFinishListeners = new ArrayList<>();

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

    public void fit(Dataset dataset) {
        isBeingFitted = true;
        fitStartListeners.forEach(NeuralNetworkFitStartListener::onFitStart);

        int i, sampleIndex = 0;

        for (i = 1; i <= maxIterations && isBeingFitted; i++) {
            dataset.shuffle();
            resetDeltaWeightsAndBiases();

            for (int j = 0; j < batchSize; j++) {
                var actual = dataset.getY(sampleIndex);
                var prediction = predict(dataset.getX(sampleIndex));
                calculateOutputLayerError(actual, prediction);

                for (int layer = errors.length - 2; layer >= 1; layer--) {
                    calculateHiddenLayerError(layer);
                }

                for (int layer = 0; layer < layers.length - 1; layer++) {
                    calculateDeltaWeights(layer);
                    calculateDeltaBiases(layer);
                }

                sampleIndex = (sampleIndex + 1) % dataset.size();
            }

            updateWeightsAndBiases();

            final var error = calculateError(dataset);

            if (!fitUpdateListeners.isEmpty()) {
                final var iteration = i;
                fitUpdateListeners.forEach(listener -> listener.onFitUpdate(iteration, error));
            }

            if (error <= minAcceptableError) break;
        }

        isBeingFitted = false;
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

    public double calculateError(Dataset dataset) {
        var error = 0.0;
        var N = dataset.size();

        for (int i = 0; i < N; i++) {
            var actual = dataset.getY(i);
            var prediction = predict(dataset.getX(i));
            error += calculateError(actual, prediction);
        }

        return 1.0 / (2 * N) * error;
    }

    public double calculateError(double[] actual, double[] prediction) {
        double error = 0.0;

        for (int i = 0; i < actual.length; i++) {
            var delta = actual[i] - prediction[i];
            error += delta * delta;
        }

        return error;
    }

    public boolean isBeingFitted() {
        return isBeingFitted;
    }

    public void stopFitting() {
        isBeingFitted = false;
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
        var deltaWeights = this.deltaWeights[layer];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                var currentDeltaWeight = deltaWeights.get(row, col);

                var neuronOutput = outputs[layer].get(col);
                var neuronError = errors[layer + 1].get(row);
                var additionalDeltaWeight = learningRate * neuronOutput * neuronError;

                deltaWeights.set(row, col, currentDeltaWeight + additionalDeltaWeight);
            }
        }
    }

    private void calculateDeltaBiases(int layer) {
        int neuronsInLayer = layers[layer + 1];
        var deltaBiases = this.deltaBiases[layer];

        for (int i = 0; i < neuronsInLayer; i++) {
            var currentDeltaBias = deltaBiases.get(i);
            var additionalDeltaBias = learningRate * errors[layer + 1].get(i);
            deltaBiases.set(i, currentDeltaBias + additionalDeltaBias);
        }
    }

    private void calculateHiddenLayerError(int layer) {
        var neuronErrors = new double[layers[layer]];
        var weights = this.weights[layer];
        var rows = weights.getRows();

        for (int i = 0; i < neuronErrors.length; i++) {
            var neuronOutput = outputs[layer].get(i);

            double error = 0.0;
            for (int row = 0; row < rows; row++) {
                error += weights.get(row, i) * errors[layer + 1].get(row);
            }

            neuronErrors[i] = neuronOutput * (1 - neuronOutput) * error;
        }

        errors[layer] = Vector.of(neuronErrors);
    }

    private void calculateOutputLayerError(double[] actual, double[] prediction) {
        double[] neuronErrors = new double[actual.length];

        for (int i = 0; i < neuronErrors.length; i++) {
            neuronErrors[i] = prediction[i] * (1 - prediction[i]) * (actual[i] - prediction[i]);
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

    public void addFitStartListener(NeuralNetworkFitStartListener listener) {
        fitStartListeners.add(listener);
    }

    public void removeFitStartListener(NeuralNetworkFitStartListener listener) {
        fitStartListeners.remove(listener);
    }

    public void addFitUpdateListener(NeuralNetworkFitUpdateListener listener) {
        fitUpdateListeners.add(listener);
    }

    public void removeFitUpdateListener(NeuralNetworkFitUpdateListener listener) {
        fitUpdateListeners.remove(listener);
    }

    public void addFitFinishListener(NeuralNetworkFitFinishListener listener) {
        fitFinishListeners.add(listener);
    }

    public void removeFitFinishListener(NeuralNetworkFitFinishListener listener) {
        fitFinishListeners.remove(listener);
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
