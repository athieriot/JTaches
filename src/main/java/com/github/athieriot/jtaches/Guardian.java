package com.github.athieriot.jtaches;

import com.github.athieriot.jtaches.command.CommandArgs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;

import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.CommandArgs.DEFAULT_RECURSIVE;
import static com.github.athieriot.jtaches.utils.GuardianUtils.relativizedEvent;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class Guardian {

    private WatchService watchService;

    private CommandArgs commandArgs;

    //TODO: Too much stateful for my tastes
    private WatchingStore<Tache, Path> globalStorage = new WatchingStore<>();

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
        //TODO: Filter .files
        if(commandArgs == null) {
            registerTache(tache, DEFAULT_RECURSIVE);
        } else {
            registerTache(tache, commandArgs.isRecursive());
        }
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
    //TODO: Move this in an utility class
    private boolean isTacheValid(Tache tache) {
        return tache.getPath() != null;
    }
    private String tacheToString(Tache tache) {
        return tache.getClass().getSimpleName() + " watching on directory: " + tache.getPath();
    }

    private void addTache(Tache tache, boolean recursive) throws IOException {
        if(recursive) {
            registerTacheDirectories(tache);
        } else {
            registerTacheDirectory(tache);
        }

        info("Register tache: " + tacheToString(tache));
    }

    //TODO: Refactor
    private void registerTacheDirectories(final Tache tache) throws IOException {
        SimpleFileVisitor<Path> directoryRegister = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attr) throws IOException {
                registerDirectory(directory, tache.getPath(), tache);

                return FileVisitResult.CONTINUE;
            }
        };
        walkFileTree(tache.getPath(), directoryRegister);
    }
    private void registerTacheDirectory(Tache tache) throws IOException {
        registerDirectory(tache.getPath(), tache.getPath(), tache);
    }
    void registerDirectory(Path path, Path globalPath, Tache tache) throws IOException {
        WatchKey key = path.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        boolean rootPath = path.equals(globalPath);
        getGlobalStorage().store(tache, key, globalPath.relativize(path), rootPath);

        debug("Register path: " + path);
    }

    public void watch() throws IOException, InterruptedException {
        watch(null);
    }
    public void watch(Long timeout) throws IOException, InterruptedException {
        if(globalStorage.isEmpty()) {
            info("No task registered.");
        } else {
            waitingForEvents(timeout);
        }
    }

    private void waitingForEvents(Long timeout) throws IOException, InterruptedException {
        Long start = System.currentTimeMillis();

        while (timeout == null || !isTimeoutOverRun(start, timeout)) {
            try {
                WatchKey localKey = watchService.poll();

                if(localKey != null) {
                    for (final WatchEvent<?> event : localKey.pollEvents()) {
                        dispatchFilteredByTache(decoratedEvent(event, localKey), localKey);
                    }

                    if (!localKey.reset()) {
                        onCancel(localKey);
                    }
                }
            } catch(ClosedWatchServiceException cse) {
                break;
            }
        }
    }
    private boolean isTimeoutOverRun(Long start, Long timeout) {
        return (System.currentTimeMillis() - start) >= timeout;
    }
    WatchEvent<?> decoratedEvent(WatchEvent<?> event, WatchKey key) {
        if(getGlobalStorage().isWatched(key)) {
            return relativizedEvent(event, getGlobalStorage().retreiveMetadata(key));
        } else {
            info("No trace of this watch key: " + key.toString());
            return event;
        }
    }

    void dispatch(WatchEvent<?> event) throws IOException {
        dispatchFilteredByTache(event, null);
    }
    void dispatchFilteredByTache(WatchEvent<?> event, WatchKey localKey) throws IOException {
        for(Tache tache : getGlobalStorage().retrieveItems()) {
            if(localKey == null || getGlobalStorage().isWatchedByItem(tache, localKey)) {
                registerNewDirectory(event, tache);
                fire(event, tache);
            }
        }
    }

    private void registerNewDirectory(WatchEvent<?> event, Tache tache) throws IOException {
        if(event.kind() == ENTRY_CREATE && event.kind() != OVERFLOW) {
            Path directoryMaybe = get(tache.getPath().toString(), event.context().toString());

            if(isDirectory(directoryMaybe)
                    && (commandArgs == null || commandArgs.isRecursive())) {
                registerDirectory(directoryMaybe, tache.getPath(), tache);
            }
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

    public void cancel() throws IOException {
        info("Watcher is no longer valid. Closing.");
        watchService.close();
    }
    private void onCancel(WatchKey localKey) throws IOException {
        //FIXME: Remove task from the store until the last one
        if(getGlobalStorage().hasFlag(localKey)) {
            cancel();
        }

        info("Release this watchkey from the Guardian.");
        getGlobalStorage().removeWatchKey(localKey);
    }

    WatchingStore<Tache, Path> getGlobalStorage() {
        return globalStorage;
    }
    public void setCommandArgs(CommandArgs commandArgs) {
        this.commandArgs = commandArgs;
    }
}
