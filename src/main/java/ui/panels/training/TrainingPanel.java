package ui.panels.training;

import network.NeuralNetwork;
import network.holder.NeuralNetworkChangeListener;
import ui.Colors;
import ui.panels.ModelListener;
import ui.SimpleDocumentListener;
import ui.views.NeuralNetworkView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import static ui.views.NeuralNetworkView.WeightsDrawingMode;

public class TrainingPanel extends JPanel implements ModelListener<TrainingState>, NeuralNetworkChangeListener {

    private static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    private static final int PADDING = 20;

    private static final String TRAIN_BUTTON_START_TEXT = "Train neural network";
    private static final String TRAIN_BUTTON_STOP_TEXT = "Stop training neural network";
    private static final Color TRAIN_BUTTON_START_COLOR = Colors.DARK_BLUE;
    private static final Color TRAIN_BUTTON_STOP_COLOR = Colors.RED;

    private final NeuralNetworkView neuralNetworkView = new NeuralNetworkView();

    private final JLabel trainingMethodLabel = createLabel("Training method:");
    private final JComboBox<TrainingMethod> trainingMethodComboBox = new JComboBox<>();

    private final JLabel miniBatchSizeLabel = createLabel("Mini-batch size:");
    private final JTextField miniBatchSizeField = new JTextField();

    private final JLabel hiddenLayersDefinitionLabel = createLabel("Hidden layers definition (L1 x L2 x ... x Ln):");
    private final JTextField hiddenLayersDefinitionField = new JTextField();

    private final JLabel learningRateLabel = createLabel("Learning rate:");
    private final JTextField learningRateField = new JTextField();

    private final JLabel minimumAcceptableErrorLabel = createLabel("Minimum acceptable error:");
    private final JTextField minimumAcceptableErrorField = new JTextField();

    private final JLabel maxIterationsLabel = createLabel("Maximum number of iterations:");
    private final JTextField maxIterationsField = new JTextField();

    private final JLabel additionalPermutationsPerSampleLabel = createLabel("Additional permutations per sample:");
    private final JTextField additionalPermutationsPerLabelField = new JTextField();

    private final JLabel weightsDrawingModeLabel = createLabel("Weights drawing mode:");
    private final JComboBox<WeightsDrawingMode> weightsDrawingModeComboBox = new JComboBox<>();

    private final JLabel trainingStatusLabel = createLabel("");
    private final JButton trainNeuralNetworkButton = new JButton();

    private final TrainingModel model;

    public TrainingPanel(TrainingModel model) {
        initializeComboBoxes();
        this.model = model;
        this.model.setListener(this);
        this.model.addChangeListener(this);

        trainingMethodComboBox.setSelectedItem(model.getTrainingMethod());
        miniBatchSizeField.setText(model.getMiniBatchSize());
        hiddenLayersDefinitionField.setText(model.getHiddenLayersDefinition());
        learningRateField.setText(model.getLearningRate());
        minimumAcceptableErrorField.setText(model.getMinimumAcceptableError());
        maxIterationsField.setText(model.getMaximumNumberOfIterations());
        additionalPermutationsPerLabelField.setText(model.getAdditionalPermutationsPerSample());
        weightsDrawingModeComboBox.setSelectedItem(model.getWeightsDrawingMode());
        neuralNetworkView.setDrawingMode(model.getWeightsDrawingMode());
        neuralNetworkView.setUseRandomColors(model.getUseRandomWeightColors());
        trainNeuralNetworkButton.setForeground(Colors.WHITE);

        setLayout(new BorderLayout());

        var panel = new JPanel(new BorderLayout());
        panel.add(createSettingsPanel(), BorderLayout.CENTER);
        panel.add(neuralNetworkView, BorderLayout.EAST);

        add(panel, BorderLayout.CENTER);
    }

