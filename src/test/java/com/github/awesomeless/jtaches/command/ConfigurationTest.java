package com.github.awesomeless.jtaches.command;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.yamlToMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigurationTest {

    @Test
    public void a_configuration_file_must_be_parsable() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.test.yaml").getFile();

        Map<String, String> map = yamlToMap(testFile);

        assertNotNull(map);
        assertEquals(map.get("path"), ".");
        assertEquals(map.get("other"), "ohyeah");
    }
}
