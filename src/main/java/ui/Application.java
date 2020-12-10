package ui;

import settings.*;
import ui.panels.DataCollectingPanel;
import ui.panels.SettingsPanel;
import ui.panels.LearningPanel;
import ui.panels.PredictingPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JFrame {

    private static final String WINDOW_TITLE = "Symbol Neural Network";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    private final Settings settings = new SettingsImpl();

    private final DataCollectingPanel dataCollectingPanel = new DataCollectingPanel(settings);
    private final LearningPanel learningPanel = new LearningPanel(settings);
    private final PredictingPanel predictingPanel = new PredictingPanel(settings, learningPanel);
    private final SettingsPanel globalSettingsPanel = new SettingsPanel(settings);

    private Application() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                settings.save();
            }
        });

        initGUI();
    }

    private void initGUI() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.add("Data collecting", dataCollectingPanel);
        tabbedPane.add("Learning", learningPanel);
        tabbedPane.add("Predicting", predictingPanel);
        tabbedPane.add("Settings", globalSettingsPanel);
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}
