package com.github.athieriot.jtaches;

import java.io.IOException;
import java.nio.file.*;
import java.security.InvalidParameterException;
import java.util.List;

import static com.esotericsoftware.minlog.Log.info;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class Guardian {

    private WatchService watchService;
    private WatchKey globalWatchKey;

    private List<Tache> taches = newArrayList();

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

    public WatchKey registerTache(Tache tache) {
        if(tache != null) {
            if(!isTacheValid(tache)) {
                throw new InvalidParameterException("Tache not valid: " + tacheToString(tache));
            }

            try {
                return addTache(tache);
            } catch (IOException e) {
                throw new InvalidParameterException("An error occured when register the tache " + tacheToString(tache) + ": " + getRootCauseMessage(e));
            }
        }

        return null;
    }
    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }
    private String tacheToString(Tache tache) {
        return tache.getClass().getSimpleName() + " watching on directory: " + tache.getPath();
    }

    private WatchKey addTache(Tache tache) throws IOException {
        WatchKey key = tache.getPath().register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, OVERFLOW);
        this.taches.add(tache);

        info("Register tache: " + tacheToString(tache));
        return key;
    }

    public void watch() throws IOException, InterruptedException {
        if(taches.isEmpty()) {
            info("No task registered.");
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
        for(Tache tache : taches) {
            dispatchEvents(event, tache);
        }
    }

    private void dispatchEvents(WatchEvent<?> event, Tache tache) {
        if (event.kind() == OVERFLOW) {
            dealWithOverFlow(event);
        }
        if (event.kind() == ENTRY_CREATE) {
            tache.onCreate(event);
        }
        if (event.kind() == ENTRY_DELETE) {
            tache.onDelete(event);
        }
        if (event.kind() == ENTRY_MODIFY) {
            tache.onModify(event);
        }
    }
    void dealWithOverFlow(WatchEvent<?> event) {
        info("Overflow detected. You may have lost one or more event calls.");
    }

    private void onCancel() throws IOException {
        info("Watcher no longer valid. Closing.");
        globalWatchKey.cancel();
        cancel();
    }
}
