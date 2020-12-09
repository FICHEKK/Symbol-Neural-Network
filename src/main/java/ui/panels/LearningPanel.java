package ui.panels;

import network.activation.Sigmoid;
import network.initializers.RandomWeightInitializer;
import settings.LearningMethod;
import settings.LearningStageSettings;
import structures.Dataset;
import network.NeuralNetwork;
import ui.views.NeuralNetworkView;
import util.DatasetLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;

public class LearningPanel extends JPanel implements LearningStageSettings {

    private static final LearningMethod DEFAULT_LEARNING_METHOD = LearningMethod.MINI_BATCH;
    private static final int DEFAULT_MINI_BATCH_SIZE = 5;
    private static final String DEFAULT_HIDDEN_LAYERS_DEFINITION = "10x10";
    private static final String HIDDEN_LAYERS_DEFINITION_SEPARATOR = "x";
    private static final double DEFAULT_LEARNING_RATE = 0.05;
    private static final double DEFAULT_MIN_ACCEPTABLE_ERROR = 0.01;
    private static final int DEFAULT_MAX_ITERATIONS = 10_000;
    private static final int DEFAULT_REPRESENTATIVE_POINTS = 30;
    private static final String DEFAULT_SYMBOL_LOAD_DIRECTORY = "symbols";

    private final JComboBox<LearningMethod> learningMethodComboBox = new JComboBox<>(new LearningMethod[]{
            LearningMethod.STOCHASTIC,
            LearningMethod.MINI_BATCH,
            LearningMethod.BATCH
    });
    private final JLabel miniBatchSizeLabel = createLabel("Mini batch size:");
    private final JTextField miniBatchSizeField = new JTextField(String.valueOf(DEFAULT_MINI_BATCH_SIZE));

    private final JComboBox<NeuralNetworkView.DrawingMode> drawingModeComboBox = new JComboBox<>(new NeuralNetworkView.DrawingMode[]{
            NeuralNetworkView.DrawingMode.DRAW_ALL_WEIGHTS,
            NeuralNetworkView.DrawingMode.DRAW_POSITIVE_WEIGHTS_ONLY,
            NeuralNetworkView.DrawingMode.DRAW_NEGATIVE_WEIGHTS_ONLY
    });

    private final JTextField hiddenLayersDefinitionField = new JTextField(DEFAULT_HIDDEN_LAYERS_DEFINITION);
    private final JTextField learningRateField = new JTextField(String.valueOf(DEFAULT_LEARNING_RATE));
    private final JTextField minAcceptableErrorField = new JTextField(String.valueOf(DEFAULT_MIN_ACCEPTABLE_ERROR));
    private final JTextField maxIterationsField = new JTextField(String.valueOf(DEFAULT_MAX_ITERATIONS));
    private final JTextField numberOfRepresentativePointsField = new JTextField(String.valueOf(DEFAULT_REPRESENTATIVE_POINTS));
    private final JTextField symbolLoadDirectoryField = new JTextField(DEFAULT_SYMBOL_LOAD_DIRECTORY);

    private final NeuralNetworkView neuralNetworkView = new NeuralNetworkView();

    private static final int PADDING = 4;
    private static final Font ARIAL = new Font("Arial", Font.PLAIN, 16);

    private NeuralNetwork neuralNetwork;
    private Dataset dataset;

    public LearningPanel() {
        setLayout(new BorderLayout());

        var panel = new JPanel(new BorderLayout());
        panel.add(createSettingsPanel(), BorderLayout.CENTER);
        panel.add(neuralNetworkView, BorderLayout.EAST);

        add(panel, BorderLayout.CENTER);
    }

    private JPanel createSettingsPanel() {
        var gridLayout = new GridLayout(0, 1);
        gridLayout.setVgap(2 * PADDING);

        JPanel panel = new JPanel(gridLayout);
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        panel.add(createLabel("Learning method:"));
        learningMethodComboBox.setSelectedItem(DEFAULT_LEARNING_METHOD);
        panel.add(learningMethodComboBox);

        learningMethodComboBox.addItemListener(e -> {
            var learningMethod = (LearningMethod) e.getItem();

            if (e.getStateChange() == ItemEvent.SELECTED) {
                miniBatchSizeLabel.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
                miniBatchSizeField.setEnabled(learningMethod == LearningMethod.MINI_BATCH);
            }
        });

        panel.add(miniBatchSizeLabel);
        panel.add(miniBatchSizeField);

        panel.add(createLabel("Hidden layers definition (L1 x L2 x ... x Ln):"));
        panel.add(hiddenLayersDefinitionField);

        panel.add(createLabel("Learning rate:"));
        panel.add(learningRateField);

        panel.add(createLabel("Minimum acceptable error:"));
        panel.add(minAcceptableErrorField);

        panel.add(createLabel("Maximum number of iterations:"));
        panel.add(maxIterationsField);

        panel.add(createLabel("Number of representative points:"));
        panel.add(numberOfRepresentativePointsField);

        panel.add(createLabel("Symbol load directory:"));
        panel.add(symbolLoadDirectoryField);

        panel.add(createLabel("Drawing mode:"));
        panel.add(drawingModeComboBox);

        drawingModeComboBox.addItemListener(e -> {
            var drawingMode = (NeuralNetworkView.DrawingMode) e.getItem();

            if (e.getStateChange() == ItemEvent.SELECTED) {
                neuralNetworkView.setDrawingMode(drawingMode);
            }
        });

        panel.add(createStartLearningButton());

        return panel;
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(ARIAL);
        return label;
    }

    private JButton createStartLearningButton() {
        var learnButton = new JButton("Learn neural network");

        learnButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataset = DatasetLoader.loadDataset(
                            getSymbolLoadDirectory(),
                            getNumberOfRepresentativePoints()
                    );

                    var layers = calculateNetworkLayers(
                            dataset.X[0].length,
                            dataset.y[0].length
                    );

                    neuralNetwork = new NeuralNetwork(new RandomWeightInitializer(-0.5, 0.5), Sigmoid.getInstance(), layers)
                            .withLearningRate(getLearningRate())
                            .withBatchSize(getBatchSize(dataset))
                            .withMaxIterations(getMaxIterations())
                            .withMinAcceptableError(getMinAcceptableError());

                    neuralNetworkView.setNeuralNetwork(neuralNetwork);
                    neuralNetwork.fit(dataset.X, dataset.y);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return learnButton;
    }

    private int[] calculateNetworkLayers(int inputNeurons, int outputNeurons) {
        var definition = hiddenLayersDefinitionField.getText();
        var hiddenLayers = definition.replaceAll("\\s+", "").split(HIDDEN_LAYERS_DEFINITION_SEPARATOR);

        int[] layers = new int[hiddenLayers.length + 2];
        layers[0] = inputNeurons;
        layers[layers.length - 1] = outputNeurons;

        for (int i = 0; i < hiddenLayers.length; i++) {
            layers[i + 1] = Integer.parseInt(hiddenLayers[i]);
        }

        return layers;
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

    private double getLearningRate() {
        return Double.parseDouble(learningRateField.getText());
    }

    private double getMinAcceptableError() {
        return Double.parseDouble(minAcceptableErrorField.getText());
    }

    private int getMaxIterations() {
        return Integer.parseInt(maxIterationsField.getText());
    }

    private int getNumberOfRepresentativePoints() {
        return Integer.parseInt(numberOfRepresentativePointsField.getText());
    }

    private String getSymbolLoadDirectory() {
        return symbolLoadDirectoryField.getText();
    }
}
