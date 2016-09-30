package com.getting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Looper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Looper.class);

    private final Object lock = new Object();

    private final List<Task> tasks = new ArrayList<>();

    private Task currentTask;

    public Looper() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    synchronized (lock) {
                        if (tasks.isEmpty()) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                LOGGER.error("run", e);
                            }
                            continue;
                        }

                        final long waitTime = tasks.get(0).getTimeRunAt() - System.currentTimeMillis();
                        if (waitTime > 0) {
                            try {
                                lock.wait(waitTime);
                            } catch (InterruptedException e) {
                                LOGGER.error("run", e);
                            }
                            continue;
                        }

                        currentTask = tasks.remove(0);
                    }

                    LOGGER.info(currentTask + " run ");
                    currentTask.run();
                    currentTask = null;
                }
            }

        };
        thread.setDaemon(true);
        thread.start();
    }


    public void postTask(Task task) {
        LOGGER.info("postTask: " + task);
        synchronized (lock) {
            tasks.add(task);
            Collections.sort(tasks);
            lock.notifyAll();
        }
    }

    public void removeTask(Object id) {
        LOGGER.info("removeTask: " + id);
        synchronized (lock) {
            Iterator<Task> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getId() == id) {
                    iterator.remove();
                }
            }

            lock.notifyAll();
        }

        if (currentTask != null && currentTask.getId() == id) {
            currentTask.cancel();
        }
    }

    public void removeAllTasks() {
        synchronized (lock) {
            tasks.clear();
            lock.notifyAll();
        }

        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public boolean isAllDone() {
        synchronized (lock) {
            return tasks.isEmpty() && currentTask == null;
        }
    }

}
