package com.getting.util.executor;

import com.getting.util.FileUtil;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    protected final StringProperty executorOutputMessage = new SimpleStringProperty();
    private final Class loaderClass;
    private final String executorName;
    private final File executorFile;
    private Process executor;
    private boolean normalExecute;

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

            LOGGER.info(executorName + " has copied to " + executorFile);
        } catch (IOException e) {
            LOGGER.error("copyExecutorToTempDirectory", e);
        }
    }

    private void ensureExecutorAvailable() {
        if (executorFile.exists() && executorFile.isFile()) {
            LOGGER.info(executorFile + " exists");
            return;
        }

        copyExecutorToTempDirectory();
    }

    @Nullable
    protected ExecuteResult execute(@NotNull Parameters parameters, boolean needMessages) {
        ensureExecutorAvailable();
        FileUtil.ensureDirectoryAvailable(parameters.getOutputDirectory());

        // cannot execute two process together
        assert executor != null;

        LOGGER.info("execute()");

        try {
            ExecuteResult result = new ExecuteResult();
            List<String> command = new ArrayList<>();
            command.add(executorFile.getAbsolutePath());
            command.addAll(parameters.build());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            executor = processBuilder.start();
            normalExecute = true;

            List<String> messages = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(executor.getInputStream()));
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }

                LOGGER.info(message);

                executorOutputMessage.set(message);
                if (needMessages) {
                    messages.add(message);
                }
            }

            result.setStatus(normalExecute ? (executor.waitFor() == 0 ? ExecuteResult.Status.SUCCESS : ExecuteResult.Status.FAIL) : ExecuteResult.Status.CANCELED);
            result.setMessages(messages);
            return result;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("execute", e);
        } finally {
            executor = null;
            parameters.setHasDone();
        }

        return null;
    }

    public void cancel() {
        LOGGER.info("cancel()");
        if (executor == null) {
            return;
        }

        normalExecute = false;
        executor.destroy();
    }

    /**
     * Another way to destroy the process
     */
    public void forceCancel() {
        LOGGER.info("forceCancel()");
        if (executor == null) {
            return;
        }

        normalExecute = false;
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

                LOGGER.info(message);
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            LOGGER.error("forceCancel", e);
        }
    }

}
