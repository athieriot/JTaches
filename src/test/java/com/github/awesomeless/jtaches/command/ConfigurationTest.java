package com.github.awesomeless.jtaches.command;

import com.google.common.collect.Multimap;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.yamlToMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ConfigurationTest {

    @Test
    public void a_configuration_file_must_be_parsable() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.test.yaml").getFile();

        Map<String, Map<String, String>> multimap = yamlToMap(testFile);
        assertNotNull(multimap);
        assertTrue(multimap.containsKey("DummyTache"));

        Map<String, String> map = multimap.get("DummyTache");
        assertEquals(map.get("path"), ".");
        assertEquals(map.get("other"), "ohyeah");
    }
}
