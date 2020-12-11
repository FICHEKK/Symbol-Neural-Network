package ui.panels;

import network.NeuralNetwork;
import network.activation.Sigmoid;
import network.initializers.RandomWeightInitializer;
import settings.LearningMethod;
import settings.LearningSettings;
import settings.Settings;
import settings.SettingsListener;
import structures.Dataset;
import ui.views.NeuralNetworkView;
import util.DatasetLoader;
import ui.SimpleDocumentListener;
import util.UserInputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.function.BiFunction;

import static settings.Settings.*;

public class LearningPanel extends JPanel implements LearningSettings, SettingsListener {

    private static final Color VALID_TEXT_COLOR = Color.BLACK;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final Font ARIAL = new Font("Arial", Font.PLAIN, 16);

    private static final int MIN_MINI_BATCH_SIZE = 1;
    private static final double MIN_LEARNING_RATE = 0;
    private static final double MIN_ACCEPTABLE_ERROR = 0;
    private static final int MIN_NUMBER_OF_ITERATIONS = 1;

    private static final String HIDDEN_LAYERS_DEFINITION_SEPARATOR = "x";
    private static final int PADDING = 20;

    private final JComboBox<LearningMethod> learningMethodComboBox = new JComboBox<>(new LearningMethod[]{
            LearningMethod.STOCHASTIC,
            LearningMethod.MINI_BATCH,
            LearningMethod.BATCH
    });
    private final JLabel miniBatchSizeLabel = createLabel("Mini-batch size:");
    private final JTextField miniBatchSizeField = new JTextField();

    private final JComboBox<NeuralNetworkView.WeightsDrawingMode> drawingModeComboBox = new JComboBox<>(new NeuralNetworkView.WeightsDrawingMode[]{
            NeuralNetworkView.WeightsDrawingMode.DRAW_ALL,
            NeuralNetworkView.WeightsDrawingMode.DRAW_POSITIVE,
            NeuralNetworkView.WeightsDrawingMode.DRAW_NEGATIVE
    });

    private final JTextField hiddenLayersDefinitionField = new JTextField();
    private final JTextField learningRateField = new JTextField();
    private final JTextField minAcceptableErrorField = new JTextField();
    private final JTextField maxIterationsField = new JTextField();
    private final JLabel learningInfoLabel = new JLabel();
    private final NeuralNetworkView neuralNetworkView = new NeuralNetworkView();

    private final Settings settings;
    private NeuralNetwork neuralNetwork;
    private Dataset dataset;

