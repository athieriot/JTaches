package com.github.awesomeless;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.Paths.get;

public class Guardian {

    private WatchService watchService;
    private WatchKey globalWatchKey;

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

    public WatchKey register(Path path) throws IOException {
        WatchKey key = path.register(this.watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_DELETE,
                                    StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Register: " + path.getFileName().toString());
        return key;
    }

    public void watch() {
        try {
            globalWatchKey = this.watchService.take();

            while (true) {
                for (final WatchEvent<?> event : globalWatchKey.pollEvents()) {
                    onEvent(event);
                }

                if (!globalWatchKey.reset()) {
                    System.out.println("Watcher no longer valid. Closing.");
                    cancel();
                    this.watchService.close();
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        globalWatchKey.cancel();
    }

    public void onEvent(WatchEvent<?> event) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            onCreate(event);
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            onDelete(event);
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
            onModify(event);
        }
    }

    public void onModify(WatchEvent<?> event) {
        System.out.println("Modify: " + event.context().toString());
    }

    public void onDelete(WatchEvent<?> event) {
        System.out.println("Delete: " + event.context().toString());
    }

    public void onCreate(WatchEvent<?> event) {
        System.out.println("Created: " + event.context().toString());
    }
}
