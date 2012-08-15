package com.github.awesomeless;

import java.io.IOException;
import java.nio.file.*;
import java.security.InvalidParameterException;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Guardian {

    private WatchService watchService;
    private WatchKey globalWatchKey;

    private Tache tache;

    public Guardian() throws IOException {
        FileSystem fileSystem = FileSystems.getDefault();
        watchService = fileSystem.newWatchService();
    }
    public static Guardian create() {
        try {
            return new Guardian();
        } catch (IOException e) {
            return null;
        }
    }

    public WatchKey registerTache(Tache tache) throws IOException {
        if(tache != null) {
            if(!isTacheValid(tache)) throw new InvalidParameterException("Tache not valid: " + tache);

            return addTache(tache);
        }

        return null;
    }

    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }
    private WatchKey addTache(Tache tache) throws IOException {
        WatchKey key = tache.getPath().register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        this.tache = tache;

        System.out.println("Register: " + tache.toString());
        return key;
    }

    public void watch() {
        if(tache == null) {
            System.out.println("No task registered.");
        } else {
            waitingForEvents();
        }
    }
    public void cancel() {
        globalWatchKey.cancel();
    }

    private void waitingForEvents() {
        try {
            globalWatchKey = watchService.take();

            doBlockingLoop();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    private void doBlockingLoop() throws IOException {
        while (true) {
            for (final WatchEvent<?> event : globalWatchKey.pollEvents()) {
                onEvent(event);
            }

            if (!globalWatchKey.reset()) {
                onCancel();
                break;
            }
        }
    }

    void onEvent(WatchEvent<?> event) {
        if (event.kind() == ENTRY_CREATE) {
            System.out.println("Created: " + event.context().toString());
            tache.onCreate(event);
        }
        if (event.kind() == ENTRY_DELETE) {
            System.out.println("Delete: " + event.context().toString());
            tache.onDelete(event);
        }
        if (event.kind() == ENTRY_MODIFY) {
            System.out.println("Modify: " + event.context().toString());
            tache.onModify(event);
        }
    }

    private void onCancel() throws IOException {
        System.out.println("Watcher no longer valid. Closing.");
        cancel();
        watchService.close();
    }
}
