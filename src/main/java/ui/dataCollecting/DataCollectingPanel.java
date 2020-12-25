package ui.dataCollecting;

import ui.ModelListener;
import ui.SimpleDocumentListener;
import ui.SymbolFileWriter;
import ui.symbolCanvas.SymbolCanvas;
import ui.views.SymbolView;

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
import java.util.ArrayList;
import java.util.List;

public class DataCollectingPanel extends JPanel implements ModelListener<DataCollectingState> {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color VALID_TEXT_COLOR = Color.WHITE;
    private static final Color INVALID_TEXT_COLOR = Color.RED;
    private static final int PADDING = 10;
    private static final int DELETE_SAMPLE_LIST_LIMIT = 10;

    private static final int TABLE_WIDTH = 300;
    private static final int SYMBOL_IDENTIFIER_COLUMN_INDEX = 0;
    private static final int SYMBOL_IDENTIFIER_COLUMN_WIDTH = TABLE_WIDTH * 2 / 5;
    private static final int SYMBOL_COUNT_COLUMN_INDEX = 1;
    private static final int SYMBOL_COUNT_COLUMN_WIDTH = TABLE_WIDTH * 2 / 5;

    private final SymbolCanvas symbolCanvas = new SymbolCanvas();
    private final JTable allSymbolsTable = createTable(3);
    private final JTable singleSymbolTable = createTable(1);
    private final SymbolView symbolView = new SymbolView();

    private final JLabel symbolIdentifierLabel = new JLabel("Symbol identifier:");
    private final JTextField symbolIdentifierField = new JTextField();

    private final DataCollectingModel model;
    private final SymbolFileWriter symbolFileWriter;

    public DataCollectingPanel(DataCollectingModel model, SymbolFileWriter symbolFileWriter) {
        this.model = model;
        this.symbolFileWriter = symbolFileWriter;
        initGUI();
    }

    private void initGUI() {
        model.setListener(this);

        initSymbolCanvas();
        initAllSymbolsTableListeners();
        initSingleTableListeners();

        setLayout(new BorderLayout());
        add(symbolCanvas, BorderLayout.CENTER);
        add(createSymbolTablePanel(), BorderLayout.EAST);
        add(createSymbolIdentifierPanel(), BorderLayout.SOUTH);

        symbolIdentifierField.setText(model.getSymbolIdentifier());
    }

    private void initSymbolCanvas() {
        symbolCanvas.setNumberOfRepresentativePoints(model.getNumberOfRepresentativePoints());
        symbolCanvas.setShowRepresentativePoints(model.getShowRepresentativePointsWhileDataCollecting());
        symbolCanvas.setDrawingEnabled(model.isDrawingEnabled());

        symbolCanvas.addSymbolFinishListener(symbolFileWriter);
        symbolCanvas.addSymbolFinishListener(model);
    }

