package ui;

import ui.panels.DataCollectingPanel;
import ui.panels.LearningPanel;
import ui.panels.PredictingPanel;

import javax.swing.*;

public class Application extends JFrame {

    private static final String WINDOW_TITLE = "Symbol Neural Network";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private final DataCollectingPanel dataCollectingPanel = new DataCollectingPanel();
    private final LearningPanel learningPanel = new LearningPanel();
    private final PredictingPanel predictingPanel = new PredictingPanel(dataCollectingPanel, learningPanel);

    private Application() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        initGUI();
    }

    private void initGUI() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.add("Data collecting", dataCollectingPanel);
        tabbedPane.add("Learning", learningPanel);
        tabbedPane.add("Predicting", predictingPanel);
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}
