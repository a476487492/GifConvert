package com.getting.util.executor;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExecuteResult {

    private final long startTime;
    private long endTime;

    private Status status = Status.FAIL;

    private List<String> messages = new ArrayList<>();

    public ExecuteResult() {
        startTime = System.currentTimeMillis();
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
        endTime = System.currentTimeMillis();
    }

    @NotNull
    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(@NotNull List<String> messages) {
        this.messages = messages;
        endTime = System.currentTimeMillis();
    }

    public long getCostTime() {
        return endTime - startTime;
    }

    public enum Status {
        SUCCESS, CANCELED, FAIL
    }

}
