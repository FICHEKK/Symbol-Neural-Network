package settings;

import structures.Dataset;
import structures.NeuralNetwork;

public interface LearningStageSettings {
    NeuralNetwork getNeuralNetwork();
    Dataset getDataset();
}
