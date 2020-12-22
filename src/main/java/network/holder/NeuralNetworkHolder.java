package network.holder;

import network.NeuralNetwork;

public interface NeuralNetworkHolder {
    NeuralNetwork getNeuralNetwork();
    void addChangeListener(NeuralNetworkChangeListener listener);
    void removeChangeListener(NeuralNetworkChangeListener listener);
}
