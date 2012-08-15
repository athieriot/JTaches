package com.github.awesomeless.jtaches.command;

import com.google.common.base.Function;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;

public class Configuration {

    public final static String CONFIGURATION_PATH = "path";

    public static Map<String, Map<String, String>> yamlToMap(String configurationFile) throws FileNotFoundException {
        Yaml yaml = new Yaml();

        return transformValues(
            (Map<String, List<Map<String, String>>>) yaml.load(new FileInputStream(new File(configurationFile))),
            new Function<List<Map<String, String>>, Map<String, String>>() {
                @Override
                public Map<String, String> apply(List<Map<String, String>> hardConfiguration) {
                    Map<String, String> tempMap = newHashMap();

                    for(Map<String, String> hardMap : hardConfiguration) {
                        tempMap.putAll(hardMap);
                    }

                    return tempMap;
                }
            }
        );
    }
}
