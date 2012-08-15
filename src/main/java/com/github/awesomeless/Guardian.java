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

            WatchKey key = tache.getPath().register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            this.tache = tache;
            System.out.println("Register: " + tache.toString());
            return key;
        }

        return null;
    }

    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }

    public void watch() {
        if(tache == null) {
            System.out.println("No task registered.");
        } else {
            try {
                globalWatchKey = watchService.take();

                while (true) {
                    for (final WatchEvent<?> event : globalWatchKey.pollEvents()) {
                        onEvent(event);
                    }

                    if (!globalWatchKey.reset()) {
                        System.out.println("Watcher no longer valid. Closing.");
                        cancel();
                        watchService.close();
                        break;
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
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

    public void cancel() {
        globalWatchKey.cancel();
    }
}
