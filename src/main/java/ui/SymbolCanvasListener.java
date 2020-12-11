package ui;

import structures.Point;

import java.util.List;

@FunctionalInterface
public interface SymbolCanvasListener {
    void onNextSymbol(List<Point> points);
}
