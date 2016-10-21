package com.getting.util.executor;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExecuteResult {

    private long startTime;
    private long endTime;

    private Status status = Status.FAIL;

    private List<String> messages = new ArrayList<>();

    public ExecuteResult() {
        startTime = System.currentTimeMillis();
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
        endTime = System.currentTimeMillis();
    }

    public void setMessages(@NotNull List<String> messages) {
        this.messages = messages;
        endTime = System.currentTimeMillis();
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
        return endTime - startTime;
    }

    public enum Status {
        SUCCESS, CANCELED, FAIL
    }

}
