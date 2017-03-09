package com.getting.util;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Looper {

    private boolean continueRun = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(Looper.class);

    private final Object lock = new Object();

    private final List<Task> tasks = new ArrayList<>();

    @Nullable
    private Task currentTask;

    public Looper(String name) {
        Thread thread = new Thread(() -> {
            LOGGER.info(this + " start run");
            while (continueRun) {
                synchronized (lock) {
                    if (tasks.isEmpty()) {
                        try {
                            LOGGER.info(this + " is empty, wait");
                            lock.wait();
                        } catch (InterruptedException e) {
                            LOGGER.error("run", e);
                        }
                        continue;
                    }

                    final long waitTime = tasks.get(0).getTimeRunAt() - System.currentTimeMillis();
                    if (waitTime > 0) {
                        try {
                            LOGGER.info(this + " is need to wait " + waitTime + "ms");
                            lock.wait(waitTime);
                        } catch (InterruptedException e) {
                            LOGGER.error("run", e);
                        }
                        continue;
                    }

                    currentTask = tasks.remove(0);
                }

                LOGGER.info(this + " run " + currentTask);
                try {
                    currentTask.run();
                } catch (Throwable e) {
                    LOGGER.error("run", e);
                }

                synchronized (lock) {
                    currentTask = null;
                }
            }

            LOGGER.info(this + " exit");
        });
        thread.setName("Thread-" + name);
        thread.start();
    }

    public void quit() {
        LOGGER.info(this + " quit");
        synchronized (lock) {
            continueRun = false;
            lock.notifyAll();
        }
    }

    public void postTask(Task task) {
        LOGGER.info(this + " postTask " + task);
        synchronized (lock) {
            tasks.add(task);
            Collections.sort(tasks);
            lock.notifyAll();
        }
    }

    public void removeTask(Object id) {
        LOGGER.info(this + " removeTask " + id);
        synchronized (lock) {
            tasks.removeIf(task -> task.getId() == id);
            lock.notifyAll();
        }

        if (currentTask != null && currentTask.getId() == id) {
            currentTask.cancel();
        }
    }

    public void removeAllTasks() {
        LOGGER.info(this + " removeAllTasks");
        synchronized (lock) {
            tasks.clear();
            lock.notifyAll();
        }

        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public void removePendingTasks() {
        LOGGER.info(this + " removePendingTasks");
        synchronized (lock) {
            tasks.clear();
            lock.notifyAll();
        }
    }

    public boolean isAllDone() {
        synchronized (lock) {
            return tasks.isEmpty() && currentTask == null;
        }
    }

}
