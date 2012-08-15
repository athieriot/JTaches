package com.github.awesomeless.jtaches.taches.internal;

import org.testng.annotations.Test;

import java.nio.file.WatchEvent;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfiguredTacheTest {

    @Test
    public void a_configured_tache_must_accept_map_with_path() throws Exception {
        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        expectedConfiguration.put("other", "");

        ConfiguredTache configuredTache = new DummyConfiguredTache(expectedConfiguration);

        assertNotNull(configuredTache);
        assertEquals(configuredTache.getPath().toString(), expectedConfiguration.get("path"));
        assertEquals(configuredTache.getConfiguration(), expectedConfiguration);
    }

    private class DummyConfiguredTache extends ConfiguredTache {
        protected DummyConfiguredTache(Map<String, String> configuration) {
            super(configuration);
        }

        @Override
        public void onCreate(WatchEvent<?> event) {}
        @Override
        public void onDelete(WatchEvent<?> event) {}
        @Override
        public void onModify(WatchEvent<?> event) {}
    }
}
