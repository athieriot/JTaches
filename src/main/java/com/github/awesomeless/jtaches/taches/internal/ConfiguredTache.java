package com.github.awesomeless.jtaches.taches.internal;

import com.github.awesomeless.jtaches.Tache;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;

abstract public class ConfiguredTache implements Tache {

    private Map<String, String> configuration;

    protected ConfiguredTache(Map<String, String> configuration) {
        validateConfiguredTache(configuration, null);
        this.configuration = configuration;
    }
    protected ConfiguredTache(Map<String, String> configuration, String... mandatory) {
        validateConfiguredTache(configuration, asList(mandatory));
        this.configuration = configuration;
    }

    private void validateConfiguredTache(Map<String, String> configuration, List<String> mandatories) {
        if(!validateConfiguration(configuration, loadedWithDefaultValues(mandatories))) {
            throw new InvalidParameterException();
        }
    }
    boolean validateConfiguration(Map<String, String> configuration, List<String> mandatories) {
        return mandatories == null || configuration == null || configuration.keySet().containsAll(mandatories);
    }

    List<String> loadedWithDefaultValues(List<String> mandatories) {
        //Path need to be set to be able to watch
        if(mandatories != null) {
            List<String> temp = newArrayList(mandatories);
            temp.add(CONFIGURATION_PATH);
            return temp;
        } else {
            return newArrayList(CONFIGURATION_PATH);
        }
    }

    @Override
    public Path getPath() {
        return get(configuration.get(CONFIGURATION_PATH));
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }
}
