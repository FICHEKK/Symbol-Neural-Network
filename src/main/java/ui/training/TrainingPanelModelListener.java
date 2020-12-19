package ui.training;

import network.NeuralNetwork;
import ui.training.state.TrainingPanelState;

public interface TrainingPanelModelListener {
    void onNeuralNetworkChange(NeuralNetwork neuralNetwork);
    void onNextState(TrainingPanelState state);
}
