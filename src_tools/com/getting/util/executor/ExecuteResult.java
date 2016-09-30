package com.getting.util.executor;

import com.sun.istack.internal.NotNull;

import java.util.List;

public class ExecuteResult {

    private final long costTime;

    private final Status status;

    private final List<String> messages;

    public ExecuteResult(@NotNull Status status, long costTime, @NotNull List<String> messages) {
        this.status = status;
        this.costTime = costTime;
        this.messages = messages;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    @NotNull
    public List<String> getMessages() {
        return messages;
    }

    public long getCostTime() {
        return costTime;
    }

    public enum Status {
        SUCCESS, CANCELED, FAIL
    }

}
