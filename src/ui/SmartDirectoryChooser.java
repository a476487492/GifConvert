package ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class SmartDirectoryChooser {

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    public File show(final Window ownerWindow) {
        directoryChooser.setInitialDirectory(DirectoryRecord.get(getClass()));
        File directory = directoryChooser.showDialog(ownerWindow);
        if (directory != null) {
            DirectoryRecord.set(getClass(), directory);
            lastDirectory.set(directory);
        }
        return directory;
    }

    public SmartDirectoryChooser() {
        lastDirectory.set(DirectoryRecord.get(getClass()));
    }

    public ObjectProperty<File> lastDirectoryProperty() {
        return lastDirectory;
    }

    private ObjectProperty<File> lastDirectory = new SimpleObjectProperty<>();

}
