package com.nikyoff.thewalls.utils;

import java.io.File;

public class FileHelper {
    public static void CreateDir(String path, boolean replace) {
        File file = new File(path);
        boolean fileExists = file.exists();

        if (fileExists && !file.isDirectory() && replace) {
            file.delete();
            file.mkdirs();
        } else if (!fileExists) {
            file.mkdirs();
        }
    }
}
