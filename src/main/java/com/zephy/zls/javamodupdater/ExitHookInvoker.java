package com.zephy.zls.javamodupdater;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExitHookInvoker {
    private static File updaterJar;
    private static HashSet<Integer> exitHookHashes = new HashSet<>();
    private static List<UpdateAction> actions = new ArrayList<>();

    public static synchronized void setUpdaterJar(File updaterJar) {
        ExitHookInvoker.updaterJar = updaterJar;
    }

    public static synchronized void setExitHook(List<UpdateAction> actions) {
        var hash = 1;
        for (var action : actions) {
            hash = 31 * hash + action.hashCode();
        }

        if (exitHookHashes.contains(hash)) {
            System.out.println("Exit hook with actions hash " + hash + " is already registered, skipping");
            return;
        }
        exitHookHashes.add(hash);

        System.out.println("Registering exit hook with actions hash: " + hash);
        Runtime.getRuntime().addShutdownHook(new Thread(ExitHookInvoker::runExitHook));
        ExitHookInvoker.actions.addAll(actions);
    }

    private static synchronized String[] buildInvocation() {
        boolean isWindows = System.getProperty("os.name", "").startsWith("Windows");
        File javaBinary = new File(System.getProperty("java.home"), "bin/java" + (isWindows ? ".exe" : ""));

        List<String> arguments = new ArrayList<>();
        arguments.add(javaBinary.getAbsolutePath());
        arguments.add("-jar");
        arguments.add(updaterJar.getAbsolutePath());

        for (UpdateAction action : actions) {
            action.encode(arguments);
        }

        return arguments.toArray(new String[0]);
    }

    private static void runExitHook() {
        try {
            String[] invocation = buildInvocation();
            System.out.println("Running post updater using: " + String.join(" ", invocation));
            Runtime.getRuntime().exec(invocation);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
