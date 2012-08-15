package com.github.awesomeless.jtaches.command;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Configuration {

    public final static String CONFIGURATION_PATH = "path";

    public static Map<String, String> yamlToMap(String configurationFile) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return (Map<String, String>) yaml.load(new FileInputStream(new File(configurationFile)));
    }
}
