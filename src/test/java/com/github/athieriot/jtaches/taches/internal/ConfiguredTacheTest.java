package com.github.athieriot.jtaches.taches.internal;

import org.testng.annotations.Test;

import java.nio.file.WatchEvent;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static org.testng.Assert.*;

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

    @Test(expectedExceptions = InvalidParameterException.class)
    public void create_a_configured_tache_without_path_must_be_forbidden() {
        Map<String, String> expectedConfiguration = newHashMap();

        new DummyConfiguredTache(expectedConfiguration);
    }

    @Test
    public void a_configured_tache_must_accept_map_with_path_and_mandatory() throws Exception {
        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        expectedConfiguration.put("other", "");

        ConfiguredTache configuredTache = new DummyConfiguredTache(expectedConfiguration, "other");

        assertNotNull(configuredTache);
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void create_a_configured_tache_must_throw_an_exception_if_bad_config() {
        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        expectedConfiguration.put("other", "");

        new DummyConfiguredTache(expectedConfiguration, "jenesuispasla");
    }

    @Test
    public void a_configured_tache_must_be_able_to_validate_good_configuration() {
        Map<String, String> configMap = newHashMap();
        configMap.put("yo", "man");
        List<String> mandatoryList = newArrayList();
        mandatoryList.add("yo");

        assertTrue(new DummyConfiguredTache(null).validateConfiguration(configMap, mandatoryList));
    }

    @Test
    public void a_configured_tache_must_be_able_to_validate_empty_mandatory() {
        Map<String, String> configMap = newHashMap();
        configMap.put("yo", "man");
        List<String> mandatoryList = newArrayList();

        assertTrue(new DummyConfiguredTache(null).validateConfiguration(configMap, mandatoryList));
    }

    @Test
    public void a_configured_tache_must_be_able_to_invalidate_bad_configuration() {
        Map<String, String> configMap = newHashMap();
        configMap.put("yo", "man");
        List<String> mandatoryList = newArrayList();
        mandatoryList.add("nop");

        assertFalse(new DummyConfiguredTache(null).validateConfiguration(configMap, mandatoryList));
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void a_configured_tache_must_block_on_additional_validation() throws Exception {
        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        expectedConfiguration.put("addonValidator", "");

        DummyConfiguredTache configuredTache = new DummyConfiguredTache(expectedConfiguration);
    }

    @Test
    public void a_configured_tache_must_take_excludes_parameter_empty() {
        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        DummyConfiguredTache configuredTache = new DummyConfiguredTache(expectedConfiguration);

        assertEquals(emptyList(), configuredTache.getExcludes());
    }

    @Test
    public void a_configured_tache_must_take_excludes_parameter() {
        String testedPattern = ".*test";

        Map<String, String> expectedConfiguration = newHashMap();
        expectedConfiguration.put("path", ".");
        expectedConfiguration.put("excludes", testedPattern);
        DummyConfiguredTache configuredTache = new DummyConfiguredTache(expectedConfiguration);

        assertEquals(newArrayList(testedPattern), configuredTache.getExcludes());
    }

    private class DummyConfiguredTache extends ConfiguredTache {
        protected DummyConfiguredTache(Map<String, String> configuration) {
            super(configuration);
        }
        private DummyConfiguredTache(Map<String, String> configuration, String... mandatory) {
            super(configuration, mandatory);
        }

        @Override
        protected void additionalValidation(Map<String, String> configuration) {
            if(configuration != null && configuration.containsKey("addonValidator")) {throw new InvalidParameterException();}
        }
        @Override
        public void onCreate(WatchEvent<?> event) {}
        @Override
        public void onDelete(WatchEvent<?> event) {}
        @Override
        public void onModify(WatchEvent<?> event) {}
    }
}
