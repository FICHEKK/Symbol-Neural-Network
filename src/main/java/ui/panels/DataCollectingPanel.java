package ui.panels;

import settings.DataCollectingStageSettings;
import ui.SymbolCanvas;
import ui.SymbolFileWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DataCollectingPanel extends JPanel implements DataCollectingStageSettings {

    private static final String DEFAULT_SYMBOL_IDENTIFIER = "alpha";
    private static final String DEFAULT_SYMBOL_SAVE_DIRECTORY = "symbols";
    private static final boolean DEFAULT_SHOW_REPRESENTATIVE_SYMBOL = true;

    private static final int MIN_REPRESENTATIVE_POINTS = 2;
    private static final int DEFAULT_REPRESENTATIVE_POINTS = 30;
    private static final int MAX_REPRESENTATIVE_POINTS = 1000;

    private final JTextField symbolIdentifierField = new JTextField(DEFAULT_SYMBOL_IDENTIFIER);
    private final JTextField symbolSaveDirectoryField = new JTextField(DEFAULT_SYMBOL_SAVE_DIRECTORY);
    private final JTextField numberOfRepresentativePointsField = new JTextField(String.valueOf(DEFAULT_REPRESENTATIVE_POINTS));
    private final JCheckBox showRepresentativeSymbolCheckbox = new JCheckBox("", DEFAULT_SHOW_REPRESENTATIVE_SYMBOL);

    private static final int PADDING = 10;

    public DataCollectingPanel() {
        setLayout(new BorderLayout());
        initSymbolCanvas();
        initSettingsPanel();
    }

    private void initSymbolCanvas() {
        SymbolCanvas symbolCanvas = new SymbolCanvas(this);
        SymbolFileWriter symbolFileWriter = new SymbolFileWriter(this);
        symbolCanvas.addListener(symbolFileWriter);
        add(symbolCanvas, BorderLayout.CENTER);
    }

    private void initSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        panel.add(new JLabel("Symbol identifier:"));
        panel.add(symbolIdentifierField);

        panel.add(new JLabel("Symbol save directory:"));
        panel.add(symbolSaveDirectoryField);

        panel.add(new JLabel("Number of representative points:"));
        panel.add(numberOfRepresentativePointsField);

        panel.add(new JLabel("Show representative symbol:"));
        panel.add(showRepresentativeSymbolCheckbox);

        add(panel, BorderLayout.SOUTH);
    }

    @Override
    public String getSymbolIdentifier() {
        return symbolIdentifierField.getText();
    }

    @Override
    public String getSymbolSaveDirectory() {
        return symbolSaveDirectoryField.getText();
    }

    @Override
    public int getNumberOfRepresentativePoints() {
        try {
            int numberOfPoints = Integer.parseInt(numberOfRepresentativePointsField.getText());

            if (numberOfPoints < MIN_REPRESENTATIVE_POINTS || numberOfPoints > MAX_REPRESENTATIVE_POINTS) {
                var msg = "'Number of representative points' must be in range [" +
                        MIN_REPRESENTATIVE_POINTS + ", " + MAX_REPRESENTATIVE_POINTS + "]." +
                        "\nUsing the default value of " + DEFAULT_REPRESENTATIVE_POINTS + " instead.";
                JOptionPane.showMessageDialog(this, msg, "Invalid number of points", JOptionPane.WARNING_MESSAGE);
                return DEFAULT_REPRESENTATIVE_POINTS;
            }

            return numberOfPoints;
        } catch (NumberFormatException exception) {
            var msg = "'Number of representative points' is not a valid integer value." +
                    "\nUsing the default value of " + DEFAULT_REPRESENTATIVE_POINTS + " instead.";
            JOptionPane.showMessageDialog(this, msg, "Invalid number of points", JOptionPane.WARNING_MESSAGE);
            return DEFAULT_REPRESENTATIVE_POINTS;
        }
    }

    @Override
    public boolean showRepresentativeSymbol() {
        return showRepresentativeSymbolCheckbox.isSelected();
    }
}
