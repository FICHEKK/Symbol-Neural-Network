package ui;

import settings.LearningMethod;
import settings.LearningStageSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class LearningPanel extends JPanel implements LearningStageSettings {

    private static final LearningMethod DEFAULT_LEARNING_METHOD = LearningMethod.MINI_BATCH;
    private static final int DEFAULT_MINI_BATCH_SIZE = 5;
    private static final double DEFAULT_LEARNING_RATE = 0.05;
    private static final double DEFAULT_MIN_ACCEPTABLE_ERROR = 0.01;
    private static final int DEFAULT_MAX_ITERATIONS = 10_000;
    private static final int DEFAULT_REPRESENTATIVE_POINTS = 10;
    private static final String DEFAULT_SYMBOL_LOAD_DIRECTORY = "symbols";

    private final JComboBox<LearningMethod> learningMethodComboBox = new JComboBox<>(new LearningMethod[]{
            LearningMethod.STOCHASTIC,
            LearningMethod.MINI_BATCH,
            LearningMethod.BATCH
    });
    private final JLabel miniBatchSizeLabel = createLabel("Mini batch size:");
    private final JTextField miniBatchSizeField = new JTextField(String.valueOf(DEFAULT_MINI_BATCH_SIZE));

    private final JTextField learningRateField = new JTextField(String.valueOf(DEFAULT_LEARNING_RATE));
    private final JTextField minAcceptableErrorField = new JTextField(String.valueOf(DEFAULT_MIN_ACCEPTABLE_ERROR));
    private final JTextField maxIterationsField = new JTextField(String.valueOf(DEFAULT_MAX_ITERATIONS));
    private final JTextField numberOfRepresentativePointsField = new JTextField(String.valueOf(DEFAULT_REPRESENTATIVE_POINTS));
    private final JTextField symbolLoadDirectoryField = new JTextField(DEFAULT_SYMBOL_LOAD_DIRECTORY);

    private static final int PADDING = 20;
    private static final Font ARIAL = new Font("Arial", Font.PLAIN, 16);

    public LearningPanel() {
        setLayout(new BorderLayout());
        initSettingsPanel();
        initStartLearningPanel();
    }

    private void initSettingsPanel() {
        var gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(2 * PADDING);

        JPanel panel = new JPanel(gridLayout);
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        var learningMethodLabel = new JLabel("Learning method:");
        learningMethodLabel.setFont(ARIAL);
        panel.add(learningMethodLabel);
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

        panel.add(createLabel("Learning rate:"));
        panel.add(learningRateField);

        panel.add(createLabel("Min acceptable error:"));
        panel.add(minAcceptableErrorField);

        panel.add(createLabel("Max iterations:"));
        panel.add(maxIterationsField);

        panel.add(createLabel("Number of representative points:"));
        panel.add(numberOfRepresentativePointsField);

        panel.add(createLabel("Symbol load directory:"));
        panel.add(symbolLoadDirectoryField);

        add(panel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(ARIAL);
        return label;
    }

    private void initStartLearningPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        var learnButton = new JButton("Learn neural network");

        learnButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NeuralNetwork().loadSymbols(getSymbolLoadDirectory(), getNumberOfRepresentativePoints());
            }
        });

        panel.add(learnButton);
        add(panel, BorderLayout.SOUTH);
    }

    @Override
    public int getBatchSize() {
        var learningMethod = (LearningMethod) learningMethodComboBox.getSelectedItem();
        if (learningMethod == null)
            throw new IllegalStateException("Learning method cannot be null.");

        switch (learningMethod) {
            case STOCHASTIC: return 1;
            case MINI_BATCH: return Integer.parseInt(miniBatchSizeField.getText());
            case BATCH: return Integer.MAX_VALUE;
            default: throw new IllegalStateException("Invalid learning method '" + learningMethod + "'.");
        }
    }

    @Override
    public double getLearningRate() {
        return Double.parseDouble(learningRateField.getText());
    }

    @Override
    public double getMinAcceptableError() {
        return Double.parseDouble(minAcceptableErrorField.getText());
    }

    @Override
    public int getMaxIterations() {
        return Integer.parseInt(maxIterationsField.getText());
    }

    @Override
    public int getNumberOfRepresentativePoints() {
        return Integer.parseInt(numberOfRepresentativePointsField.getText());
    }

    @Override
    public String getSymbolLoadDirectory() {
        return symbolLoadDirectoryField.getText();
    }
}
