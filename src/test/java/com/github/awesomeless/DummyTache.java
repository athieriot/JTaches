package com.github.awesomeless;

import com.google.common.collect.Lists;

import java.nio.file.*;
import java.util.Collection;

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
