package com.getting.util.executor;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public abstract class Parameters {

    private boolean hasDone;

    @NotNull
    public abstract List<String> build();

    @Nullable
    public abstract File getOutputDirectory();

    public boolean hasDone() {
        return hasDone;
    }

    public void setHasDone() {
        hasDone = true;
    }

}
