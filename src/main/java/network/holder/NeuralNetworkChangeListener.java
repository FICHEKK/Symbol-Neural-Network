package network.holder;

import network.NeuralNetwork;

@FunctionalInterface
public interface NeuralNetworkChangeListener {
    void onNeuralNetworkChange(NeuralNetwork neuralNetwork);
}
