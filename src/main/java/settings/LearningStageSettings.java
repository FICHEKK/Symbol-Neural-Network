package settings;

import structures.Dataset;
import network.NeuralNetwork;

public interface LearningStageSettings {
    NeuralNetwork getNeuralNetwork();
    Dataset getDataset();
}
