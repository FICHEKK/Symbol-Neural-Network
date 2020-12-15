package ui;

import structures.Point;

import java.util.List;

@FunctionalInterface
public interface SymbolCanvasUpdateListener {
    void onNextSymbolUpdate(List<Point> normalizedPoints);
}
