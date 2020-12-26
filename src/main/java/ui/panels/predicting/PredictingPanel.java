package ui.panels.predicting;

import ui.ModelListener;
import ui.views.HistogramView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PredictingPanel extends JPanel implements ModelListener<PredictingState> {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color PANEL_TEXT_COLOR = Color.WHITE;
    private static final Font ARIAL = new Font("Arial", Font.BOLD, 16);
    private static final int PADDING = 20;

    private final HistogramView histogram = new HistogramView();
    private final ui.symbolCanvas.SymbolCanvas symbolCanvas = new ui.symbolCanvas.SymbolCanvas();
    private final JLabel predictionLabel = new JLabel("I will write my prediction here!");

    private final PredictingModel model;

    public PredictingPanel(PredictingModel model) {
        this.model = model;
        this.model.setListener(this);

        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND_COLOR);

        add(histogram, BorderLayout.NORTH);
        add(symbolCanvas, BorderLayout.CENTER);
        add(predictionLabel, BorderLayout.SOUTH);

        initSymbolCanvas();
        initPredictionLabel();
    }

    private void initSymbolCanvas() {
        symbolCanvas.setDrawingEnabled(false);
        symbolCanvas.addSymbolUpdateListener(model);
        symbolCanvas.addSymbolFinishListener(model);

        symbolCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!symbolCanvas.isDrawingEnabled()) {
                    predictionLabel.setText("I have not been trained yet...");
                }
            }
        });
    }

    private void initPredictionLabel() {
        predictionLabel.setFont(ARIAL);
        predictionLabel.setHorizontalAlignment(JLabel.CENTER);
        predictionLabel.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
        predictionLabel.setForeground(PANEL_TEXT_COLOR);
    }

    @Override
    public void onNextState(PredictingState state) {
        if (state instanceof PredictingState.Histogram) {
            renderHistogram((PredictingState.Histogram) state);
        }
        else if (state instanceof PredictingState.SymbolCanvas) {
            renderCanvas((PredictingState.SymbolCanvas) state);
        }
        else if (state instanceof PredictingState.Message) {
            renderMessage((PredictingState.Message) state);
        }
    }

    private void renderHistogram(PredictingState.Histogram state) {
        histogram.setData(state.identifiers, state.prediction);
    }

    private void renderCanvas(PredictingState.SymbolCanvas state) {
        symbolCanvas.setNumberOfRepresentativePoints(state.numberOfRepresentativePoints);
        symbolCanvas.setShowRepresentativePoints(state.showRepresentativePoints);
        symbolCanvas.setDrawingEnabled(state.isDrawingEnabled);
    }

    private void renderMessage(PredictingState.Message state) {
        predictionLabel.setText(state.message);
    }
}
