import settings.DataCollectingStageSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SymbolFileWriter implements SymbolCanvasListener {

    private final DataCollectingStageSettings settings;

    public SymbolFileWriter(DataCollectingStageSettings settings) {
        this.settings = settings;
    }

    @Override
    public void onNextSymbol(List<Point> points) {
        var directoryPath = Paths.get(
                settings.getSymbolSaveDirectory(),
                String.valueOf(points.size()),
                settings.getSymbolIdentifier()
        );

        var filePath = directoryPath.resolve(UUID.randomUUID().toString());

        try {
            Files.createDirectories(directoryPath);
            Files.createFile(filePath);
            Files.write(filePath, points.stream().map(point -> point.x + " " + point.y).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
