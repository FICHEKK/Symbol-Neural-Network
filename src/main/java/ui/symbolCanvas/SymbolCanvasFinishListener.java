package ui.symbolCanvas;

import structures.Point;

import java.util.List;

@FunctionalInterface
public interface SymbolCanvasFinishListener {
    void onNextSymbolFinish(List<List<Point>> partedCurve);
}
