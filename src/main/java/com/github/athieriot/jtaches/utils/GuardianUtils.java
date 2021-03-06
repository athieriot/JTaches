package com.github.athieriot.jtaches.utils;

import com.github.athieriot.jtaches.Tache;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.security.PrivilegedAction;

import static com.esotericsoftware.minlog.Log.debug;
import static java.nio.file.Paths.get;
import static java.security.AccessController.doPrivileged;

public enum GuardianUtils {;

    public static WatchEvent<?> relativizedEvent(WatchEvent<?> event, Path relativePath) {
        try {
            Field context = event.getClass().getDeclaredField("context");
            forbiddenSet(event, get(relativePath.toString(), event.context().toString()), context);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            debug("Unable to decorate " + event.kind() + " event: " + event.context() + " with relative path: " + relativePath, e);
        }

        return event;
    }

    public static boolean isTacheValid(Tache tache) {
        //Try every patterns to fail fast if one is not valid
        included(tache, get(""));

        return tache.getPath() != null;
    }
    public static String tacheToString(Tache tache) {
        return tache.getClass().getSimpleName() + " watching on directory: " + tache.getPath();
    }

    private static void forbiddenSet(WatchEvent<?> event, Path path, Field context) throws IllegalAccessException {
        setAccessibility(context, true);
        context.set(event, path);
        setAccessibility(context, false);
    }

    private static void setAccessibility(final Field context, final boolean accessible) {
        doPrivileged(
            new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    context.setAccessible(accessible); return true;
                }
            }
        );
    }

    public static boolean included(Tache tache, Path path) {
        boolean included = true;

        for(String excludeRegexp : tache.getExcludes()) {
            included = !path.toString().matches(excludeRegexp);
        }
        return included;
    }
}
