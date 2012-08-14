package com.github.awesomeless;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.Paths.get;

public class Guardian {

    private WatchService watchService;

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
            WatchKey watchKey = this.watchService.take();

            while (true) {
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    doEvent(event);
                }

                if (!watchKey.reset()) {
                    System.out.println("Watcher no longer valid. Closing.");
                    watchKey.cancel();
                    watchService.close();
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void doEvent(WatchEvent<?> event) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            System.out.println("Created: " + event.context().toString());
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            System.out.println("Delete: " + event.context().toString());
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
            System.out.println("Modify: " + event.context().toString());
        }
    }
}
