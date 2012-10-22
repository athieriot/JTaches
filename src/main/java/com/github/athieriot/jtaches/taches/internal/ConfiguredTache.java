package com.github.athieriot.jtaches.taches.internal;

import com.github.athieriot.jtaches.Tache;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;

public abstract class ConfiguredTache implements Tache {

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
        List<String> allMandatories = loadedWithDefaultValues(mandatories);
        if (!validateConfiguration(configuration, allMandatories)) {
            throw new InvalidParameterException("An error occured while creating the task. Configuration invalid: " + allMandatories.toString() + " needed.");
        }

        additionalValidation(configuration);
    }

    final boolean validateConfiguration(Map<String, String> configuration, List<String> mandatories) {
        return mandatories == null || configuration == null || configuration.keySet().containsAll(mandatories);
    }

    protected void additionalValidation(Map<String, String> configuration) throws InvalidParameterException {}

    //TODO: Not good to have this in ConfiguredTache only
    private List<String> loadedWithDefaultValues(List<String> mandatories) {
        //Path need to be set to be able to watch
        if (mandatories != null) {
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
