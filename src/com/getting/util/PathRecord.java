package com.getting.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.prefs.Preferences;


public class PathRecord {

    private static final String LAST_VISIT_DIRECTORY = "last_visit_directory";

    @NotNull
    public File get() {
        String path = Preferences.userNodeForPackage(referClass).get(LAST_VISIT_DIRECTORY, System.getProperty("java.io.tmpdir"));
        return new File(path);
    }

    public void set(@NotNull File directory) {
        Preferences.userNodeForPackage(referClass).put(LAST_VISIT_DIRECTORY, directory.getAbsolutePath());
    }

    public PathRecord(@NotNull Class referClass) {
        this.referClass = referClass;
    }

    private final Class referClass;

}
