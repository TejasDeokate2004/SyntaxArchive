package com.logicarchive.util;

import java.util.Arrays;
import java.util.List;

public final class FileValidationUtil {

    private FileValidationUtil() {
    }

    public static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    public static final List<String> ALLOWED_CODE_EXTENSIONS = Arrays.asList(
            ".java", ".py", ".cpp", ".c", ".js", ".ts", ".go", ".kt"
    );

    public static final List<String> ALLOWED_RESOURCE_EXTENSIONS = Arrays.asList(
            ".pdf", ".doc", ".docx", ".ppt", ".pptx", ".txt",
            ".png", ".jpg", ".jpeg", ".zip"
    );

    public static boolean isValidCodeFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerName = fileName.toLowerCase();
        return ALLOWED_CODE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    public static boolean isValidResourceFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerName = fileName.toLowerCase();
        return ALLOWED_RESOURCE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    public static boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}
