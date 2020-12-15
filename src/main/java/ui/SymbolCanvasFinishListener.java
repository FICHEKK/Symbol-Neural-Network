package ui;

import structures.Point;

import java.util.List;

@FunctionalInterface
public interface SymbolCanvasFinishListener {
    void onNextSymbolFinish(List<Point> normalizedPoints);
}
