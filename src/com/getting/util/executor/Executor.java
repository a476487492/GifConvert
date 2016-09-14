package com.getting.util.executor;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Executor {

    protected final StringProperty executorOutputMessage = new SimpleStringProperty();
    private final Class loaderClass;
    private final String executorName;
    private final File executorFile;
    private final DoubleProperty executeProgress = new SimpleDoubleProperty(Double.NaN);
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

            System.out.println(executorName + " has copied to " + executorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureExecutorAvailable() {
        if (executorFile.exists() && executorFile.isFile()) {
            System.out.println(executorFile + " exists");
            return;
        }

        copyExecutorToTempDirectory();
    }

    private void ensureOutputDirectoryAvailable(File outputDirectory) {
        if (outputDirectory == null) {
            return;
        }

        if (outputDirectory.exists() && outputDirectory.isDirectory()) {
            System.out.println(outputDirectory + " exists");
            return;
        }

        boolean mkdirsSuccess = outputDirectory.mkdirs();
        System.out.println(outputDirectory + " mkdirs " + mkdirsSuccess);
    }

    @Nullable
    protected ExecuteResult execute(@NotNull Parameters parameters, boolean needMessages) {
        ensureExecutorAvailable();
        ensureOutputDirectoryAvailable(parameters.getOutputDirectory());

        // cannot execute two process together
        assert executor != null;

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

                System.out.println(message);

                executorOutputMessage.set(message);
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

    public DoubleProperty executeProgressProperty() {
        return executeProgress;
    }

    /**
     * Another way to destroy the process, run on a new thread
     */
    public void forceCancel() {
        if (executor == null) {
            return;
        }

        isCanceled = true;
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

                System.out.println(message);
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}
