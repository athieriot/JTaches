package com.github.athieriot.jtaches.utils;

import com.github.athieriot.jtaches.DummyTache;
import com.github.athieriot.jtaches.Tache;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static com.github.athieriot.jtaches.utils.GuardianUtils.*;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class GuardianUtilsTest {

    @Test
    public void relativize_an_even_should_work() throws Exception {
        Path relativePath = get("src/main");
        WatchEvent<?> event = (WatchEvent<?>) newWatchEvent(ENTRY_CREATE);

        Path expectedContext = get(relativePath.toString(), event.context().toString());
        WatchEvent<?> expectedEvent = relativizedEvent(event, relativePath);

        assertEquals(expectedContext, expectedEvent.context());
    }

    @Test
    public void tell_if_a_tache_is_valid() {
        Tache validTache = new DummyTache(get("test"));

        assertTrue(isTacheValid(validTache));
    }

    @Test
    public void tell_if_a_tache_is_invalid() {
        Tache inValidTache = new DummyTache();

        assertFalse(isTacheValid(inValidTache));
    }

    @Test
    public void provide_a_string_for_a_tache() {
        Tache testedTache = new DummyTache(get("test"));

        assertTrue(tacheToString(testedTache).contains("test"));
        assertTrue(tacheToString(testedTache).contains("DummyTache"));
    }

    @Test
    public void path_included_by_default() {
        Tache defaultTache = new DummyTache();

        assertTrue(included(defaultTache, get("ok")));
    }

    @Test
    public void path_excluded_if_asked() {
        DummyTache defaultTache = new DummyTache();
        defaultTache.setExcludes(newArrayList(".*yaml"));

        assertFalse(included(defaultTache, get("jtaches.yaml")));
    }

    @Test
    public void path_excluded_if_deliniter_escaped() {
        DummyTache defaultTache = new DummyTache();
        defaultTache.setExcludes(newArrayList("but\\;not"));

        assertFalse(included(defaultTache, get("but;not")));
    }
}
