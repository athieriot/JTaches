package com.github.athieriot.jtaches;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class DummyTache  implements Tache {

    private Path path;

    public DummyTache() {
    }

    public DummyTache(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
    }

    @Override
    public void onModify(WatchEvent<?> event) {
    }
}
