package com.github.athieriot.jtaches;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.info;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class Guardian {

    private WatchService watchService;

    /**
     * Corresponding Map between subdirectory registration and relative path.
     * Used internally by the Guardian to make recursive watching working
     */
    //TODO: Exploded this system in other classes maybe
    private Map<WatchKey, Path> globalWatchKeys = newHashMap();

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

    public void registerTache(Tache tache) throws IOException {
        //TODO: Think about this default choice
        //TODO: Filter .files
        //TODO: Add more tests about recursive watching
        registerTache(tache, false);
    }
    public void registerTache(Tache tache, boolean recursive) {
        if(tache != null) {
            if(!isTacheValid(tache)) {
                throw new InvalidParameterException("Tache not valid: " + tacheToString(tache));
            }

            try {
                addTache(tache, recursive);
            } catch (IOException e) {
                throw new InvalidParameterException("An error occured when register the tache " + tacheToString(tache) + ": " + getRootCauseMessage(e));
            }
        }
    }
    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }
    private String tacheToString(Tache tache) {
        return tache.getClass().getSimpleName() + " watching on directory: " + tache.getPath();
    }

    private void addTache(Tache tache, boolean recursive) throws IOException {
        if(recursive) {
            registerDirectories(tache.getPath());
        } else {
            registerDirectory(tache.getPath());
        }

        this.taches.add(tache);
        info("Register tache: " + tacheToString(tache));
    }

    private void registerDirectories(final Path globalPath) throws IOException {
        //TODO: Valider From/To de CopyTache
        SimpleFileVisitor<Path> directoryRegister = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attr) throws IOException {
                registerDirectory(directory, globalPath);

                return FileVisitResult.CONTINUE;
            }
        };
        walkFileTree(globalPath, directoryRegister);
    }
    private void registerDirectory(Path path) throws IOException {
        registerDirectory(path, path);
    }
    private void registerDirectory(Path path, Path globalPath) throws IOException {
        WatchKey key = path.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        globalWatchKeys.put(key, globalPath.relativize(path));

        debug("Register path: " + path);
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
        while (true) {
            WatchKey localKey = watchService.take();

            for (final WatchEvent<?> event : localKey.pollEvents()) {
                dispatch(relativizedEvent(event, localKey));
            }

            if (!localKey.reset()) {
                onCancel(localKey);
                break;
            }
        }
    }

    //TODO: Move this in an utility class
    WatchEvent<?> relativizedEvent(WatchEvent<?> event, WatchKey key) {
        if(globalWatchKeys.containsKey(key)) {
            return decoratedEvent(event, globalWatchKeys.get(key));
        } else {
            info("No trace of this watch key: " + key.toString());
            return event;
        }
    }
    private WatchEvent<?> decoratedEvent(WatchEvent<?> event, Path relativePath) {
        try {
            Field context = event.getClass().getDeclaredField("context");
            context.setAccessible(true);
            context.set(event, get(relativePath.toString(), event.context().toString()));
            context.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            debug("Unable to decorate " + event.kind() + " event: " + event.context() + " with relative path: " + relativePath, e);
        }

        return event;
    }

    void dispatch(WatchEvent<?> event) {
        for(Tache tache : taches) {
            fire(event, tache);
        }
    }
    private void fire(WatchEvent<?> event, Tache tache) {
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

    private void onCancel(WatchKey key) throws IOException {
        info("Watcher no longer valid. Closing.");
        key.cancel();
        cancel();
    }
}
