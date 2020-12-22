package ui.panels;

import settings.Settings;
import settings.SettingsListener;
import structures.Point;
import ui.SimpleDocumentListener;
import ui.symbolCanvas.SymbolCanvas;
import ui.symbolCanvas.SymbolCanvasFinishListener;
import ui.SymbolFileWriter;
import ui.views.SymbolView;
import util.DatasetLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataCollectingPanel extends JPanel implements SettingsListener, SymbolCanvasFinishListener {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color VALID_TEXT_COLOR = Color.WHITE;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final int TABLE_WIDTH = 270;
    private static final int PADDING = 10;

    private final JLabel symbolIdentifierLabel = new JLabel("Symbol identifier:");
    private final JTextField symbolIdentifierField = new JTextField();

    private final String[] allSymbolsTableColumnNames = {"Symbol", "# of samples", "# of points"};
    private final JTable allSymbolsTable = createTable(3);
    private final JTable singleSymbolTable = createTable(1);
    private final SymbolView symbolView = new SymbolView();
    private final SymbolCanvas symbolCanvas = new SymbolCanvas();

    private final Settings settings;

    public DataCollectingPanel(Settings settings) {
        this.settings = settings;
        settings.addListener(this);

        symbolCanvas.setNumberOfRepresentativePoints(settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));
        symbolCanvas.setShowRepresentativePoints(settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING));
        symbolCanvas.setDrawingEnabled(!settings.getStringProperty(Settings.SYMBOL_IDENTIFIER).isBlank());
        symbolCanvas.addSymbolFinishListener(new SymbolFileWriter(settings));
        symbolCanvas.addSymbolFinishListener(this);

        symbolCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (symbolCanvas.isDrawingEnabled()) return;
                JOptionPane.showMessageDialog(
                        null,
                        "You must specify symbol identifier before drawing." + System.lineSeparator() +
                                "You can do so at the bottom of this tab.",
                        "Identifier not specified",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setLayout(new BorderLayout());
        add(symbolCanvas, BorderLayout.CENTER);
        add(createSettingsPanel(), BorderLayout.SOUTH);
        add(createDataDisplayPanel(), BorderLayout.EAST);

        updateAllSymbolsTable();

        allSymbolsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_DELETE) return;

                var identifiers = stringifyTableColumn(allSymbolsTable, allSymbolsTable.getSelectedRows());

                var title = "Delete symbols?";
                var message = "Delete following symbols?" + System.lineSeparator() + elementPerLine(identifiers);
                var decision = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (decision != JOptionPane.YES_OPTION) return;

                try {
                    deleteIdentifiers(identifiers);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                updateSingleSymbolTable(null);
                updateAllSymbolsTable();
                symbolView.clear();
            }
        });

        singleSymbolTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_DELETE) return;

                var samples = stringifyTableColumn(singleSymbolTable, singleSymbolTable.getSelectedRows());

                var title = "Delete samples?";
                var message = "Delete following samples?" + System.lineSeparator() + elementPerLine(samples);
                var decision = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (decision != JOptionPane.YES_OPTION) return;

                var identifier = singleSymbolTable.getColumnName(0);

                try {
                    deleteSamples(identifier, samples);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                updateAllSymbolsTable();
                updateSingleSymbolTable(identifier);
                symbolView.clear();
            }
        });
    }

    private JPanel createDataDisplayPanel() {
        var displayPanel = new JPanel(new GridLayout(0, 1));

        allSymbolsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || allSymbolsTable.getSelectedRow() == -1) return;
            var identifier = allSymbolsTable.getValueAt(allSymbolsTable.getSelectedRow(), 0).toString();
            updateSingleSymbolTable(identifier);
            symbolView.clear();
        });

        singleSymbolTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || singleSymbolTable.getSelectedRow() == -1) return;

            var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
            var numberOfRepresentativePoints = settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
            var identifier = singleSymbolTable.getColumnName(0);
            var sample = singleSymbolTable.getValueAt(singleSymbolTable.getSelectedRow(), 0).toString();
            var path = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints), identifier, sample);

            var normalizedPoints = new ArrayList<Point>();

            try {
                var lines = Files.readAllLines(path);

                for (int i = 0; i < lines.size() / 2; i++) {
                    var x = Double.parseDouble(lines.get(i * 2));
                    var y = Double.parseDouble(lines.get(i * 2 + 1));
                    normalizedPoints.add(new Point(x, y));
                }

                symbolView.setSymbol(normalizedPoints);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        displayPanel.add(new JScrollPane(allSymbolsTable));
        displayPanel.add(new JScrollPane(singleSymbolTable));
        displayPanel.add(symbolView);

        return displayPanel;
    }

    private static JTable createTable(int columns) {
        var table = new JTable(0, columns);

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

        return table;
    }

    private void updateAllSymbolsTable() {
        var symbolToSampleCount = DatasetLoader.getSymbolToSampleCount(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)
        );

        var rowData = new Object[symbolToSampleCount.size()][];
        var pointCount = settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);

        int index = 0;
        for (var entry : symbolToSampleCount.entrySet()) {
            rowData[index++] = new Object[]{entry.getKey(), entry.getValue(), pointCount};
        }

        ((DefaultTableModel) allSymbolsTable.getModel()).setDataVector(rowData, allSymbolsTableColumnNames);
    }

    private void updateSingleSymbolTable(String identifier) {
        if (identifier == null) {
            ((DefaultTableModel) singleSymbolTable.getModel()).setDataVector(new Object[0][], new String[0]);
            return;
        }

        var samples = DatasetLoader.getSymbolSamples(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS),
                identifier
        );

        var rowData = new Object[samples.size()][];

        for (int i = 0; i < samples.size(); i++) {
            rowData[i] = new Object[]{samples.get(i)};
        }

        ((DefaultTableModel) singleSymbolTable.getModel()).setDataVector(rowData, new String[]{identifier});
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBackground(PANEL_BACKGROUND_COLOR);
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
        if (property.equals(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING)) {
            symbolCanvas.setShowRepresentativePoints(settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING));
        }
        else if (property.equals(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)) {
            symbolCanvas.setNumberOfRepresentativePoints(settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));
            updateAllSymbolsTable();
            updateSingleSymbolTable(null);
            symbolView.setSymbol(null);
        }
    }

    @Override
    public void onNextSymbolFinish(List<Point> normalizedPoints) {
        var identifier = settings.getStringProperty(Settings.SYMBOL_IDENTIFIER);

        if (singleSymbolTable.getColumnCount() != 0) {
            var selectedIdentifier = singleSymbolTable.getColumnName(0);

            if (identifier.equals(selectedIdentifier)) {
                updateSingleSymbolTable(identifier);
            }
        }

        updateAllSymbolsTable();
    }

    private void deleteIdentifiers(List<String> identifiers) throws IOException {
        var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
        var numberOfPoints = settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);

        for (var identifier : identifiers) {
            var identifierDirectory = Paths.get(loadDirectory, numberOfPoints, identifier);
            deleteDirectory(identifierDirectory);
        }
    }

    private void deleteSamples(String identifier, List<String> samples) throws IOException {
        var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
        var numberOfPoints = settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
        var identifierDirectory = Paths.get(loadDirectory, numberOfPoints, identifier);

        for (var sample : samples) {
            Files.delete(identifierDirectory.resolve(sample));
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        var files = directory.toFile().listFiles();

        if (files != null) {
            for (var file : files) {
                deleteDirectory(file.toPath());
            }
        }

        Files.delete(directory);
    }

    private static String elementPerLine(List<String> strings) {
        var sb = new StringBuilder();
        strings.forEach(s -> sb.append("• ").append(s).append(System.lineSeparator()));
        return sb.toString();
    }

    private static List<String> stringifyTableColumn(JTable table, int[] rowIndices) {
        var strings = new ArrayList<String>();

        for (int rowIndex : rowIndices) {
            strings.add(table.getModel().getValueAt(rowIndex, 0).toString());
        }

        return strings;
    }
}
