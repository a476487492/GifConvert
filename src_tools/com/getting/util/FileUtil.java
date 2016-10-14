package com.getting.util;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class FileUtil {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int GB = 1024 * MB;

    public static String formatFileSize(@NotNull File file) {
        if (file.length() < MB) {
            return String.format("%.2f KB", 1.0 * file.length() / KB);
        } else if (file.length() < GB) {
            return String.format("%.2f MB", 1.0 * file.length() / MB);
        } else {
            return String.format("%.2f GB", 1.0 * file.length() / GB);
        }
    }

    public static String getFileNameWithoutExtension(@NotNull File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getFileExtension(@NotNull File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf("."), name.length());
    }

    public static File ensureFileExist(@NotNull File file) {
        File correctFile = file;
        int suffix = 1;
        while (true) {
            if (!correctFile.exists()) {
                return correctFile;
            } else {
                correctFile = new File(file.getParentFile(), getFileNameWithoutExtension(file) + "_" + suffix + getFileExtension(file));
                suffix++;
            }
        }
    }

    public static void openFileDirectory(@NotNull File file) {
        try {
            Runtime.getRuntime().exec("explorer /select,\"" + file.getAbsolutePath() + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCostTimeDescription(long time) {
        return NumberFormat.getNumberInstance().format(time / 1000.0) + " 秒";
    }

}
