package ui.training;

import ui.training.state.TrainingPanelState;

@FunctionalInterface
public interface TrainingPanelModelListener {
    void onNextState(TrainingPanelState state);
}
