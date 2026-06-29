package com.zephy.zls.javamodupdater;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class JavaModUpdater {
    public static boolean isUpdaterExtracted() {
        return new File("JavaModUpdater.jar").exists();
    }

    public static void tryExtractUpdater() throws IOException {
        if (isUpdaterExtracted()) {
            return;
        }

        var file = new File("JavaModUpdater.jar");
        try (var from = JavaModUpdater.class.getResourceAsStream("/JavaModUpdater.jar");
            var to = new FileOutputStream(file)) {
            connect(from, to);
        }
        ExitHookInvoker.setUpdaterJar(file);
    }

    public static synchronized void scheduleDelete(String filePath) throws IOException {
        tryExtractUpdater();
        registerDeleteFileHook(new File(filePath));
    }
    public static synchronized void scheduleMove(String oldFilePath, String newFilePath) throws IOException {
        tryExtractUpdater();
        registerMoveFileHook(new File(oldFilePath), new File(newFilePath));
    }

    private static void registerDeleteFileHook(File file) {
        ExitHookInvoker.setUpdaterJar(new File("JavaModUpdater.jar"));
        ExitHookInvoker.setExitHook(DeleteJarUpdateActions(file));
    }
    private static void registerMoveFileHook(File oldFile, File newFile) {
        ExitHookInvoker.setUpdaterJar(new File("JavaModUpdater.jar"));
        ExitHookInvoker.setExitHook(MoveJarUpdateActions(oldFile, newFile));
    }

    public static List<UpdateAction> DeleteJarUpdateActions(File file) {
        return Arrays.asList(
            new UpdateAction.DeleteFile(file)
        );
    }
    public static List<UpdateAction> MoveJarUpdateActions(File oldFile, File newFile) {
        return Arrays.asList(
            new UpdateAction.MoveDownloadedFile(oldFile, newFile)
        );
    }

    public static void connect(InputStream from, OutputStream to) throws IOException {
        var buf = new byte[4096];
        int r;
        while ((r = from.read(buf)) != -1) {
            to.write(buf, 0, r);
        }
    }
}
