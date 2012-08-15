package com.github.awesomeless.jtaches.taches;

import com.github.awesomeless.jtaches.ConfiguredTache;

import java.nio.file.WatchEvent;
import java.util.Map;

public class SysoutTache extends ConfiguredTache {

    public SysoutTache(Map<String, String> configuration) {
        super(configuration);
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        System.out.println("Created: " + event.context().toString());
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        System.out.println("Deleted: " + event.context().toString());
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        System.out.println("Modified: " + event.context().toString());
    }
}
