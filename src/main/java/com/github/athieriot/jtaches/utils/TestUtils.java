package com.github.athieriot.jtaches.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public enum TestUtils {;

    public static WatchEvent<Path> newWatchEvent(final WatchEvent.Kind<Path> kind) {
        Path path;
        try {path = Files.createTempFile("noevent", "");
        } catch (IOException e) {path = null;}

        return newWatchEvent(kind, path);
    }

    public static WatchEvent<Path> newWatchEvent(final WatchEvent.Kind<Path> kind, final Path path) {
        return buildWatchEvent(kind, path);
    }

    private static WatchEvent<Path> buildWatchEvent(final WatchEvent.Kind<Path> kind, final Path path) {
        return new WatchEvent<Path>() {
            @Override
            public Kind<Path> kind() { return kind; }
            @Override
            public int count() {return 0;}
            @Override
            public Path context() { return path; }
        };
    }

    public static WatchEvent<Object> newOverFlowEvent() {
        return new WatchEvent<Object>() {
            @Override
            public Kind<Object> kind() { return StandardWatchEventKinds.OVERFLOW; }
            @Override
            public int count() { return 0; }
            @Override
            public Object context() { return null; }
        };
    }
}
