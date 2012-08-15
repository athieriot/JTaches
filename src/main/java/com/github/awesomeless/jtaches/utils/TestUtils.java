package com.github.awesomeless.jtaches.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class TestUtils {

    public static WatchEvent<Path> newWatchEvent(final WatchEvent.Kind<Path> kind) {
        return new WatchEvent<Path>() {
            @Override
            public Kind<Path> kind() {return kind;}
            @Override
            public int count() {return 0;}
            @Override
            public Path context() {
                try {return Files.createTempFile("noevent", "");
                } catch (IOException e) {return null;}
            }
        };
    }
}
