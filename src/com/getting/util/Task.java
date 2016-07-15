package com.getting.util;

import com.sun.istack.internal.NotNull;

public abstract class Task implements Comparable<Task> {

    private final Long timeRunAt;
    private final Object id;

    public Task(Object id, long delay) {
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
    }

    public abstract void run();

    public void cancel() {
    }

    public long getTimeRunAt() {
        return timeRunAt;
    }

    public Object getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull Task o) {
        return timeRunAt.compareTo(o.timeRunAt);
    }

}
