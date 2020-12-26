package ui.training;

public abstract class TrainingState {

    public static class FitStatus extends TrainingState {
        public final int iteration;
        public final double error;

        public FitStatus(int iteration, double error) {
            this.iteration = iteration;
            this.error = error;
        }
    }

    public static class Settings extends TrainingState {
        public final boolean isMiniBatchSizeValid;
        public final boolean isHiddenLayersDefinitionValid;
        public final boolean isLearningRateValid;
        public final boolean isMinimumAcceptableErrorValid;
        public final boolean isMaximumNumberOfIterationsValid;
        public final boolean isAdditionalPermutationsPerSampleValid;
        public final boolean isMiniBatchSectionEnabled;
        public final boolean isTrainNeuralNetworkButtonEnabled;
        public final boolean useRandomWeightColors;

        public Settings(boolean isMiniBatchSizeValid,
                                          boolean isHiddenLayersDefinitionValid,
                                          boolean isLearningRateValid,
                                          boolean isMinimumAcceptableErrorValid,
                                          boolean isMaximumNumberOfIterationsValid,
                                          boolean isAdditionalPermutationsPerSampleValid,
                                          boolean isMiniBatchSectionEnabled,
                                          boolean isTrainNeuralNetworkButtonEnabled,
                                          boolean useRandomWeightColors) {
            this.isMiniBatchSizeValid = isMiniBatchSizeValid;
            this.isHiddenLayersDefinitionValid = isHiddenLayersDefinitionValid;
            this.isLearningRateValid = isLearningRateValid;
            this.isMinimumAcceptableErrorValid = isMinimumAcceptableErrorValid;
            this.isMaximumNumberOfIterationsValid = isMaximumNumberOfIterationsValid;
            this.isAdditionalPermutationsPerSampleValid = isAdditionalPermutationsPerSampleValid;
            this.isMiniBatchSectionEnabled = isMiniBatchSectionEnabled;
            this.isTrainNeuralNetworkButtonEnabled = isTrainNeuralNetworkButtonEnabled;
            this.useRandomWeightColors = useRandomWeightColors;
        }
    }
}
