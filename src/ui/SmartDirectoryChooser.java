package ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class SmartDirectoryChooser {

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    public File show(final Window ownerWindow) {
        directoryChooser.setInitialDirectory(DirectoryRecord.get(saveClass));
        File directory = directoryChooser.showDialog(ownerWindow);

        if (directory != null) {
            DirectoryRecord.set(saveClass, directory);
            lastDirectory.set(directory);
        }

        return directory;
    }

    private final Class saveClass;

    public SmartDirectoryChooser(Class saveClass) {
        this.saveClass = saveClass;
        lastDirectory.set(DirectoryRecord.get(saveClass));
    }

    public ObjectProperty<File> lastDirectoryProperty() {
        return lastDirectory;
    }

    private ObjectProperty<File> lastDirectory = new SimpleObjectProperty<>();

}
