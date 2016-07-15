package com.getting.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.prefs.Preferences;


public class PathRecord {

    private static final String LAST_VISIT_DIRECTORY = "last_visit_directory";

    public void set(@NotNull File directory) {
        Preferences.userNodeForPackage(referClass).put(LAST_VISIT_DIRECTORY, directory.getAbsolutePath());
        path.set(directory);
    }

    private ObjectProperty<File> path = new SimpleObjectProperty<>();

    public PathRecord(@NotNull Class referClass) {
        this.referClass = referClass;
        path.set(new File(Preferences.userNodeForPackage(referClass).get(LAST_VISIT_DIRECTORY, System.getProperty("java.io.tmpdir"))));
    }

    private final Class referClass;

    public File getPath() {
        return path.get();
    }

    public ObjectProperty<File> pathProperty() {
        return path;
    }
}
