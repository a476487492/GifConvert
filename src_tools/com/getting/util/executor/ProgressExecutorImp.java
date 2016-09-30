package com.getting.util.executor;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ProgressExecutorImp extends Executor {

    private final DoubleProperty executeProgress = new SimpleDoubleProperty(Double.NaN);

    public ProgressExecutorImp(@NotNull Class loaderClass, @NotNull String executorName) {
        super(loaderClass, executorName);
    }

    public double getExecuteProgress() {
        return executeProgress.get();
    }

    public DoubleProperty executeProgressProperty() {
        return executeProgress;
    }

}
