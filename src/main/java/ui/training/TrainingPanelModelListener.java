package ui.training;

import ui.training.state.TrainingPanelState;

public interface TrainingPanelModelListener {
    void onNextState(TrainingPanelState state);
}
