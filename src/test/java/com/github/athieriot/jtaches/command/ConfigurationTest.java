package com.github.athieriot.jtaches.command;

import com.github.athieriot.jtaches.DummyTache;
import com.github.athieriot.jtaches.Tache;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.List;

import static com.github.athieriot.jtaches.command.Configuration.yamlToMap;
import static org.testng.Assert.*;

//TODO: Test booleans
public class ConfigurationTest {

    @Test
    public void a_configuration_file_must_be_parsable() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.test.yaml").getFile();

        List<Tache> taches = yamlToMap(testFile);
        assertNotNull(taches);
        assertFalse(taches.isEmpty());

        assertTrue(taches.get(0) instanceof DummyTache);
        assertEquals(taches.get(0).getPath().toString(), ".");
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void a_configuration_file_must_not_authorize_creation_of_other_things() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.never.yaml").getFile();

        yamlToMap(testFile);
    }

    @Test
    public void a_configuration_file_empty_must_be_parsable() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.blank.yaml").getFile();

        assertTrue(yamlToMap(testFile).isEmpty());
    }
}
