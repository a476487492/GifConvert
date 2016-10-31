package com.getting.util;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Task implements Comparable<Task>, Runnable {

    @NotNull
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

    @NotNull
    @Override
    public String toString() {
        return super.toString() + "{" + String.valueOf(id) + "}";
    }

}
