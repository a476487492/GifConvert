package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Looper {

    private final static ThreadLocal<Looper> LOOPER_THREAD_LOCAL = new ThreadLocal<>();

    private final Object lock = new Object();

    private final List<Task> tasks = new ArrayList<>();

    private Task currentTask;

    private Looper() {
    }

    public static void prepare() {
        if (LOOPER_THREAD_LOCAL.get() != null) {
            throw new IllegalStateException("can not call Looper.prepare() twice");
        }

        LOOPER_THREAD_LOCAL.set(new Looper());
        loop();
    }

    private static void loop() {
        final Looper looper = myLoop();
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    synchronized (looper.lock) {
                        if (looper.tasks.isEmpty()) {
                            try {
                                looper.lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        final long waitTime = looper.tasks.get(0).getTimeRunAt() - System.currentTimeMillis();
                        if (waitTime > 0) {
                            try {
                                looper.lock.wait(waitTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        looper.currentTask = looper.tasks.remove(0);
                    }

                    looper.currentTask.run();
                    looper.currentTask = null;
                }
            }

        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void postMessage(Task task) {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            looper.tasks.add(task);
            Collections.sort(looper.tasks);
            looper.lock.notifyAll();
        }
    }

    public static void removeTask(Object id) {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            Iterator<Task> iterator = looper.tasks.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getId() == id) {
                    iterator.remove();
                }
            }

            looper.lock.notifyAll();
        }

        if (looper.currentTask != null && looper.currentTask.getId() == id) {
            looper.currentTask.cancel();
        }
    }

    public static void removeAllTasks() {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            looper.tasks.clear();
            looper.lock.notifyAll();
        }

        if (looper.currentTask != null) {
            looper.currentTask.cancel();
        }
    }

    private static Looper myLoop() {
        if (LOOPER_THREAD_LOCAL.get() == null) {
            throw new IllegalStateException("call Looper.prepare() first");
        }

        return LOOPER_THREAD_LOCAL.get();
    }

}
