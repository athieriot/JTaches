package com.github.awesomeless.jtaches;

import com.github.awesomeless.jtaches.Tache;

import java.nio.file.*;

public class DummyTache implements Tache {

    private Path path;

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
