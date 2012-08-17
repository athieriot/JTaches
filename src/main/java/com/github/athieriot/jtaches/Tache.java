package com.github.athieriot.jtaches;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface Tache {
    Path getPath();
    void onCreate(WatchEvent<?> event);
    void onDelete(WatchEvent<?> event);
    void onModify(WatchEvent<?> event);
}
