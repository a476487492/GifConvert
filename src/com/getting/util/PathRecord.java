package com.getting.util;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.prefs.Preferences;


public class PathRecord {

    private final String key;

    public void set(@NotNull File directory) {
        Preferences.userNodeForPackage(referClass).put(key, directory.getAbsolutePath());
        path.set(directory);
    }

    private ObjectProperty<File> path = new SimpleObjectProperty<>();

    public PathRecord(@NotNull Class referClass, @NotNull String key) {
        this.referClass = referClass;
        this.key = key;
        path.set(new File(Preferences.userNodeForPackage(referClass).get(key, System.getProperty("java.io.tmpdir"))));
    }

    private final Class referClass;

    public File getPath() {
        return path.get();
    }

    public ObjectProperty<File> pathProperty() {
        return path;
    }

}
