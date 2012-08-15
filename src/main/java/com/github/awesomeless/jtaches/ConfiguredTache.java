package com.github.awesomeless.jtaches;

import java.nio.file.Path;
import java.util.Map;

import static java.nio.file.Paths.get;

abstract public class ConfiguredTache implements Tache {

    public final static String CONFIGURATION_PATH = "path";

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