    public LearningPanel(Settings settings) {
        this.settings = settings;
        settings.addListener(this);
        setLayout(new BorderLayout());

        var panel = new JPanel(new BorderLayout());
        panel.add(createSettingsPanel(), BorderLayout.CENTER);
        panel.add(neuralNetworkView, BorderLayout.EAST);
        neuralNetworkView.setUseRandomColors(settings.getBooleanProperty(USE_RANDOM_WEIGHT_COLORS));

        add(panel, BorderLayout.CENTER);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, PADDING / 4));
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        addLearningMethod(panel);
        addMiniBatchSize(panel);
        addHiddenLayersDefinition(panel);
        addLearningRate(panel);
        addMinimumAcceptableError(panel);
        addMaximumNumberOfIterations(panel);
        addWeightsDrawingMode(panel);
        panel.add(learningInfoLabel);
        addStartLearningButton(panel);

        return panel;
    }

    private void addLearningMethod(JPanel panel) {
        panel.add(createLabel("Learning method:"));
        panel.add(learningMethodComboBox);

        learningMethodComboBox.addItemListener(e -> {
            var learningMethod = (LearningMethod) e.getItem();

            if (e.getStateChange() == ItemEvent.SELECTED) {
                miniBatchSizeLabel.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
                miniBatchSizeField.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
                settings.setStringProperty(LEARNING_METHOD, learningMethod.toString());
            }
        });

        var learningMethod = LearningMethod.from(settings.getStringProperty(LEARNING_METHOD));
        learningMethodComboBox.setSelectedItem(learningMethod);
        miniBatchSizeLabel.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
        miniBatchSizeField.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
    }

    private void addMiniBatchSize(JPanel panel) {
        panel.add(miniBatchSizeLabel);
        panel.add(miniBatchSizeField);
        addPropertyField(miniBatchSizeLabel, miniBatchSizeField, MINI_BATCH_SIZE, MIN_MINI_BATCH_SIZE, UserInputValidator::assertIntegerWithLowerBound);
    }

    private void addHiddenLayersDefinition(JPanel panel) {
        var hiddenLayersDefinitionLabel = createLabel("Hidden layers definition (L1 x L2 x ... x Ln):");
        panel.add(hiddenLayersDefinitionLabel);
        panel.add(hiddenLayersDefinitionField);
        hiddenLayersDefinitionField.setText(settings.getStringProperty(HIDDEN_LAYERS_DEFINITION));
        hiddenLayersDefinitionField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            var isValid = isHiddenLayersDefinitionValid();
            hiddenLayersDefinitionLabel.setForeground(isValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);

            if (isValid) {
                settings.setStringProperty(HIDDEN_LAYERS_DEFINITION, hiddenLayersDefinitionField.getText());
            }
        });
    }

    private void addLearningRate(JPanel panel) {
        var learningRateLabel = createLabel("Learning rate:");
        panel.add(learningRateLabel);
        panel.add(learningRateField);
        addPropertyField(learningRateLabel, learningRateField, LEARNING_RATE, MIN_LEARNING_RATE, UserInputValidator::assertDoubleWithLowerBound);
    }

    private void addMinimumAcceptableError(JPanel panel) {
        var minAcceptableErrorLabel = createLabel("Minimum acceptable error:");
        panel.add(minAcceptableErrorLabel);
        panel.add(minAcceptableErrorField);
        addPropertyField(minAcceptableErrorLabel, minAcceptableErrorField, MINIMUM_ACCEPTABLE_ERROR, MIN_ACCEPTABLE_ERROR, UserInputValidator::assertDoubleWithLowerBound);
    }

    private void addMaximumNumberOfIterations(JPanel panel) {
        var maxIterationsLabel = createLabel("Maximum number of iterations:");
        panel.add(maxIterationsLabel);
        panel.add(maxIterationsField);
        addPropertyField(maxIterationsLabel, maxIterationsField, MAXIMUM_NUMBER_OF_ITERATIONS, MIN_NUMBER_OF_ITERATIONS, UserInputValidator::assertIntegerWithLowerBound);
    }

    private void addWeightsDrawingMode(JPanel panel) {
        panel.add(createLabel("Weights drawing mode:"));
        panel.add(drawingModeComboBox);

        drawingModeComboBox.addItemListener(e -> {
            var drawingMode = (NeuralNetworkView.WeightsDrawingMode) e.getItem();

            if (e.getStateChange() == ItemEvent.SELECTED) {
                neuralNetworkView.setDrawingMode(drawingMode);
                settings.setStringProperty(DRAWING_MODE, drawingMode.toString());
            }
        });

        drawingModeComboBox.setSelectedItem(NeuralNetworkView.WeightsDrawingMode.from(settings.getStringProperty(DRAWING_MODE)));
    }

    private <T> void addPropertyField(JLabel label, JTextField field, String property, T lowerBound, BiFunction<String, T, Boolean> predicate) {
        field.setText(settings.getStringProperty(property));

        field.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            var isValid = predicate.apply(field.getText(), lowerBound);
            label.setForeground(isValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);

            if (isValid) {
                settings.setStringProperty(property, field.getText());
            }
        });
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(ARIAL);
        return label;
    }

    private void addStartLearningButton(JPanel panel) {
        var learnButton = new JButton("Learn neural network");

        learnButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areAllSettingsProperlyDefined()) return;

                var loadDir = settings.getStringProperty(SYMBOL_LOAD_DIRECTORY);
                var points = settings.getIntProperty(NUMBER_OF_REPRESENTATIVE_POINTS);

                try {
                    dataset = DatasetLoader.loadDataset(loadDir, points);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                if (dataset == null) {
                    var msg = "Error loading symbols: Directory '" + loadDir + "/" + points + "' does not exist.";
                    JOptionPane.showMessageDialog(null, msg);
                    return;
                }

                var layers = calculateNetworkLayers(
                        dataset.X[0].length,
                        dataset.y[0].length
                );

                neuralNetwork = new NeuralNetwork(new RandomWeightInitializer(-0.5, 0.5), Sigmoid.getInstance(), layers)
                        .withLearningRate(Double.parseDouble(learningRateField.getText()))
                        .withBatchSize(getBatchSize(dataset))
                        .withMaxIterations(Integer.parseInt(maxIterationsField.getText()))
                        .withMinAcceptableError(Double.parseDouble(minAcceptableErrorField.getText()));

                neuralNetworkView.setNeuralNetwork(neuralNetwork);

                var iterations = neuralNetwork.fit(dataset.X, dataset.y);
                var error = neuralNetwork.calculateNetworkError(dataset.X, dataset.y);
                learningInfoLabel.setText("Iterations: " + iterations + " | Error: " + error);
            }
        });

        panel.add(learnButton);
    }

    private boolean isHiddenLayersDefinitionValid() {
        var definition = hiddenLayersDefinitionField.getText();
        var hiddenLayers = definition.split(HIDDEN_LAYERS_DEFINITION_SEPARATOR, -1);

        for (var layer : hiddenLayers) {
            var isInteger = UserInputValidator.assertInteger(layer.trim());
            if (!isInteger) return false;
        }

        return true;
    }

    private int[] calculateNetworkLayers(int inputNeurons, int outputNeurons) {
        var definition = hiddenLayersDefinitionField.getText();
        var hiddenLayers = definition.split(HIDDEN_LAYERS_DEFINITION_SEPARATOR);

        int[] layers = new int[hiddenLayers.length + 2];
        layers[0] = inputNeurons;
        layers[layers.length - 1] = outputNeurons;

        for (int i = 0; i < hiddenLayers.length; i++) {
            layers[i + 1] = Integer.parseInt(hiddenLayers[i].trim());
        }

        return layers;
    }

    private boolean areAllSettingsProperlyDefined() {
        var isMiniBatchSizeValid = UserInputValidator.assertIntegerWithLowerBound(miniBatchSizeField.getText(), MIN_MINI_BATCH_SIZE);
        var isHiddenLayersDefinitionValid = isHiddenLayersDefinitionValid();
        var isLearningRateValid = UserInputValidator.assertDoubleWithLowerBound(learningRateField.getText(), MIN_LEARNING_RATE);
        var isMinAcceptableErrorValid = UserInputValidator.assertDoubleWithLowerBound(minAcceptableErrorField.getText(), MIN_ACCEPTABLE_ERROR);
        var isMaxNumberOfIterationsValid = UserInputValidator.assertIntegerWithLowerBound(maxIterationsField.getText(), MIN_NUMBER_OF_ITERATIONS);

        var illDefinedProperties = new StringBuilder();

        if (!isMiniBatchSizeValid) illDefinedProperties.append("\n• Mini-batch size");
        if (!isHiddenLayersDefinitionValid) illDefinedProperties.append("\n• Hidden layers definition");
        if (!isLearningRateValid) illDefinedProperties.append("\n• Learning rate");
        if (!isMinAcceptableErrorValid) illDefinedProperties.append("\n• Minimum acceptable error");
        if (!isMaxNumberOfIterationsValid) illDefinedProperties.append("\n• Maximum number of iterations");

        if (illDefinedProperties.length() == 0) return true;

        JOptionPane.showMessageDialog(null, "The following properties were not defined correctly:" + illDefinedProperties.toString());
        return false;
    }

    @Override
    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    @Override
    public Dataset getDataset() {
        return dataset;
    }

    private int getBatchSize(Dataset dataset) {
        var learningMethod = (LearningMethod) learningMethodComboBox.getSelectedItem();
        if (learningMethod == null)
            throw new IllegalStateException("Learning method cannot be null.");

        switch (learningMethod) {
            case STOCHASTIC:
                return 1;
            case MINI_BATCH:
                return Integer.parseInt(miniBatchSizeField.getText());
            case BATCH:
                return dataset.X.length;
            default:
                throw new IllegalStateException("Invalid learning method '" + learningMethod + "'.");
        }
    }

    @Override
    public void onPropertyChange(String property) {
        if (!property.equals(USE_RANDOM_WEIGHT_COLORS)) return;
        var useRandomColors = settings.getBooleanProperty(USE_RANDOM_WEIGHT_COLORS);
        neuralNetworkView.setUseRandomColors(useRandomColors);
    }
}
