package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Map;

import static java.nio.file.Paths.get;

public class SysoutTache extends ConfiguredTache {

    public SysoutTache(Map<String, String> configuration) {
        super(configuration);
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        System.out.println("Created: " + rationalizePath(event));
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        System.out.println("Deleted: " + rationalizePath(event));
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        System.out.println("Modified: " + rationalizePath(event));
    }

    private Path rationalizePath(WatchEvent<?> event) {
        return get(getPath().toString(), event.context().toString());
    }
}
