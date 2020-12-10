package ui.panels;

import settings.Settings;
import util.SimpleDocumentListener;
import util.UserInputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsPanel extends JPanel {

    private static final int MIN_REPRESENTATIVE_POINTS = 2;
    private static final int MAX_REPRESENTATIVE_POINTS = 1000;
    private static final int PADDING = 20;

    private final JTextField numberOfRepresentativePointsField = new JTextField();

    private final Settings settings;

    public SettingsPanel(Settings settings) {
        this.settings = settings;

        setLayout(new BorderLayout());
        add(createSettingsPanel(), BorderLayout.NORTH);
    }

    private JPanel createSettingsPanel() {
        var panel = new JPanel(new GridLayout(0, 2, 0, PADDING / 2));
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        addNumberOfRepresentativePointsRow(panel);
        addSymbolSaveDirectoryRow(panel);
        addSymbolLoadDirectoryRow(panel);
        addShowRepresentativePointsRow(panel);

        return panel;
    }

    private void addNumberOfRepresentativePointsRow(JPanel panel) {
        var numberOfRepresentativePointsLabel = new JLabel("Number of representative points " +
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

            numberOfRepresentativePointsLabel.setForeground(isValid ? Color.BLACK : Color.RED);

            if (!isValid) return;

            settings.setStringProperty(
                    Settings.NUMBER_OF_REPRESENTATIVE_POINTS,
                    numberOfRepresentativePointsField.getText()
            );
        });
    }

    private void addSymbolSaveDirectoryRow(JPanel panel) {
        panel.add(new JLabel("Symbol save directory:"));

        var symbolSaveDirectoryField = new JTextField();
        panel.add(symbolSaveDirectoryField);

        symbolSaveDirectoryField.setText(settings.getStringProperty(Settings.SYMBOL_SAVE_DIRECTORY));
        symbolSaveDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e -> settings.setStringProperty(
                Settings.SYMBOL_SAVE_DIRECTORY,
                symbolSaveDirectoryField.getText()
        ));
    }

    private void addSymbolLoadDirectoryRow(JPanel panel) {
        panel.add(new JLabel("Symbol load directory:"));

        var symbolLoadDirectoryField = new JTextField();
        panel.add(symbolLoadDirectoryField);

        symbolLoadDirectoryField.setText(settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY));
        symbolLoadDirectoryField.getDocument().addDocumentListener((SimpleDocumentListener) e -> settings.setStringProperty(
                Settings.SYMBOL_LOAD_DIRECTORY,
                symbolLoadDirectoryField.getText()
        ));
    }

    private void addShowRepresentativePointsRow(JPanel panel) {
        panel.add(new JLabel("Show representative points:"));

        var showRepresentativeSymbolCheckbox = new JCheckBox();
        panel.add(showRepresentativeSymbolCheckbox);

        showRepresentativeSymbolCheckbox.setSelected(settings.getBooleanProperty(Settings.SHOULD_SHOW_REPRESENTATIVE_POINTS));
        showRepresentativeSymbolCheckbox.addItemListener(e -> settings.setBooleanProperty(
                Settings.SHOULD_SHOW_REPRESENTATIVE_POINTS,
                e.getStateChange() == ItemEvent.SELECTED
        ));
    }
}
