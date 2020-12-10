package ui.panels;

import settings.Settings;
import ui.SymbolCanvas;
import ui.SymbolFileWriter;
import util.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DataCollectingPanel extends JPanel {

    private static final Color VALID_TEXT_COLOR = Color.BLACK;
    private static final Color INVALID_TEXT_COLOR = Color.RED;

    private static final int PADDING = 10;

    private final JLabel symbolIdentifierLabel = new JLabel("Symbol identifier:");
    private final JTextField symbolIdentifierField = new JTextField();

    private final Settings settings;
    private final SymbolCanvas symbolCanvas;

    public DataCollectingPanel(Settings settings) {
        this.settings = settings;

        symbolCanvas = new SymbolCanvas(settings);
        symbolCanvas.setDrawingEnabled(!settings.getStringProperty(Settings.SYMBOL_IDENTIFIER).isBlank());
        symbolCanvas.addListener(new SymbolFileWriter(settings));

        setLayout(new BorderLayout());
        add(symbolCanvas, BorderLayout.CENTER);
        add(createSettingsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        panel.add(symbolIdentifierLabel);
        panel.add(symbolIdentifierField);

        symbolIdentifierField.setText(settings.getStringProperty(Settings.SYMBOL_IDENTIFIER));
        symbolIdentifierField.getDocument().addDocumentListener((SimpleDocumentListener) e -> handleSymbolIdentifierTextChange());
        handleSymbolIdentifierTextChange();

        return panel;
    }

    private void handleSymbolIdentifierTextChange() {
        var identifier = symbolIdentifierField.getText();
        var isValid = !identifier.isBlank();

        symbolIdentifierLabel.setForeground(isValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
        symbolCanvas.setDrawingEnabled(isValid);

        settings.setStringProperty(Settings.SYMBOL_IDENTIFIER, symbolIdentifierField.getText());
    }
}
