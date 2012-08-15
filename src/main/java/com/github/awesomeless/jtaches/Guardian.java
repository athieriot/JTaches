package com.github.awesomeless.jtaches;

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

    public WatchKey registerTache(Tache tache) throws InvalidParameterException {
        if(tache != null) {
            if(!isTacheValid(tache)) throw new InvalidParameterException("Tache not valid: " + tache);

            try {
                return addTache(tache);
            } catch (IOException e) {
                throw new InvalidParameterException("An error occured when register the tache: " + tache);
            }
        }

        return null;
    }

    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }
    private WatchKey addTache(Tache tache) throws IOException {
        WatchKey key = tache.getPath().register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        this.tache = tache;

        //TODO: Better message
        System.out.println("Register tache: " + tache.toString());
        return key;
    }

    public void watch() throws IOException, InterruptedException {
        if(tache == null) {
            System.out.println("No task registered.");
        } else {
            waitingForEvents();
        }
    }
    public void cancel() throws IOException {
        watchService.close();
    }

    private void waitingForEvents() throws IOException, InterruptedException {
        globalWatchKey = watchService.take();

        doBlockingLoop();
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
        globalWatchKey.cancel();
        cancel();
    }
}
