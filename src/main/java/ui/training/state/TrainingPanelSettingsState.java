package ui.training.state;

public class TrainingPanelSettingsState extends TrainingPanelState {

    public final boolean isMiniBatchSizeValid;
    public final boolean isHiddenLayersDefinitionValid;
    public final boolean isLearningRateValid;
    public final boolean isMinimumAcceptableErrorValid;
    public final boolean isMaximumNumberOfIterationsValid;
    public final boolean isMiniBatchSectionEnabled;
    public final boolean isTrainNeuralNetworkButtonEnabled;
    public final boolean useRandomWeightColors;

    public TrainingPanelSettingsState(boolean isMiniBatchSizeValid,
                                      boolean isHiddenLayersDefinitionValid,
                                      boolean isLearningRateValid,
                                      boolean isMinimumAcceptableErrorValid,
                                      boolean isMaximumNumberOfIterationsValid,
                                      boolean isMiniBatchSectionEnabled,
                                      boolean isTrainNeuralNetworkButtonEnabled,
                                      boolean useRandomWeightColors) {
        this.isMiniBatchSizeValid = isMiniBatchSizeValid;
        this.isHiddenLayersDefinitionValid = isHiddenLayersDefinitionValid;
        this.isLearningRateValid = isLearningRateValid;
        this.isMinimumAcceptableErrorValid = isMinimumAcceptableErrorValid;
        this.isMaximumNumberOfIterationsValid = isMaximumNumberOfIterationsValid;
        this.isMiniBatchSectionEnabled = isMiniBatchSectionEnabled;
        this.isTrainNeuralNetworkButtonEnabled = isTrainNeuralNetworkButtonEnabled;
        this.useRandomWeightColors = useRandomWeightColors;
    }
}
