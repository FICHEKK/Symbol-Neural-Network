package ui;

import settings.Settings;
import structures.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SymbolFileWriter implements SymbolCanvasListener {

    private final Settings settings;

    public SymbolFileWriter(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onNextSymbol(List<Point> points) {
        var directoryPath = Paths.get(
                settings.getStringProperty(Settings.SYMBOL_SAVE_DIRECTORY),
                String.valueOf(points.size()),
                settings.getStringProperty(Settings.SYMBOL_IDENTIFIER)
        );

        var filePath = directoryPath.resolve(UUID.randomUUID().toString());

        try {
            Files.createDirectories(directoryPath);
            Files.createFile(filePath);
            Files.write(
                    filePath,
                    points.stream()
                            .map(point -> point.x + System.lineSeparator() + point.y)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
