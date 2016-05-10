package executor;

import com.sun.istack.internal.NotNull;
import debug.Debug;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Executor {

    private final Class loaderClass;

    private final String executorName;

    private final File executorFile;

    private Process executor;

    private boolean isCanceled;

    public Executor(@NotNull Class loaderClass, @NotNull String executorName) {
        this.loaderClass = loaderClass;
        this.executorName = executorName;
        executorFile = new File(System.getProperty("java.io.tmpdir"), executorName);
    }

    private void copyExecutorToTempDirectory() {
        try (OutputStream outputStream = new FileOutputStream(executorFile);
             InputStream inputStream = loaderClass.getResourceAsStream(executorName)) {
            byte[] buffer = new byte[4 * 1024];
            while (true) {
                int readCount = inputStream.read(buffer);
                if (readCount == -1) {
                    break;
                }

                outputStream.write(buffer, 0, readCount);
            }

            if (Debug.LOG) {
                System.out.println("executor has copied to temp directory");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureExecutorAvailable() {
        if (executorFile.exists()) {
            if (Debug.LOG) {
                System.out.println("executor exists");
            }
            return;
        }

        copyExecutorToTempDirectory();
    }

    protected final StringProperty status = new SimpleStringProperty();

    @NotNull
    protected ExecuteResult execute(@NotNull Parameters parameters, boolean needMessages) {
        ensureExecutorAvailable();

        if (executor != null) {
            throw new RuntimeException("cannot execute two process together");
        }

        final long startTime = System.currentTimeMillis();
        try {
            List<String> command = new ArrayList<>();
            command.add(executorFile.getAbsolutePath());
            command.addAll(parameters.build());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            executor = processBuilder.start();
            isCanceled = false;

            List<String> messages = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(executor.getInputStream()));
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }

                if (Debug.LOG) {
                    System.out.println(message);
                }

                status.set(message);
                if (needMessages) {
                    messages.add(message);
                }
            }

            return new ExecuteResult(executor.waitFor() == 0, isCanceled, System.currentTimeMillis() - startTime, messages);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor = null;
        }

        return null;
    }

    public void cancel() {
        if (executor == null) {
            return;
        }

        isCanceled = true;
        executor.destroy();
    }

    /**
     * Another way to destroy the process, run on a new thread
     */
    public void forceCancel() {
        if (executor == null) {
            return;
        }

        isCanceled = true;
        new Thread() {

            public void run() {
                ArrayList<String> command = new ArrayList<>();
                command.add("taskkill");
                command.add("/F");
                command.add("/IM");
                command.add(executorName);
                command.add("/T");
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);

                try {
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while (true) {
                        String message = reader.readLine();
                        if (message == null) {
                            break;
                        }

                        if (Debug.LOG) {
                            System.out.println(message);
                        }
                    }
                    process.waitFor();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

}
