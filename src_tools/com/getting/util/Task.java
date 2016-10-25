package com.getting.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract class Task implements Comparable<Task>, Runnable {

    private final Long timeRunAt;
    private final Object id;

    public Task(@Nullable Object id, long delay) {
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
    }

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

    @Override
    public String toString() {
        return super.toString() + "{" + String.valueOf(id) + "}";
    }

}
