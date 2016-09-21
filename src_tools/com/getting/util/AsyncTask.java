package com.getting.util;

import javafx.application.Platform;

public abstract class AsyncTask<R> extends Task {

    public AsyncTask(Object id, long delay) {
        super(id, delay);
    }

    public void preTaskOnUi() {
    }

    public abstract R runTask();

    public void postTaskOnUi(R result) {
    }

    @Override
    public final void run() {
        Platform.runLater(this::preTaskOnUi);

        R result = runTask();

        Platform.runLater(() -> postTaskOnUi(result));
    }

}
