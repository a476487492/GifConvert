package ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.prefs.Preferences;


class DirectoryRecord {

    private static final String LAST_VISIT_DIRECTORY = "last_visit_directory";

    @Nullable
    public static File get(Class saveClass) {
        Preferences preferences = Preferences.userNodeForPackage(saveClass);
        return new File(preferences.get(LAST_VISIT_DIRECTORY, ""));
    }

    public static void set(Class saveClass, @NotNull File directory) {
        Preferences preferences = Preferences.userNodeForPackage(saveClass);
        preferences.put(LAST_VISIT_DIRECTORY, directory.getAbsolutePath());
    }

    private DirectoryRecord() {
    }

}