    private void initAllSymbolsTableListeners() {
        allSymbolsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || allSymbolsTable.getSelectedRow() == -1) return;
            var identifier = allSymbolsTable.getValueAt(allSymbolsTable.getSelectedRow(), SYMBOL_IDENTIFIER_COLUMN_INDEX).toString();
            model.setSelectedSymbolIdentifier(identifier);
        });

        allSymbolsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var selectedRow = allSymbolsTable.getSelectedRow();
                if (selectedRow == -1 || e.getClickCount() != 2) return;

                var identifier = allSymbolsTable.getValueAt(selectedRow, SYMBOL_IDENTIFIER_COLUMN_INDEX).toString();
                symbolIdentifierField.setText(identifier);
            }
        });

        allSymbolsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_DELETE) return;

                var identifiers = stringifyTableColumn(allSymbolsTable, allSymbolsTable.getSelectedRows());
                if (identifiers.size() == 0) return;

                var title = "Delete symbols?";
                var message = "Delete following symbols?" + System.lineSeparator() + elementPerLine(identifiers);
                var decision = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (decision != JOptionPane.YES_OPTION) return;

                try {
                    model.deleteIdentifiers(identifiers);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void initSingleTableListeners() {
        singleSymbolTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || singleSymbolTable.getSelectedRow() == -1) return;
            model.setSelectedSample(singleSymbolTable.getValueAt(singleSymbolTable.getSelectedRow(), 0).toString());
        });

        singleSymbolTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_DELETE) return;

                var samples = stringifyTableColumn(singleSymbolTable, singleSymbolTable.getSelectedRows());
                if (samples.size() == 0) return;

                var title = "Delete samples?";
                var message = "Delete following samples?" + System.lineSeparator() + elementPerLine(samples);
                var decision = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (decision != JOptionPane.YES_OPTION) return;

                try {
                    model.deleteSamples(samples);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private JPanel createSymbolTablePanel() {
        var displayPanel = new JPanel(new GridLayout(0, 1));
        displayPanel.add(new JScrollPane(allSymbolsTable));
        displayPanel.add(new JScrollPane(singleSymbolTable));
        displayPanel.add(symbolView);
        return displayPanel;
    }

    private JPanel createSymbolIdentifierPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBackground(PANEL_BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        panel.add(symbolIdentifierLabel);
        panel.add(symbolIdentifierField);

        symbolIdentifierField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
                model.setSymbolIdentifier(symbolIdentifierField.getText()));

        return panel;
    }

    @Override
    public void onNextState(DataCollectingState state) {
        if (state instanceof DataCollectingState.SymbolCanvas) {
            renderSymbolCanvas((DataCollectingState.SymbolCanvas) state);
        }
        else if (state instanceof DataCollectingState.AllSymbolsTable) {
            renderAllSymbolsTable((DataCollectingState.AllSymbolsTable) state);
        }
        else if (state instanceof DataCollectingState.SingleSymbolTable) {
            renderSingleSymbolTable((DataCollectingState.SingleSymbolTable) state);
        }
        else if (state instanceof DataCollectingState.SymbolView) {
            renderSymbolView((DataCollectingState.SymbolView) state);
        }
        else if (state instanceof DataCollectingState.SymbolIdentifier) {
            renderSymbolIdentifier((DataCollectingState.SymbolIdentifier) state);
        }
    }

    private void renderSymbolCanvas(DataCollectingState.SymbolCanvas state) {
        symbolCanvas.setNumberOfRepresentativePoints(state.numberOfRepresentativePoints);
        symbolCanvas.setShowRepresentativePoints(state.showRepresentativePoints);
        symbolCanvas.setDrawingEnabled(state.isDrawingEnabled);
    }

    private void renderAllSymbolsTable(DataCollectingState.AllSymbolsTable state) {
        var identifierCount = state.symbolToSampleCount.size();

        var header = new String[] {
                "Symbols (" + identifierCount + ")",
                "# of samples (" + state.totalSampleCount + ")",
                "# of points"
        };

        var rowData = new Object[identifierCount][];

        int index = 0;
        for (var entry : state.symbolToSampleCount.entrySet()) {
            rowData[index++] = new Object[]{entry.getKey(), entry.getValue(), state.numberOfPoints};
        }

        ((DefaultTableModel) allSymbolsTable.getModel()).setDataVector(rowData, header);

        allSymbolsTable.getColumnModel().getColumn(SYMBOL_IDENTIFIER_COLUMN_INDEX).setPreferredWidth(SYMBOL_IDENTIFIER_COLUMN_WIDTH);
        allSymbolsTable.getColumnModel().getColumn(SYMBOL_COUNT_COLUMN_INDEX).setPreferredWidth(SYMBOL_COUNT_COLUMN_WIDTH);
    }

    private void renderSingleSymbolTable(DataCollectingState.SingleSymbolTable state) {
        var model = (DefaultTableModel) singleSymbolTable.getModel();

        if (state.identifier == null) {
            model.setDataVector(new Object[0][], new String[0]);
            return;
        }

        var sampleCount = state.samples.size();
        var rowData = new Object[sampleCount][];

        for (int i = 0; i < sampleCount; i++) {
            rowData[i] = new Object[]{state.samples.get(i)};
        }

        model.setDataVector(rowData, new String[] { state.identifier });
    }

    private void renderSymbolView(DataCollectingState.SymbolView state) {
        symbolView.setSymbol(state.normalizedPoints);
    }

    private void renderSymbolIdentifier(DataCollectingState.SymbolIdentifier state) {
        symbolIdentifierLabel.setForeground(state.isSymbolIdentifierValid ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR);
        symbolCanvas.setDrawingEnabled(state.isSymbolIdentifierValid);
    }

    private static String elementPerLine(List<String> strings) {
        var sb = new StringBuilder();
        var size = Math.min(strings.size(), DELETE_SAMPLE_LIST_LIMIT);

        for (int i = 0; i < size; i++) {
            sb.append("• ").append(strings.get(i)).append(System.lineSeparator());
        }

        if (strings.size() > DELETE_SAMPLE_LIST_LIMIT) {
            sb.append("...and ").append(strings.size() - DELETE_SAMPLE_LIST_LIMIT).append(" more.");
        }

        return sb.toString();
    }

    private static List<String> stringifyTableColumn(JTable table, int[] rowIndices) {
        var strings = new ArrayList<String>();

        for (int rowIndex : rowIndices) {
            strings.add(table.getModel().getValueAt(rowIndex, 0).toString());
        }

        return strings;
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

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        var renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, renderer);

        return table;
    }
}
