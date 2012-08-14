package com.github.awesomeless;

import java.nio.file.*;

import java.util.List;

import static java.nio.file.Paths.get;

public class Guardian {
    public static void main(String[] args) {
        watching();
    }

    public static void watching() {
        Path home = get(".");

        try {
            FileSystem fileSystem = FileSystems.getDefault();
            WatchService watcher = fileSystem.newWatchService();

            home.register(watcher,  StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_DELETE,
                                    StandardWatchEventKinds.ENTRY_MODIFY);
            System.out.println("Watching: " + home.getFileName().toString());

            WatchKey watckKey = watcher.take();

            List<WatchEvent<?>> events = watckKey.pollEvents();
            for (WatchEvent event : events) {
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
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }
}
