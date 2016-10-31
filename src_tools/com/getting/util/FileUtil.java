package com.getting.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int GB = 1024 * MB;

    @NotNull
    public static String formatFileSize(@NotNull File file) {
        if (file.length() < MB) {
            return String.format("%.2f KB", 1.0 * file.length() / KB);
        } else if (file.length() < GB) {
            return String.format("%.2f MB", 1.0 * file.length() / MB);
        } else {
            return String.format("%.2f GB", 1.0 * file.length() / GB);
        }
    }

    @NotNull
    public static String getFileNameWithoutExtension(@NotNull File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf("."));
    }

    /**
     * @param file
     * @return ex.: .mkv
     */
    @NotNull
    public static String getFileExtension(@NotNull File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf("."), name.length());
    }

    @NotNull
    public static File ensureFileNameAvailable(@NotNull File file) {
        final String name = getFileNameWithoutExtension(file);
        final String extension = getFileExtension(file);
        File correctFile = file;
        int suffix = 1;
        while (true) {
            if (correctFile.exists()) {
                correctFile = new File(file.getParentFile(), name + "_" + suffix + extension);
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
