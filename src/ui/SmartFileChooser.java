package ui;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class SmartFileChooser {

    private final FileChooser fileChooser = new FileChooser();

    public SmartFileChooser(Class saveClass) {
        this.saveClass = saveClass;
    }

    public File showOpenDialog(final Window ownerWindow) {
        fileChooser.setInitialDirectory(DirectoryRecord.get(saveClass));
        File file = fileChooser.showOpenDialog(ownerWindow);

        if (file != null) {
            DirectoryRecord.set(saveClass, file.getParentFile());
        }

        return file;
    }

    private final Class saveClass;

    public void addExtensionFilters(FileChooser.ExtensionFilter extensionFilter) {
        fileChooser.getExtensionFilters().add(extensionFilter);
    }

}
