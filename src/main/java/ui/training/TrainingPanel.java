package ui.training;

import network.NeuralNetwork;
import ui.SimpleDocumentListener;
import ui.training.state.TrainingPanelFitState;
import ui.training.state.TrainingPanelSettingsState;
import ui.training.state.TrainingPanelState;
import ui.views.NeuralNetworkView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import static ui.views.NeuralNetworkView.WeightsDrawingMode;

public class TrainingPanel extends JPanel implements TrainingPanelModelListener {

    private static final Color VALID_TEXT_COLOR = Color.LIGHT_GRAY;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final Color SETTINGS_PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    private static final int PADDING = 20;

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

    private final JLabel weightsDrawingModeLabel = createLabel("Weights drawing mode:");
    private final JComboBox<WeightsDrawingMode> weightsDrawingModeComboBox = new JComboBox<>();

    private final JLabel trainingStatusLabel = createLabel("");
    private final JButton trainNeuralNetworkButton = new JButton("Train neural network");

    private final TrainingPanelModel model;

    public TrainingPanel(TrainingPanelModel model) {
        initializeComboBoxes();
        this.model = model;
        this.model.setListener(this);

        trainingMethodComboBox.setSelectedItem(model.getTrainingMethod());
        miniBatchSizeField.setText(model.getMiniBatchSize());
        hiddenLayersDefinitionField.setText(model.getHiddenLayersDefinition());
        learningRateField.setText(model.getLearningRate());
        minimumAcceptableErrorField.setText(model.getMinimumAcceptableError());
        maxIterationsField.setText(model.getMaximumNumberOfIterations());
        weightsDrawingModeComboBox.setSelectedItem(model.getWeightsDrawingMode());
        neuralNetworkView.setDrawingMode(model.getWeightsDrawingMode());
        neuralNetworkView.setUseRandomColors(model.getUseRandomWeightColors());

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
        panel.setBackground(SETTINGS_PANEL_BACKGROUND_COLOR);

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
                model.trainNeuralNetwork();
            }
        });

        return panel;
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(ARIAL);
        label.setForeground(VALID_TEXT_COLOR);
        return label;
    }

    @Override
    public void onNeuralNetworkChange(NeuralNetwork neuralNetwork) {
        SwingUtilities.invokeLater(() -> neuralNetworkView.setNeuralNetwork(neuralNetwork));
    }

    @Override
    public void onNextState(TrainingPanelState state) {
        if (state instanceof TrainingPanelSettingsState) {
            renderSettings((TrainingPanelSettingsState) state);
        }
        else if (state instanceof TrainingPanelFitState) {
            renderFitStatus((TrainingPanelFitState) state);
        }
    }

    private void renderSettings(TrainingPanelSettingsState state) {
        SwingUtilities.invokeLater(() -> {
            miniBatchSizeLabel.setEnabled(state.isMiniBatchSectionEnabled);
            miniBatchSizeField.setEnabled(state.isMiniBatchSectionEnabled);
            miniBatchSizeLabel.setForeground(state.isMiniBatchSizeValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
            hiddenLayersDefinitionLabel.setForeground(state.isHiddenLayersDefinitionValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
            learningRateLabel.setForeground(state.isLearningRateValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
            minimumAcceptableErrorLabel.setForeground(state.isMinimumAcceptableErrorValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
            maxIterationsLabel.setForeground(state.isMaximumNumberOfIterationsValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
            neuralNetworkView.setUseRandomColors(state.useRandomWeightColors);
            trainNeuralNetworkButton.setEnabled(state.isTrainNeuralNetworkButtonEnabled);
        });
    }

    private void renderFitStatus(TrainingPanelFitState state) {
        SwingUtilities.invokeLater(() -> trainingStatusLabel.setText("Iterations: " + state.iteration + " | Error: " + state.error));
    }
}
