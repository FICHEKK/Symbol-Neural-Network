package ui.panels;

import settings.Settings;
import settings.SettingsListener;
import structures.Point;
import ui.SymbolCanvas;
import ui.SymbolCanvasListener;
import ui.SymbolFileWriter;
import util.DatasetLoader;
import util.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DataCollectingPanel extends JPanel implements SettingsListener, SymbolCanvasListener {

    private static final Color VALID_TEXT_COLOR = Color.BLACK;
    private static final Color INVALID_TEXT_COLOR = Color.RED;

    private static final int TABLE_WIDTH = 270;
    private static final int PADDING = 10;

    private final JLabel symbolIdentifierLabel = new JLabel("Symbol identifier:");
    private final JTextField symbolIdentifierField = new JTextField();

    private final String[] columnNames = { "Symbol", "# of samples", "# of points" };
    private final JTable table = new JTable(0, 3);
    private Map<String, Integer> symbolToSampleCount;

    private final Settings settings;
    private final SymbolCanvas symbolCanvas;

    public DataCollectingPanel(Settings settings) {
        this.settings = settings;
        settings.addListener(this);

        symbolCanvas = new SymbolCanvas(settings);
        symbolCanvas.setDrawingEnabled(!settings.getStringProperty(Settings.SYMBOL_IDENTIFIER).isBlank());
        symbolCanvas.addListener(new SymbolFileWriter(settings));
        symbolCanvas.addListener(this);

        setLayout(new BorderLayout());
        add(symbolCanvas, BorderLayout.CENTER);
        add(createSettingsPanel(), BorderLayout.SOUTH);
        add(createSymbolTable(), BorderLayout.EAST);

        updateSymbolToSampleCount();
        updateSymbolTable();
    }

    private JScrollPane createSymbolTable() {
        var model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        table.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, 0));
        table.getTableHeader().setReorderingAllowed(false);

        var renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, renderer);

        return new JScrollPane(table);
    }

    private void updateSymbolToSampleCount() {
        symbolToSampleCount = DatasetLoader.getSymbolToSampleCount(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)
        );
    }

    private void updateSymbolTable() {
        var rowData = new Object[symbolToSampleCount.size()][];
        var pointCount = settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);

        int index = 0;
        for (var entry : symbolToSampleCount.entrySet()) {
            rowData[index++] = new Object[] {entry.getKey(), entry.getValue(), pointCount };
        }

        var model = (DefaultTableModel) table.getModel();
        model.setDataVector(rowData, columnNames);
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

    @Override
    public void onPropertyChange(String property) {
        if (!property.equals(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)) return;
        updateSymbolToSampleCount();
        updateSymbolTable();
    }

    @Override
    public void onNextSymbol(List<Point> points) {
        var identifier = settings.getStringProperty(Settings.SYMBOL_IDENTIFIER);

        if (symbolToSampleCount.containsKey(identifier)) {
            var count = symbolToSampleCount.get(identifier);
            symbolToSampleCount.put(identifier, count + 1);
        }
        else {
            symbolToSampleCount.put(identifier, 1);
        }

        updateSymbolTable();
    }
}
