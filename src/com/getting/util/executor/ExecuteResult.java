package com.getting.util.executor;

import java.util.List;

public class ExecuteResult {

    protected final long costTime;

    private final boolean success;

    private final boolean canceled;

    private final List<String> messages;

    public ExecuteResult(boolean success, boolean canceled, long costTime, List<String> messages) {
        this.success = success;
        this.canceled = canceled;
        this.costTime = costTime;
        this.messages = messages;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }

    public long getCostTime() {
        return costTime;
    }

}