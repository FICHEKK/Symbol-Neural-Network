package ui.panels;

import settings.Settings;
import ui.SimpleDocumentListener;
import util.UserInputValidator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsPanel extends JPanel {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color VALID_TEXT_COLOR = Color.WHITE;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final Color BORDER_COLOR = Color.ORANGE;

    private static final int MIN_REPRESENTATIVE_POINTS = 2;
    private static final int MAX_REPRESENTATIVE_POINTS = 100;
    private static final int PANEL_PADDING = 20;
    private static final int WINDOW_PADDING = PANEL_PADDING * 2;

    private static final LayoutManager PANEL_LAYOUT = new GridLayout(0, 2, PANEL_PADDING, PANEL_PADDING / 2);
    private static final Border BORDER = new LineBorder(BORDER_COLOR, 1);
    private static final Font BORDER_FONT = new Font("Arial", Font.PLAIN, 14);
    private final JTextField numberOfRepresentativePointsField = new JTextField();

    private final Settings settings;

    public SettingsPanel(Settings settings) {
        this.settings = settings;
        setBackground(PANEL_BACKGROUND_COLOR);
        setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
        setLayout(new BorderLayout());
        add(createAllSectionsPanel(), BorderLayout.NORTH);
    }

    private JPanel createAllSectionsPanel() {
        var panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(PANEL_PADDING, 0, PANEL_PADDING, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel.add(wrapComponentInTitledBorder(createGeneralPanel(), "General"), gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panel.add(wrapComponentInTitledBorder(createDataCollectingPanel(), "Data collecting"), gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panel.add(wrapComponentInTitledBorder(createTrainingPanel(), "Training"), gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panel.add(wrapComponentInTitledBorder(createPredictingPanel(), "Predicting"), gridBagConstraints);

        return panel;
    }

    private JPanel wrapComponentInTitledBorder(JComponent component, String title) {
        var panel = new JPanel(new BorderLayout());

        panel.add(component, BorderLayout.CENTER);
        panel.setBackground(PANEL_BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BORDER,
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.TOP,
                BORDER_FONT,
                BORDER_COLOR
        ));

        return panel;
    }

    private JPanel createGeneralPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        addNumberOfRepresentativePointsRow(panel);

        return panel;
    }

    private JPanel createDataCollectingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        addSymbolSaveDirectoryRow(panel);
        addShowRepresentativePointsRow(panel, Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING);

        return panel;
    }

    private JPanel createTrainingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        addSymbolLoadDirectoryRow(panel);
        addUseRandomWeightColorsRow(panel);

        return panel;
    }

    private JPanel createPredictingPanel() {
        var panel = new JPanel(PANEL_LAYOUT);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        addShowRepresentativePointsRow(panel, Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING);

        return panel;
    }

    private void addNumberOfRepresentativePointsRow(JPanel panel) {
        var numberOfRepresentativePointsLabel = createLabel("Number of representative points " +
                "[" + MIN_REPRESENTATIVE_POINTS + ", " + MAX_REPRESENTATIVE_POINTS + "]:");

        panel.add(numberOfRepresentativePointsLabel);
        panel.add(numberOfRepresentativePointsField);

        numberOfRepresentativePointsField.setText(settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));
        numberOfRepresentativePointsField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            var isValid = UserInputValidator.assertIntegerInRange(
                    numberOfRepresentativePointsField.getText(),
                    MIN_REPRESENTATIVE_POINTS,
                    MAX_REPRESENTATIVE_POINTS
            );

            numberOfRepresentativePointsLabel.setForeground(isValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);

            if (!isValid) return;

            settings.setStringProperty(
                    Settings.NUMBER_OF_REPRESENTATIVE_POINTS,
                    numberOfRepresentativePointsField.getText()
            );
        });
    }

    private void addSymbolSaveDirectoryRow(JPanel panel) {
        panel.add(createLabel("Symbol save directory:"));

        var symbolSaveDirectoryField = new JTextField();
        panel.add(symbolSaveDirectoryField);

        symbolSaveDirectoryField.setText(settings.getStringProperty(Settings.SYMBOL_SAVE_DIRECTORY));
        symbolSaveDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e -> settings.setStringProperty(
                Settings.SYMBOL_SAVE_DIRECTORY,
                symbolSaveDirectoryField.getText()
        ));
    }

    private void addSymbolLoadDirectoryRow(JPanel panel) {
        panel.add(createLabel("Symbol load directory:"));

        var symbolLoadDirectoryField = new JTextField();
        panel.add(symbolLoadDirectoryField);

        symbolLoadDirectoryField.setText(settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY));
        symbolLoadDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e -> settings.setStringProperty(
                Settings.SYMBOL_LOAD_DIRECTORY,
                symbolLoadDirectoryField.getText()
        ));
    }

    private void addShowRepresentativePointsRow(JPanel panel, String settingsProperty) {
        panel.add(createLabel("Show representative points:"));

        var showRepresentativeSymbolCheckbox = new JCheckBox();
        panel.add(showRepresentativeSymbolCheckbox);

        showRepresentativeSymbolCheckbox.setSelected(settings.getBooleanProperty(settingsProperty));
        showRepresentativeSymbolCheckbox.addItemListener(e -> settings.setBooleanProperty(
                settingsProperty,
                e.getStateChange() == ItemEvent.SELECTED
        ));
    }

    private void addUseRandomWeightColorsRow(JPanel panel) {
        panel.add(createLabel("Use random weight colors:"));

        var useRandomWeightColorsCheckbox = new JCheckBox();
        panel.add(useRandomWeightColorsCheckbox);

        useRandomWeightColorsCheckbox.setSelected(settings.getBooleanProperty(Settings.USE_RANDOM_WEIGHT_COLORS));
        useRandomWeightColorsCheckbox.addItemListener(e -> settings.setBooleanProperty(
                Settings.USE_RANDOM_WEIGHT_COLORS,
                e.getStateChange() == ItemEvent.SELECTED
        ));
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setForeground(VALID_TEXT_COLOR);
        return label;
    }
}
