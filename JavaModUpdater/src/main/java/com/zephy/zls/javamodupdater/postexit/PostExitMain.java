package com.zephy.zls.javamodupdater.postexit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PostExitMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        File outputFile = new File(".JavaModUpdater", "postexit.log");
        outputFile.getParentFile().mkdirs();
        PrintStream printStream = new PrintStream(new FileOutputStream(outputFile, true));
        System.setErr(printStream);
        System.setOut(printStream);
        for (int i = 0; i < args.length; i++) {
            switch (args[i].intern()) {
                case "delete":
                    File file = unlockedFile(args[++i]);
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                        break;
                    }
                    deleteFile(file);
                    break;
                case "move":
                    File from = unlockedFile(args[++i]);
                    File to = unlockedFile(args[++i]);
                    System.out.println("Moving " + from + " to " + to);
                    Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    break;
                default:
                    System.out.println("Unknown instruction " + args[i]);
                    System.exit(1);
            }
        }
    }

    public static File unlockedFile(String name) throws InterruptedException {
        File file = new File(name);
        while (file.exists() && !file.renameTo(file)) {
            System.out.println("Waiting on a process to relinquish access to " + file);
            Thread.sleep(1000L);
        }
        file.getParentFile().mkdirs();
        return file;
    }

    private static void deleteDirectory(File dir) throws InterruptedException {
        System.out.println("Trying to delete " + dir);
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Deleting " + file);
                    deleteDirectory(file);
                } else {
                    deleteFile(file);
                }
            }
        }
        System.out.println("Deleting directory " + dir);
        if (!dir.delete()) {
            System.out.println("Failed to delete directory " + dir);
        }
    }

    private static void deleteFile(File file) throws InterruptedException {
        File unlocked = unlockedFile(file.getPath());
        System.out.println("Deleting " + unlocked);
        if (!unlocked.delete()) {
            System.out.println("Failed to delete " + unlocked);
        }
    }
}