    private void initializeComboBoxes() {
        trainingMethodComboBox.addItem(TrainingMethod.STOCHASTIC);
        trainingMethodComboBox.addItem(TrainingMethod.MINI_BATCH);
        trainingMethodComboBox.addItem(TrainingMethod.BATCH);

        weightsDrawingModeComboBox.addItem(WeightsDrawingMode.DRAW_ALL);
        weightsDrawingModeComboBox.addItem(WeightsDrawingMode.DRAW_POSITIVE);
        weightsDrawingModeComboBox.addItem(WeightsDrawingMode.DRAW_NEGATIVE);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, PADDING / 4));
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        panel.setBackground(Colors.BACKGROUND);

        panel.add(trainingMethodLabel);
        panel.add(trainingMethodComboBox);

        panel.add(miniBatchSizeLabel);
        panel.add(miniBatchSizeField);

        panel.add(hiddenLayersDefinitionLabel);
        panel.add(hiddenLayersDefinitionField);

        panel.add(learningRateLabel);
        panel.add(learningRateField);

        panel.add(minimumAcceptableErrorLabel);
        panel.add(minimumAcceptableErrorField);

        panel.add(maxIterationsLabel);
        panel.add(maxIterationsField);

        panel.add(additionalPermutationsPerSampleLabel);
        panel.add(additionalPermutationsPerLabelField);

        panel.add(weightsDrawingModeLabel);
        panel.add(weightsDrawingModeComboBox);

        panel.add(trainingStatusLabel);
        panel.add(trainNeuralNetworkButton);

        trainingMethodComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                model.setTrainingMethod((TrainingMethod) e.getItem());
            }
        });

        miniBatchSizeField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setMiniBatchSize(miniBatchSizeField.getText()));

        hiddenLayersDefinitionField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setHiddenLayersDefinition(hiddenLayersDefinitionField.getText()));

        learningRateField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setLearningRate(learningRateField.getText()));

        minimumAcceptableErrorField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setMinimumAcceptableError(minimumAcceptableErrorField.getText()));

        maxIterationsField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setMaximumNumberOfIterations(maxIterationsField.getText()));

        additionalPermutationsPerLabelField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setAdditionalPermutationsPerSample(additionalPermutationsPerLabelField.getText()));

        weightsDrawingModeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                var weightsDrawingMode = (WeightsDrawingMode) e.getItem();
                model.setWeightsDrawingMode(weightsDrawingMode);
                neuralNetworkView.setDrawingMode(weightsDrawingMode);
            }
        });

        trainNeuralNetworkButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.handleTrainButtonClick();
            }
        });

        return panel;
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(ARIAL);
        label.setForeground(Colors.VALID_TEXT);
        return label;
    }

    @Override
    public void onNeuralNetworkChange(NeuralNetwork neuralNetwork) {
        SwingUtilities.invokeLater(() -> neuralNetworkView.setNeuralNetwork(neuralNetwork));
    }

    @Override
    public void onNextState(TrainingState state) {
        if (state instanceof TrainingState.Settings) {
            renderSettings((TrainingState.Settings) state);
        }
        else if (state instanceof TrainingState.FitStatus) {
            renderFitStatus((TrainingState.FitStatus) state);
        }
        else if (state instanceof TrainingState.TrainButton) {
            renderTrainButton((TrainingState.TrainButton) state);
        }
        else if (state instanceof TrainingState.Error) {
            renderError((TrainingState.Error) state);
        }
    }

    private void renderSettings(TrainingState.Settings state) {
        SwingUtilities.invokeLater(() -> {
            miniBatchSizeLabel.setEnabled(state.isMiniBatchSectionEnabled);
            miniBatchSizeField.setEnabled(state.isMiniBatchSectionEnabled);
            miniBatchSizeLabel.setForeground(state.isMiniBatchSizeValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            hiddenLayersDefinitionLabel.setForeground(state.isHiddenLayersDefinitionValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            learningRateLabel.setForeground(state.isLearningRateValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            minimumAcceptableErrorLabel.setForeground(state.isMinimumAcceptableErrorValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            maxIterationsLabel.setForeground(state.isMaximumNumberOfIterationsValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            additionalPermutationsPerSampleLabel.setForeground(state.isAdditionalPermutationsPerSampleValid ? Colors.VALID_TEXT : Colors.INVALID_TEXT);
            neuralNetworkView.setUseRandomColors(state.useRandomWeightColors);
        });
    }

    private void renderFitStatus(TrainingState.FitStatus state) {
        SwingUtilities.invokeLater(() -> trainingStatusLabel.setText("Iterations: " + state.iteration + " | Error: " + state.error));
    }

    private void renderTrainButton(TrainingState.TrainButton state) {
        SwingUtilities.invokeLater(() -> {
            trainNeuralNetworkButton.setEnabled(state.isNetworkBeingFitted || state.isEverySettingValid);
            trainNeuralNetworkButton.setText(state.isNetworkBeingFitted ? TRAIN_BUTTON_STOP_TEXT : TRAIN_BUTTON_START_TEXT);
            trainNeuralNetworkButton.setBackground(state.isNetworkBeingFitted ? TRAIN_BUTTON_STOP_COLOR : TRAIN_BUTTON_START_COLOR);
        });
    }

    private void renderError(TrainingState.Error state) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, state.message, "An error occurred", JOptionPane.ERROR_MESSAGE));
    }
}
