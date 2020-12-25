package ui.training;

import network.NeuralNetwork;
import network.NeuralNetworkFitFinishListener;
import network.NeuralNetworkFitUpdateListener;
import network.activation.Sigmoid;
import network.holder.NeuralNetworkHolder;
import network.holder.NeuralNetworkChangeListener;
import network.initializers.RandomWeightInitializer;
import settings.Settings;
import settings.SettingsListener;
import structures.Dataset;
import ui.training.state.TrainingPanelFitState;
import ui.training.state.TrainingPanelSettingsState;
import util.DatasetLoader;
import util.UserInputValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static settings.Settings.*;
import static ui.views.NeuralNetworkView.WeightsDrawingMode;

public class TrainingPanelModel implements SettingsListener, NeuralNetworkHolder {

    private static final String HIDDEN_LAYERS_DEFINITION_SEPARATOR = "x";

    private static final int MIN_MINI_BATCH_SIZE = 1;
    private static final double MIN_LEARNING_RATE = 0;
    private static final double MIN_ACCEPTABLE_ERROR = 0;
    private static final int MIN_NUMBER_OF_ITERATIONS = 1;
    private static final int MIN_ADDITIONAL_PERMUTATIONS_PER_SAMPLE = 0;

    private static final int MIN_NEURONS_IN_HIDDEN_LAYER = 1;
    private static final int MAX_NEURONS_IN_HIDDEN_LAYER = 100;

    private static final double MIN_RANDOM_WEIGHT = -0.5;
    private static final double MAX_RANDOM_WEIGHT = +0.5;

    private final List<NeuralNetworkChangeListener> listeners = new ArrayList<>();

    private TrainingMethod trainingMethod;
    private String miniBatchSize;
    private String hiddenLayersDefinition;
    private String learningRate;
    private String minimumAcceptableError;
    private String maximumNumberOfIterations;
    private String additionalPermutationsPerSample;
    private WeightsDrawingMode weightsDrawingMode;
    private boolean useRandomWeightColors;
    private boolean isCurrentlyTraining;

    private final Settings settings;
    private NeuralNetwork neuralNetwork;
    private TrainingPanelModelListener listener;

    public TrainingPanelModel(Settings settings) {
        this.settings = settings;
        this.settings.addListener(this);

        trainingMethod = TrainingMethod.from(settings.getStringProperty(TRAINING_METHOD));
        miniBatchSize = settings.getStringProperty(MINI_BATCH_SIZE);
        hiddenLayersDefinition = settings.getStringProperty(HIDDEN_LAYERS_DEFINITION);
        learningRate = settings.getStringProperty(LEARNING_RATE);
        minimumAcceptableError = settings.getStringProperty(MINIMUM_ACCEPTABLE_ERROR);
        maximumNumberOfIterations = settings.getStringProperty(MAXIMUM_NUMBER_OF_ITERATIONS);
        additionalPermutationsPerSample = settings.getStringProperty(ADDITIONAL_PERMUTATIONS_PER_SAMPLE);
        weightsDrawingMode = WeightsDrawingMode.from(settings.getStringProperty(WEIGHTS_DRAWING_MODE));
        useRandomWeightColors = settings.getBooleanProperty(USE_RANDOM_WEIGHT_COLORS);
    }

    public void setListener(TrainingPanelModelListener listener) {
        this.listener = listener;
        notifyListenerOnSettingsState();
    }

    public void trainNeuralNetwork() {
        new Thread(() -> {
            try {
                var loadDirectory = settings.getStringProperty(SYMBOL_LOAD_DIRECTORY);
                var numberOfRepresentativePoints = settings.getIntProperty(NUMBER_OF_REPRESENTATIVE_POINTS);

                var dataset = DatasetLoader.loadDataset(loadDirectory, numberOfRepresentativePoints)
                        .expand(settings.getIntProperty(ADDITIONAL_PERMUTATIONS_PER_SAMPLE))
                        .shuffle();

                if (dataset.X.length == 0)
                    throw new IOException("You need dataset in order to train a neural network.");

                neuralNetwork = createNeuralNetwork(dataset);
                listeners.forEach(listener -> listener.onNeuralNetworkChange(neuralNetwork));

                NeuralNetworkFitFinishListener finishListener = () -> {
                    isCurrentlyTraining = false;
                    notifyListenerOnSettingsState();
                };

                NeuralNetworkFitUpdateListener updateListener = (iteration, error) ->
                        listener.onNextState(new TrainingPanelFitState(iteration, error));

                neuralNetwork.addFitFinishListener(finishListener);
                neuralNetwork.addFitUpdateListener(updateListener);

                neuralNetwork.fit(dataset.X, dataset.Y);

                neuralNetwork.removeFitFinishListener(finishListener);
                neuralNetwork.removeFitUpdateListener(updateListener);
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
                notifyListenerOnSettingsState();
            }
        }).start();
    }

