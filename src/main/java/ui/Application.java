package ui;

import settings.Settings;
import settings.SettingsImpl;
import ui.panels.dataCollecting.DataCollectingPanel;
import ui.panels.dataCollecting.DataCollectingModel;
import ui.panels.help.HelpPanel;
import ui.panels.predicting.PredictingModel;
import ui.panels.predicting.PredictingPanel;
import ui.panels.settings.SettingsPanel;
import ui.panels.settings.SettingsModel;
import ui.panels.training.TrainingPanel;
import ui.panels.training.TrainingModel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JFrame {

    private static final String WANTED_LOOK_AND_FEEL = "Nimbus";
    private static final String WINDOW_TITLE = "Symbol Neural Network";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    private final Settings settings = new SettingsImpl();

    private final DataCollectingModel dataCollectingModel = new DataCollectingModel(settings);
    private final DataCollectingPanel dataCollectingPanel = new DataCollectingPanel(dataCollectingModel, new SymbolFileWriter(settings));

    private final TrainingModel trainingModel = new TrainingModel(settings);
    private final TrainingPanel trainingPanel = new TrainingPanel(trainingModel);

    private final PredictingModel predictingModel = new PredictingModel(settings, trainingModel);
    private final PredictingPanel predictingPanel = new PredictingPanel(predictingModel);

    private final SettingsModel settingsPanelModel = new SettingsModel(settings);
    private final SettingsPanel globalSettingsPanel = new SettingsPanel(settingsPanelModel);

    private final HelpPanel helpPanel = new HelpPanel();

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
        tabbedPane.add("Training", trainingPanel);
        tabbedPane.add("Predicting", predictingPanel);
        tabbedPane.add("Settings", globalSettingsPanel);
        tabbedPane.add("Help", helpPanel);
        add(tabbedPane);
    }

    public static void main(String[] args) {
        setWantedLookAndFeelIfPossible();
        SwingUtilities.invokeLater(Application::new);
    }

    private static void setWantedLookAndFeelIfPossible() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals(WANTED_LOOK_AND_FEEL)) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            // ignore
        }
    }
}
