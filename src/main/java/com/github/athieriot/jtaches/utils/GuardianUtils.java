package com.github.athieriot.jtaches.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.debug;
import static com.google.common.collect.Maps.newHashMap;
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

    public static<L, R> Map<L, R> pairCollectionAsMap(Collection<Pair<L, R>> origin) {
        Map<L, R> target = newHashMap();

        for(Pair<L, R> pair : origin) {
            target.put(pair.getLeft(), pair.getRight());
        }

        return target;
    }
}
