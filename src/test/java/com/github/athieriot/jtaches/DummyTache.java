package com.github.athieriot.jtaches;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collection;

import static java.nio.file.Paths.get;
import static java.util.Collections.emptyList;

public class DummyTache  implements Tache {

    private Path path;
    private Collection<String> excludes = emptyList();

    public DummyTache() {
    }

    public DummyTache(String path) {
        this.path = get(path);
    }

    public DummyTache(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Collection<String> getExcludes() {
        return excludes;
    }
    public void setExcludes(Collection<String> excludes) {
        this.excludes = excludes;
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
