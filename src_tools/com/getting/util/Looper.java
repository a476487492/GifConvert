package com.getting.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Looper {

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
                                e.printStackTrace();
                            }
                            continue;
                        }

                        final long waitTime = tasks.get(0).getTimeRunAt() - System.currentTimeMillis();
                        if (waitTime > 0) {
                            try {
                                lock.wait(waitTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        currentTask = tasks.remove(0);
                    }

                    currentTask.run();
                    currentTask = null;
                }
            }

        };
        thread.setDaemon(true);
        thread.start();
    }


    public void postTask(Task task) {
        synchronized (lock) {
            tasks.add(task);
            Collections.sort(tasks);
            lock.notifyAll();
        }
    }

    public void removeTask(Object id) {
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
