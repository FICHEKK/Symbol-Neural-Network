import settings.DataCollectingStageSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Application extends JFrame implements DataCollectingStageSettings {

    private static final String WINDOW_TITLE = "Symbol Neural Network";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private static final String DEFAULT_SYMBOL_IDENTIFIER = "alpha";
    private static final String DEFAULT_SYMBOL_SAVE_DIRECTORY = "symbols";
    private static final boolean DEFAULT_SHOW_REPRESENTATIVE_SYMBOL = true;

    private static final int MIN_REPRESENTATIVE_POINTS = 2;
    private static final int DEFAULT_REPRESENTATIVE_POINTS = 10;
    private static final int MAX_REPRESENTATIVE_POINTS = 1000;

    private final SymbolCanvas symbolCanvas = new SymbolCanvas(this);
    private final SymbolFileWriter symbolFileWriter = new SymbolFileWriter(this);

    private final JTextField symbolIdentifierField = new JTextField(DEFAULT_SYMBOL_IDENTIFIER);
    private final JTextField symbolSaveDirectoryField = new JTextField(DEFAULT_SYMBOL_SAVE_DIRECTORY);
    private final JTextField numberOfRepresentativePointsField = new JTextField(String.valueOf(DEFAULT_REPRESENTATIVE_POINTS));
    private final JCheckBox showRepresentativeSymbolCheckbox = new JCheckBox("", DEFAULT_SHOW_REPRESENTATIVE_SYMBOL);

    private Application() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        initGUI();
    }

    @Override
    public String getSymbolIdentifier() {
        return symbolIdentifierField.getText();
    }

    @Override
    public String getSymbolSaveDirectory() {
        return symbolSaveDirectoryField.getText();
    }

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

    private void initGUI() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.add("Data collecting", createDataCollectingPanel());
        tabbedPane.add("Learning", new JPanel());
        tabbedPane.add("Predicting", new JPanel());
        add(tabbedPane);
    }

    private JPanel createDataCollectingPanel() {
        var dataCollectingPanel = new JPanel(new BorderLayout());

        dataCollectingPanel.add(symbolCanvas);
        symbolCanvas.addListener(symbolFileWriter);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Symbol identifier:"));
        panel.add(symbolIdentifierField);
        panel.add(new JLabel("Symbol save directory:"));
        panel.add(symbolSaveDirectoryField);
        panel.add(new JLabel("Number of representative points:"));
        panel.add(numberOfRepresentativePointsField);
        panel.add(new JLabel("Show representative symbol:"));
        panel.add(showRepresentativeSymbolCheckbox);

        dataCollectingPanel.add(panel, BorderLayout.SOUTH);

        return dataCollectingPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}
