package com.github.awesomeless;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.Watchable;
import java.util.Collection;

public interface Tache {
    Path getPath();
    void onCreate(WatchEvent<?> event);
    void onDelete(WatchEvent<?> event);
    void onModify(WatchEvent<?> event);
}
