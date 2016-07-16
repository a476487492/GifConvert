package com.getting.util;

import java.io.File;

public class FileUtil {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final int GB = 1024 * MB;

    public static String getFileSizeDescription(File file) {
        if (file.length() < MB) {
            return String.format("%.2f KB", 1.0 * file.length() / KB);
        } else if (file.length() < GB) {
            return String.format("%.2f MB", 1.0 * file.length() / MB);
        } else {
            return String.format("%.2f GB", 1.0 * file.length() / GB);
        }
    }

    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf("."), name.length());
    }

    public static File ensureFileNotExist(File file) {
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

}
