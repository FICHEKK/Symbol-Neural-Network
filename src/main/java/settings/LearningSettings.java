package settings;

import structures.Dataset;
import network.NeuralNetwork;

public interface LearningSettings {
    NeuralNetwork getNeuralNetwork();
    Dataset getDataset();
}
