package ui;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class SmartFileChooser {

    private final FileChooser fileChooser = new FileChooser();

    public File showOpenDialog(final Window ownerWindow) {
        fileChooser.setInitialDirectory(DirectoryRecord.get(getClass()));
        File file = fileChooser.showOpenDialog(ownerWindow);

        if (file != null) {
            DirectoryRecord.set(getClass(), file.getParentFile());
        }

        return file;
    }

    public void addExtensionFilters(FileChooser.ExtensionFilter extensionFilter) {
        fileChooser.getExtensionFilters().add(extensionFilter);
    }

}
