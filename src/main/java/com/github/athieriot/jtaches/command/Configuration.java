package com.github.athieriot.jtaches.command;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import com.github.athieriot.jtaches.Tache;

public class Configuration {

    public final static String CONFIGURATION_PATH = "path";

    public static List<Tache> yamlToMap(String configurationFile) throws FileNotFoundException {
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(Configuration.class.getClassLoader()));
        List objects = (List) yaml.load(new FileInputStream(new File(configurationFile)));

        if(null == objects) return newArrayList();
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
