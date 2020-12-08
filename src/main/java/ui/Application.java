package ui;

import javax.swing.*;

public class Application extends JFrame {

    private static final String WINDOW_TITLE = "Symbol Neural Network";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

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
        tabbedPane.add("Data collecting", new DataCollectingPanel());
        tabbedPane.add("Learning", new LearningPanel());
        tabbedPane.add("Predicting", new JPanel());
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::new);
    }
}
