package com.getting.util.executor;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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
