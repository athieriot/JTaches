package com.github.athieriot.jtaches;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collection;

public interface Tache {
    Path getPath();
    Collection<String> getExcludes();
    void onCreate(WatchEvent<?> event);
    void onDelete(WatchEvent<?> event);
    void onModify(WatchEvent<?> event);
}
