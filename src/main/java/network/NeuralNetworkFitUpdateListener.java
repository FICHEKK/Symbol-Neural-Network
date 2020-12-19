package network;

@FunctionalInterface
public interface NeuralNetworkFitUpdateListener {
    void onFitUpdate(int iteration, double error);
}
