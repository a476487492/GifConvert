package com.getting.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.prefs.Preferences;


public class PathRecord {

    private final String key;
    @NotNull
    private final Class referClass;
    private final ObjectProperty<File> path = new SimpleObjectProperty<>();

    public PathRecord(@NotNull Class referClass, @NotNull String key) {
        this.referClass = referClass;
        this.key = key;
        path.set(new File(Preferences.userNodeForPackage(referClass).get(key, System.getProperty("java.io.tmpdir"))));
    }

    public void set(@NotNull File directory) {
        Preferences.userNodeForPackage(referClass).put(key, directory.getAbsolutePath());
        path.set(directory);
    }

    public File getPath() {
        return path.get();
    }

    @NotNull
    public ObjectProperty<File> pathProperty() {
        return path;
    }

}
