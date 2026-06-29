package com.zephy.zls.javamodupdater;

import java.io.File;
import java.util.List;

public abstract class UpdateAction {
    private UpdateAction() {  }

    public abstract void encode(List<String> arguments);

    public static class DeleteFile extends UpdateAction {
        File toDelete;

        @Override
        public void encode(List<String> arguments) {
            arguments.add("delete");
            arguments.add(toDelete.getAbsolutePath());
        }

        public DeleteFile(File toDelete) {
            this.toDelete = toDelete;
        }
    }

    public static class MoveDownloadedFile extends UpdateAction {
        File whereFrom;
        File whereTo;

        @Override
        public void encode(List<String> arguments) {
            arguments.add("move");
            arguments.add(whereFrom.getAbsolutePath());
            arguments.add(whereTo.getAbsolutePath());
        }

        public MoveDownloadedFile(File whereFrom, File whereTo) {
            this.whereFrom = whereFrom;
            this.whereTo = whereTo;
        }
    }
}
