package ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.prefs.Preferences;


class DirectoryRecord {

    private static final String LAST_VISIT_DIRECTORY = "last_visit_directory";

    @Nullable
    static File get(@NotNull Class saveClass) {
        String path = Preferences.userNodeForPackage(saveClass).get(LAST_VISIT_DIRECTORY, "");
        if ("".equals(path)) {
            return null;
        }

        return new File(path);
    }

    static void set(@NotNull Class saveClass, @NotNull File directory) {
        Preferences.userNodeForPackage(saveClass).put(LAST_VISIT_DIRECTORY, directory.getAbsolutePath());
    }

    private DirectoryRecord() {
    }

}
