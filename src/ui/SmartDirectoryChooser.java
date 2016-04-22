package ui;

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
        }
        return directory;
    }

}
