package ui.panels.help;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HelpPanel extends JPanel {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color VALID_TEXT_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.ORANGE;
    private static final String BORDER_COLOR_HEX = String.format("#%02x%02x%02x", BORDER_COLOR.getRed(), BORDER_COLOR.getGreen(), BORDER_COLOR.getBlue());
    private static final Font BORDER_FONT = new Font("Arial", Font.PLAIN, 14);

    private static final int PANEL_PADDING = 20;
    private static final int SCROLLING_SPEED = 16;

    public HelpPanel() {
        setBackground(PANEL_BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        add(createAllSectionsPanel());
    }

    private JScrollPane createAllSectionsPanel() {
        var panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(PANEL_PADDING * 2, PANEL_PADDING * 2, PANEL_PADDING * 2, PANEL_PADDING * 2));

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(PANEL_PADDING, 0, PANEL_PADDING, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;

        panel.add(wrapComponentInTitledBorder(createDataCollectingPanel(), "Data collecting"), modifyConstraints(gridBagConstraints, 0));
        panel.add(wrapComponentInTitledBorder(createTrainingPanel(), "Training"), modifyConstraints(gridBagConstraints, 1));
        panel.add(wrapComponentInTitledBorder(createPredictingPanel(), "Predicting"), modifyConstraints(gridBagConstraints, 2));
        panel.add(wrapComponentInTitledBorder(createSettingsPanel(), "Settings"), modifyConstraints(gridBagConstraints, 3));

        var scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLING_SPEED);
        return scrollPane;
    }

    private GridBagConstraints modifyConstraints(GridBagConstraints constraints, int gridY) {
        constraints.gridy = gridY;
        return constraints;
    }

    private JPanel wrapComponentInTitledBorder(JComponent component, String title) {
        var panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        panel.setBackground(PANEL_BACKGROUND_COLOR);

        panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.TOP,
                BORDER_FONT,
                BORDER_COLOR
        ));

        return panel;
    }

    private JPanel createDataCollectingPanel() {
        return createPanel(
                "The primary purpose of this stage is to generate training data for the neural network.",
                "Generating training data is simple, consisting of only 2 steps:",

                title("Step 1:"),
                "Label the symbol you are drawing by specifying the <i>Symbol identifier</i> at the bottom of the screen.",

                title("Step 2:"),
                "Draw the symbol by holding and dragging the left mouse button on the canvas.",
                "You can draw your symbol in any number of lines.",
                "When you are done drawing, press the right mouse button to save the drawn symbol.",
                "",
                "NOTE: If you are not satisfied with the drawn symbol, you can discard it by pressing the middle mouse button.",
                "NOTE: You can inspect your dataset on the right side of the screen."
        );
    }

    private JPanel createTrainingPanel() {
        return createPanel(
                "Training stage is where previously generated training data gets converted into a neural network.",
                "There are plenty of options and here are their explanations:",

                title("Training method"),
                "<i>Stochastic</i> - Neural network weights are adjusted after each sample.",
                "<i>Mini-batch</i> - Neural network weights are adjusted after <i>Mini-batch size</i> samples.",
                "<i>Batch</i> - Neural network weights are adjusted after all samples are processed.",

                title("Hidden layers definition"),
                "Defines the exact architecture of the hidden layers.",
                "For example, <i>10 x 8 x 8</i> means the first hidden layer has 10 neurons, while second and third both have 8 neurons.",
                "NOTE: It is often best to only have a single hidden layer with number of neurons lower than input and higher than output.",

                title("Learning rate"),
                "Indicates the rate at which the network learns. Tuning this value must be done with care.",
                "Making this value too small will make the training process very slow.",
                "On the other hand, making this value too high will only cause the network to diverge.",

                title("Minimum acceptable error"),
                "Once the network error goes below this threshold, the training process will stop.",

                title("Maximum number of iterations"),
                "Once the training exceeds this number of iterations, the training process will stop.",

                title("Additional permutations per sample"),
                "This option will increase the dataset by creating additional samples from existing ones.",
                "For example, if our dataset has 5 samples and this option is set to 2, for each of the 5 samples 2 additional will be generated.",
                "Final dataset size will be 5 (original) + 10 (additional) = 15 (total).",
                "NOTE: This option is only used for analytic purposes and is best left at value 0.",

                title("Weights drawing mode"),
                "<i>All</i> - Drawn neural network will contain all of the weights.",
                "<i>Positive</i> - Drawn neural network will contain only positive weights.",
                "<i>Negative</i> - Drawn neural network will contain only negative weights."
        );
    }

    private JPanel createPredictingPanel() {
        return createPanel(
                "This stage can only be used once the neural network has been trained.",
                "If you haven't done so yet, please consult <i>Data collecting</i> and <i>Training</i> sections above.",
                "",
                "Once your network is trained, making predictions is trivial: simply draw a symbol and let the neural network guess!",
                "At the top of the screen a histogram is shown. It displays the neural network output layer values.",
                "At the bottom of the screen a neural network prediction is displayed, where the message will vary based on how certain the network is.",
                "",
                "NOTE: To remove the currently drawn symbol and start a new one, press the drawing canvas with the right mouse button."
        );
    }

    private JPanel createSettingsPanel() {
        return createPanel(
                "Last, but not least, there are many settings which can be tweaked:",

                title("<font color='" + BORDER_COLOR_HEX + "'>Data collecting:</font>"),
                title("Number of representative points"),
                "Defines how many points will be sampled from the originally drawn symbol.",
                "For example, if this value is set to 10, every symbol that is drawn will be converted into 10 representative points.",
                "Consequently, this option defines the number of neurons in the input layer, which is double this value, as each point consists of x and y value.",
                title("Symbol save directory"),
                "Specifies the path of the directory where all of the collected symbol samples will be saved.",
                title("Show representative points while data collecting"),
                "When turned on, representative points will be shown in real-time as the symbol is being drawn in the data collecting stage.",
                title("Show continuous curve index in symbol view"),
                "When turned on, symbol view (window in the bottom right corner) will display in what order the parts of the symbol were drawn, starting from index 0.",
                title("Show representative points in symbol view"),
                "When turned on, representative points will be shown in symbol view window.",
                title("Animate symbol in symbol view"),
                "When turned on, symbol view will animate symbol's drawing process in the exact same way as it was originally done by the artist.",

                title("<font color='" + BORDER_COLOR_HEX + "'>Training:</font>"),
                title("Symbol load directory"),
                "Specifies the path of the directory from where symbols will be loaded when training the neural network.",
                title("Use random weight colors"),
                "Instead of positive and negative weight colors, random colors are used when drawing the neural network in the training stage.",

                title("<font color='" + BORDER_COLOR_HEX + "'>Predicting:</font>"),
                title("Show representative points while predicting"),
                "When turned on, representative points will be shown in real-time as the symbol is being drawn in the predicting stage.",
                title("Update histogram while drawing"),
                "When turned on, histogram will be updated live as the symbol is being drawn in the predicting stage."
        );
    }

    private static String title(String title) {
        return "<br><b>" + title + "</b>";
    }

    private static JPanel createPanel(String... texts) {
        var panel = new JPanel(new GridLayout(0, 1, PANEL_PADDING, PANEL_PADDING / 2));
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(PANEL_BACKGROUND_COLOR);
        panel.add(createHtmlLabel(texts));
        return panel;
    }

    private static JLabel createHtmlLabel(String... lines) {
        var sb = new StringBuilder();

        for (var line : lines) {
            sb.append(line);
            sb.append("<br>");
        }

        return createLabel("<html>" + sb.toString() + "</html>");
    }

    private static JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setForeground(VALID_TEXT_COLOR);
        return label;
    }
}