    private NeuralNetwork createNeuralNetwork(Dataset dataset) {
        var network = new NeuralNetwork(
                new RandomWeightInitializer(MIN_RANDOM_WEIGHT, MAX_RANDOM_WEIGHT),
                Sigmoid.getInstance(),
                calculateNetworkLayers(dataset)
        );

        network.setLearningRate(Double.parseDouble(learningRate));
        network.setBatchSize(getBatchSize(dataset));
        network.setMaxIterations(Integer.parseInt(maximumNumberOfIterations));
        network.setMinAcceptableError(Double.parseDouble(minimumAcceptableError));

        return network;
    }

    public void setTrainingMethod(TrainingMethod trainingMethod) {
        this.trainingMethod = trainingMethod;
        settings.setStringProperty(TRAINING_METHOD, trainingMethod.toString());
        notifyListenerOnSettingsState();
    }

    public void setMiniBatchSize(String miniBatchSize) {
        this.miniBatchSize = miniBatchSize;
        savePropertyIfValid(MINI_BATCH_SIZE, this::isMiniBatchSizeValid, miniBatchSize);
        notifyListenerOnSettingsState();
    }

    public void setHiddenLayersDefinition(String hiddenLayersDefinition) {
        this.hiddenLayersDefinition = hiddenLayersDefinition;
        savePropertyIfValid(HIDDEN_LAYERS_DEFINITION, this::isHiddenLayersDefinitionValid, hiddenLayersDefinition);
        notifyListenerOnSettingsState();
    }

    public void setLearningRate(String learningRate) {
        this.learningRate = learningRate;
        savePropertyIfValid(LEARNING_RATE, this::isLearningRateValid, learningRate);
        notifyListenerOnSettingsState();
    }

    public void setMinimumAcceptableError(String minimumAcceptableError) {
        this.minimumAcceptableError = minimumAcceptableError;
        savePropertyIfValid(MINIMUM_ACCEPTABLE_ERROR, this::isMinimumAcceptableErrorValid, minimumAcceptableError);
        notifyListenerOnSettingsState();
    }

    public void setMaximumNumberOfIterations(String maximumNumberOfIterations) {
        this.maximumNumberOfIterations = maximumNumberOfIterations;
        savePropertyIfValid(MAXIMUM_NUMBER_OF_ITERATIONS, this::isMaximumNumberOfIterationsValid, maximumNumberOfIterations);
        notifyListenerOnSettingsState();
    }

    public void setAdditionalPermutationsPerSample(String additionalPermutationsPerSample) {
        this.additionalPermutationsPerSample = additionalPermutationsPerSample;
        savePropertyIfValid(ADDITIONAL_PERMUTATIONS_PER_SAMPLE, this::isAdditionalPermutationsPerSampleValid, additionalPermutationsPerSample);
        notifyListenerOnSettingsState();
    }

    public void setWeightsDrawingMode(WeightsDrawingMode weightsDrawingMode) {
        this.weightsDrawingMode = weightsDrawingMode;
        settings.setStringProperty(WEIGHTS_DRAWING_MODE, weightsDrawingMode.toString());
    }

    private void savePropertyIfValid(String property, BooleanSupplier propertyValidity, String potentialNewValue) {
        if (propertyValidity.getAsBoolean()) {
            settings.setStringProperty(property, potentialNewValue);
        }
    }

    public TrainingMethod getTrainingMethod() {
        return trainingMethod;
    }

    public String getMiniBatchSize() {
        return miniBatchSize;
    }

    public String getHiddenLayersDefinition() {
        return hiddenLayersDefinition;
    }

    public String getLearningRate() {
        return learningRate;
    }

    public String getMinimumAcceptableError() {
        return minimumAcceptableError;
    }

    public String getMaximumNumberOfIterations() {
        return maximumNumberOfIterations;
    }

