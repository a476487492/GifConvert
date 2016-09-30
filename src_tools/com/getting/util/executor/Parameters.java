package com.getting.util.executor;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.util.List;

public interface Parameters {

    @NotNull
    List<String> build();

    @NotNull
    File getOutputDirectory();

    boolean hasDone();

    void setHasDone();

}
