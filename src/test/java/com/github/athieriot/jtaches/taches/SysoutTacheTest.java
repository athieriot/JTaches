package com.github.athieriot.jtaches.taches;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Maps.newHashMap;

public class SysoutTacheTest {

    private SysoutTache sysoutTache;

    @BeforeTest
    public void setup() {
        Map<String, String> map = newHashMap();
        map.put("path", "/tmp");

        sysoutTache = new SysoutTache(map);
    }

    @Test
    public void syouttache_must_not_throw_exception_on_create_event() throws Exception {
        sysoutTache.onCreate(newWatchEvent(null));
    }

    @Test
    public void syouttache_must_not_throw_exception_on_delete_event() throws Exception {
        sysoutTache.onDelete(newWatchEvent(null));
    }

    @Test
    public void syouttache_must_not_throw_exception_on_modify_event() throws Exception {
        sysoutTache.onModify(newWatchEvent(null));
    }
}