    public String getAdditionalPermutationsPerSample() {
        return additionalPermutationsPerSample;
    }

    public WeightsDrawingMode getWeightsDrawingMode() {
        return weightsDrawingMode;
    }

    public boolean getUseRandomWeightColors() {
        return useRandomWeightColors;
    }

    private int getBatchSize(Dataset dataset) {
        if (trainingMethod == TrainingMethod.STOCHASTIC) return 1;
        if (trainingMethod == TrainingMethod.MINI_BATCH) return Integer.parseInt(miniBatchSize);
        return dataset.X.length;
    }

    private int[] calculateNetworkLayers(Dataset dataset) {
        var inputNeurons = dataset.X[0].length;
        var outputNeurons = dataset.Y[0].length;
        var hiddenLayers = hiddenLayersDefinition.split(HIDDEN_LAYERS_DEFINITION_SEPARATOR);

        int[] layers = new int[hiddenLayers.length + 2];
        layers[0] = inputNeurons;
        layers[layers.length - 1] = outputNeurons;

        for (int i = 0; i < hiddenLayers.length; i++) {
            layers[i + 1] = Integer.parseInt(hiddenLayers[i].trim());
        }

        return layers;
    }

    @Override
    public void onPropertyChange(String property) {
        if (!property.equals(USE_RANDOM_WEIGHT_COLORS)) return;
        useRandomWeightColors = settings.getBooleanProperty(USE_RANDOM_WEIGHT_COLORS);
        notifyListenerOnSettingsState();
    }

    private void notifyListenerOnSettingsState() {
        listener.onNextState(
                new TrainingPanelSettingsState(
                        isMiniBatchSizeValid(),
                        isHiddenLayersDefinitionValid(),
                        isLearningRateValid(),
                        isMinimumAcceptableErrorValid(),
                        isMaximumNumberOfIterationsValid(),
                        isAdditionalPermutationsPerSampleValid(),
                        isMiniBatchSectionEnabled(),
                        isTrainNeuralNetworkButtonEnabled(),
                        useRandomWeightColors
                )
        );
    }

    private boolean isMiniBatchSizeValid() {
        return UserInputValidator.assertIntegerWithLowerBound(miniBatchSize, MIN_MINI_BATCH_SIZE);
    }

    private boolean isHiddenLayersDefinitionValid() {
        var hiddenLayers = hiddenLayersDefinition.split(HIDDEN_LAYERS_DEFINITION_SEPARATOR, -1);

        for (var layerNeuronCount : hiddenLayers) {
            var isIntegerInRange = UserInputValidator.assertIntegerInRange(
                    layerNeuronCount.trim(),
                    MIN_NEURONS_IN_HIDDEN_LAYER,
                    MAX_NEURONS_IN_HIDDEN_LAYER
            );
            if (!isIntegerInRange) return false;
        }

        return true;
    }

    private boolean isLearningRateValid() {
        return UserInputValidator.assertDoubleWithLowerBound(learningRate, MIN_LEARNING_RATE);
    }

    private boolean isMinimumAcceptableErrorValid() {
        return UserInputValidator.assertDoubleWithLowerBound(minimumAcceptableError, MIN_ACCEPTABLE_ERROR);
    }

    private boolean isMaximumNumberOfIterationsValid() {
        return UserInputValidator.assertIntegerWithLowerBound(maximumNumberOfIterations, MIN_NUMBER_OF_ITERATIONS);
    }

    private boolean isAdditionalPermutationsPerSampleValid() {
        return UserInputValidator.assertIntegerWithLowerBound(additionalPermutationsPerSample, MIN_ADDITIONAL_PERMUTATIONS_PER_SAMPLE);
    }

    private boolean isMiniBatchSectionEnabled() {
        return trainingMethod == TrainingMethod.MINI_BATCH;
    }

    private boolean isTrainNeuralNetworkButtonEnabled() {
        return isMiniBatchSizeValid() &&
                isHiddenLayersDefinitionValid() &&
                isLearningRateValid() &&
                isMinimumAcceptableErrorValid() &&
                isMaximumNumberOfIterationsValid() &&
                isAdditionalPermutationsPerSampleValid() &&
                !isCurrentlyTraining;
    }

    @Override
    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    @Override
    public void addChangeListener(NeuralNetworkChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(NeuralNetworkChangeListener listener) {
        listeners.remove(listener);
    }
}
