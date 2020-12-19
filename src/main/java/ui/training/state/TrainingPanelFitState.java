package ui.training.state;

public class TrainingPanelFitState extends TrainingPanelState {

    public final int iteration;
    public final double error;

    public TrainingPanelFitState(int iteration, double error) {
        this.iteration = iteration;
        this.error = error;
    }
}
