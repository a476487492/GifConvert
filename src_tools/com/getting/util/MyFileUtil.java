package com.getting.util;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class MyFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFileUtil.class);

    private static final double KB = 1024;
    private static final double MB = 1024 * KB;
    private static final double GB = 1024 * MB;

    @NotNull
    public static String formatFileSize(@NotNull File file) {
        if (file.length() < MB) {
            return String.format("%.2f KB", file.length() / KB);
        } else if (file.length() < GB) {
            return String.format("%.2f MB", file.length() / MB);
        } else {
            return String.format("%.2f GB", file.length() / GB);
        }
    }

    @NotNull
    public static File ensureFileNameAvailable(@NotNull File file) {
        final String baseName = FilenameUtils.getBaseName(file.getAbsolutePath());
        final String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        File correctFile = file;
        int suffix = 1;
        while (true) {
            if (correctFile.exists()) {
                correctFile = new File(file.getParentFile(), baseName + "_" + suffix + "." + extension);
                suffix++;
            } else {
                return correctFile;
            }
        }
    }

    public static void openFileDirectory(@NotNull File file) {
        try {
            Runtime.getRuntime().exec("explorer /select,\"" + file.getAbsolutePath() + "\"");
        } catch (IOException e) {
            LOGGER.error("openFileDirectory", e);
        }
    }

    public static void ensureDirectoryAvailable(@NotNull File outputDirectory) {
        if (outputDirectory.exists() && outputDirectory.isDirectory()) {
            LOGGER.info(outputDirectory + " exists");
            return;
        }

        boolean mkdirsSuccess = outputDirectory.mkdirs();
        LOGGER.info(outputDirectory + " mkdirs " + mkdirsSuccess);
    }

}
