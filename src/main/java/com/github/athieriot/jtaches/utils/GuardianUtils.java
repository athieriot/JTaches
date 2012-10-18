package com.github.athieriot.jtaches.utils;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static com.esotericsoftware.minlog.Log.debug;
import static java.nio.file.Paths.get;

public enum GuardianUtils {;

    public static WatchEvent<?> relativizedEvent(WatchEvent<?> event, Path relativePath) {
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
}
