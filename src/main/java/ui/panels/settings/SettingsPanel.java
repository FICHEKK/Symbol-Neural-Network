package ui.panels.settings;

import ui.ModelListener;
import ui.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsPanel extends JPanel implements ModelListener<SettingsState> {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color VALID_TEXT_COLOR = Color.WHITE;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final Color BORDER_COLOR = Color.ORANGE;

    private static final int PANEL_PADDING = 20;
    private static final int WINDOW_PADDING = PANEL_PADDING * 2;

    private static final LayoutManager PANEL_LAYOUT = new GridLayout(0, 2, PANEL_PADDING, PANEL_PADDING / 2);
    private static final Border BORDER = new LineBorder(BORDER_COLOR, 1);
    private static final Font BORDER_FONT = new Font("Arial", Font.PLAIN, 14);

    private final JLabel numberOfRepresentativePointsLabel = createLabel("Number of representative points (from " +
            SettingsModel.MIN_REPRESENTATIVE_POINTS + " to " + SettingsModel.MAX_REPRESENTATIVE_POINTS + "):");
    private final JTextField numberOfRepresentativePointsField = new JTextField();

    private final JLabel symbolSaveDirectoryLabel = createLabel("Symbol save directory:");
    private final JTextField symbolSaveDirectoryField = new JTextField();

    private final JLabel showRepresentativePointsWhileDataCollectingLabel = createLabel("Show representative points while data collecting:");
    private final JCheckBox showRepresentativePointsWhileDataCollectingCheckbox = new JCheckBox();

    private final JLabel symbolLoadDirectoryLabel = createLabel("Symbol load directory:");
    private final JTextField symbolLoadDirectoryField = new JTextField();

    private final JLabel useRandomWeightColorsLabel = createLabel("Use random weight colors:");
    private final JCheckBox useRandomWeightColorsCheckbox = new JCheckBox();

    private final JLabel showRepresentativePointsWhilePredictingLabel = createLabel("Show representative points while predicting:");
    private final JCheckBox showRepresentativePointsWhilePredictingCheckbox = new JCheckBox();

    private final JLabel updateHistogramWhileDrawingLabel = createLabel("Update histogram while drawing:");
    private final JCheckBox updateHistogramWhileDrawingCheckbox = new JCheckBox();

    private final SettingsModel model;

    public SettingsPanel(SettingsModel model) {
        this.model = model;
        model.setListener(this);
        setBackground(PANEL_BACKGROUND_COLOR);
        setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
        setLayout(new BorderLayout());
        add(createAllSectionsPanel(), BorderLayout.NORTH);

        numberOfRepresentativePointsField.setText(model.getNumberOfRepresentativePoints());
        symbolSaveDirectoryField.setText(model.getSymbolSaveDirectory());
        showRepresentativePointsWhileDataCollectingCheckbox.setSelected(model.isShowRepresentativePointsWhileDataCollecting());
        symbolLoadDirectoryField.setText(model.getSymbolLoadDirectory());
        useRandomWeightColorsCheckbox.setSelected(model.isUseRandomWeightColors());
        showRepresentativePointsWhilePredictingCheckbox.setSelected(model.isShowRepresentativePointsWhilePredicting());
        updateHistogramWhileDrawingCheckbox.setSelected(model.isUpdateHistogramWhileDrawing());
    }

    private JPanel createAllSectionsPanel() {
        var panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(PANEL_PADDING, 0, PANEL_PADDING, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;

        panel.add(wrapComponentInTitledBorder(createDataCollectingPanel(), "Data collecting"), modifyConstraints(gridBagConstraints, 0));
        panel.add(wrapComponentInTitledBorder(createTrainingPanel(), "Training"), modifyConstraints(gridBagConstraints, 1));
        panel.add(wrapComponentInTitledBorder(createPredictingPanel(), "Predicting"), modifyConstraints(gridBagConstraints, 2));

        return panel;
    }

    private GridBagConstraints modifyConstraints(GridBagConstraints constraints, int gridY) {
        constraints.gridy = gridY;
        return constraints;
    }

    private JPanel wrapComponentInTitledBorder(JComponent component, String title) {
        var panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        panel.setBackground(PANEL_BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(BORDER, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, BORDER_FONT, BORDER_COLOR));
        return panel;
    }

    private JPanel createDataCollectingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        panel.add(numberOfRepresentativePointsLabel);
        panel.add(numberOfRepresentativePointsField);

        panel.add(symbolSaveDirectoryLabel);
        panel.add(symbolSaveDirectoryField);

        panel.add(showRepresentativePointsWhileDataCollectingLabel);
        panel.add(showRepresentativePointsWhileDataCollectingCheckbox);

        numberOfRepresentativePointsField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setNumberOfRepresentativePoints(numberOfRepresentativePointsField.getText()));

        symbolSaveDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setSymbolSaveDirectory(symbolSaveDirectoryField.getText()));

        showRepresentativePointsWhileDataCollectingCheckbox.addItemListener(e ->
                model.setShowRepresentativePointsWhileDataCollecting(e.getStateChange() == ItemEvent.SELECTED));

        return panel;
    }

    private JPanel createTrainingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        panel.add(symbolLoadDirectoryLabel);
        panel.add(symbolLoadDirectoryField);

        panel.add(useRandomWeightColorsLabel);
        panel.add(useRandomWeightColorsCheckbox);

        symbolLoadDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setSymbolLoadDirectory(symbolLoadDirectoryField.getText()));

        useRandomWeightColorsCheckbox.addItemListener(e ->
                model.setUseRandomWeightColors(e.getStateChange() == ItemEvent.SELECTED));

        return panel;
    }

    private JPanel createPredictingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        panel.add(showRepresentativePointsWhilePredictingLabel);
        panel.add(showRepresentativePointsWhilePredictingCheckbox);

        panel.add(updateHistogramWhileDrawingLabel);
        panel.add(updateHistogramWhileDrawingCheckbox);

        showRepresentativePointsWhilePredictingCheckbox.addItemListener(e ->
                model.setShowRepresentativePointsWhilePredicting(e.getStateChange() == ItemEvent.SELECTED));

        updateHistogramWhileDrawingCheckbox.addItemListener(e ->
                model.setUpdateHistogramWhileDrawing(e.getStateChange() == ItemEvent.SELECTED));

        return panel;
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setForeground(VALID_TEXT_COLOR);
        return label;
    }

    @Override
    public void onNextState(SettingsState state) {
        if (state instanceof SettingsState.DataCollectingSection) {
            renderDataCollectingSection((SettingsState.DataCollectingSection) state);
        }
        else if (state instanceof SettingsState.TrainingSection) {
            renderTrainingSection((SettingsState.TrainingSection) state);
        }
    }

    private void renderDataCollectingSection(SettingsState.DataCollectingSection state) {
        numberOfRepresentativePointsLabel.setForeground(state.isNumberOfRepresentativePointsValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
        symbolSaveDirectoryLabel.setForeground(state.isSymbolSaveDirectoryValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
    }

    private void renderTrainingSection(SettingsState.TrainingSection state) {
        symbolLoadDirectoryLabel.setForeground(state.isSymbolLoadDirectoryValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
    }
}
