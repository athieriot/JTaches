package com.github.athieriot.jtaches.taches;

import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.github.athieriot.jtaches.taches.RabbitmqTache.*;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Files.createTempDir;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RabbitmqTacheTest {

    private final File path = createTempDir();

    @Test
    public void event_create_must_return_routingKey() {
        Map<String,String> map = newHashMap();
        map.put(CONFIGURATION_PATH, path.toString());
        map.put(CONFIGURATION_EXCHANGE_NAME, "exchange");

        RabbitmqTache testedTache = new RabbitmqTache(map);
        assertEquals("ENTRY_CREATE", testedTache.eventToRoutingKey(newWatchEvent(ENTRY_CREATE)));

        String expectedCreateKey = "expectedCreateKey";
        map.put(CONFIGURATION_CREATE_EVENT_ROUTING_KEY, expectedCreateKey);
        assertEquals(expectedCreateKey, testedTache.eventToRoutingKey(newWatchEvent(ENTRY_CREATE)));
    }

    @Test
    public void event_delete_must_return_routingKey() {
        Map<String,String> map = newHashMap();
        map.put(CONFIGURATION_PATH, path.toString());
        map.put(CONFIGURATION_EXCHANGE_NAME, "exchange");

        RabbitmqTache testedTache = new RabbitmqTache(map);
        assertEquals("ENTRY_DELETE", testedTache.eventToRoutingKey(newWatchEvent(ENTRY_DELETE)));

        String expectedDeleteKey = "expectedDeleteKey";
        map.put(CONFIGURATION_DELETE_EVENT_ROUTING_KEY, expectedDeleteKey);
        assertEquals(expectedDeleteKey, testedTache.eventToRoutingKey(newWatchEvent(ENTRY_DELETE)));
    }

    @Test
    public void event_modify_must_return_routingKey() {
        Map<String,String> map = newHashMap();
        map.put(CONFIGURATION_PATH, path.toString());
        map.put(CONFIGURATION_EXCHANGE_NAME, "exchange");

        RabbitmqTache testedTache = new RabbitmqTache(map);
        assertEquals("ENTRY_MODIFY", testedTache.eventToRoutingKey(newWatchEvent(ENTRY_MODIFY)));

        String expectedModifyKey = "expectedModifyKey";
        map.put(CONFIGURATION_MODIFY_EVENT_ROUTING_KEY, expectedModifyKey);
        assertEquals(expectedModifyKey, testedTache.eventToRoutingKey(newWatchEvent(ENTRY_MODIFY)));
    }

    @Test
    public void event_must_return_a_relative_path() {
        Map<String,String> map = newHashMap();
        map.put(CONFIGURATION_PATH, "./src");
        map.put(CONFIGURATION_EXCHANGE_NAME, "exchange");

        RabbitmqTache testedTache = new RabbitmqTache(map);

        assertEquals("./src/testFile", testedTache.eventToFilePath(newWatchEvent(ENTRY_MODIFY, get("testFile"))));
    }

    @Test
    public void event_must_return_a_absolute_path() {
        Map<String,String> map = newHashMap();
        map.put(CONFIGURATION_PATH, "./src");
        map.put(CONFIGURATION_EXCHANGE_NAME, "exchange");
        map.put(CONFIGURATION_ABSOLUTE_PATH, "true");

        RabbitmqTache testedTache = new RabbitmqTache(map);

        assertTrue(testedTache.eventToFilePath(newWatchEvent(ENTRY_MODIFY, get("testFile"))).matches("\\/.*\\/src\\/testFile"));
    }
}