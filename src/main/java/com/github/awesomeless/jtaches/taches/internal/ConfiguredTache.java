package com.github.awesomeless.jtaches.taches.internal;

import com.github.awesomeless.jtaches.Tache;

import java.nio.file.Path;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.CONFIGURATION_PATH;
import static java.nio.file.Paths.get;

abstract public class ConfiguredTache implements Tache {

    private Map<String, String> configuration;

    protected ConfiguredTache(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    @Override
    public Path getPath() {
        return get(configuration.get(CONFIGURATION_PATH));
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }
}
