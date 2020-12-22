package ui;

import settings.Settings;
import structures.Point;
import ui.symbolCanvas.SymbolCanvasFinishListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolFileWriter implements SymbolCanvasFinishListener {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private static final String SYMBOL_FILE_EXTENSION = ".txt";

    private final Settings settings;

    public SymbolFileWriter(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onNextSymbolFinish(List<Point> normalizedPoints) {
        var directoryPath = Paths.get(
                settings.getStringProperty(Settings.SYMBOL_SAVE_DIRECTORY),
                settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS),
                settings.getStringProperty(Settings.SYMBOL_IDENTIFIER)
        );

        var fileName = FORMAT.format(new Date(System.currentTimeMillis()));
        var filePath = directoryPath.resolve(fileName + SYMBOL_FILE_EXTENSION);

        try {
            Files.createDirectories(directoryPath);
            Files.createFile(filePath);
            Files.write(
                    filePath,
                    normalizedPoints.stream()
                            .map(point -> point.x + System.lineSeparator() + point.y)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
