package com.github.athieriot.jtaches.command;

import com.github.athieriot.jtaches.Tache;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public enum Configuration {;

    public static final String DELIMITER = "(?<!\\\\);";

    public static final String CONFIGURATION_PATH = "path";
    public static final String CONFIGURATION_EXCLUDES = "excludes";

    public static List<Tache> yamlToMap(String configurationFile) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        List objects = (List) yaml.load(new FileInputStream(new File(configurationFile)));

        if(null == objects) {
            return newArrayList();
        }
        verifyInstances(objects);

        return (List<Tache>) objects;
    }

    private static void verifyInstances(List objects) {
        for (Object o : objects) {
            if(!(o instanceof Tache)) {
                throw new InvalidParameterException("Only instances of Tache are allowed: " + o.getClass().getCanonicalName());
            }
        }
    }
}
