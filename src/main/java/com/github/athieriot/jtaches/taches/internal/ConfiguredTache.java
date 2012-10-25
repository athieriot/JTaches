package com.github.athieriot.jtaches.taches.internal;

import com.github.athieriot.jtaches.Tache;
import com.github.athieriot.jtaches.command.Configuration;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_EXCLUDES;
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
        List<String> allMandatoryFields = loadedWithDefaultFieldsValue(mandatories);
        if (!validateConfiguration(configuration, allMandatoryFields)) {
            throw new InvalidParameterException("An error occured while creating the task. Configuration invalid: " + allMandatoryFields.toString() + " needed.");
        }

        additionalValidation(configuration);
    }

    final boolean validateConfiguration(Map<String, String> configuration, List<String> mandatories) {
        return mandatories == null || configuration == null || configuration.keySet().containsAll(mandatories);
    }

    protected void additionalValidation(Map<String, String> configuration) throws InvalidParameterException {}

    private List<String> loadedWithDefaultFieldsValue(List<String> mandatories) {
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

    @Override
    public Collection<String> getExcludes() {
        String excludesConfiguration = configuration.get(CONFIGURATION_EXCLUDES);
        return excludesConfiguration == null ? Collections.<String>emptyList() : newArrayList(excludesConfiguration.split(Configuration.DELIMITER));
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }
}
