package com.github.awesomeless.jtaches.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import static com.github.awesomeless.jtaches.utils.TestUtils.newWatchEvent;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TestUtilsTest {

    @Test
    public void newWatchEvent_must_not_be_null() throws Exception {
        WatchEvent<Path> expectedWatchEvent = newWatchEvent(null);

        assertNotNull(expectedWatchEvent);
        assertTrue(expectedWatchEvent.count() >= 0);
        assertNotNull(expectedWatchEvent.context());
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_a_create_event() throws Exception {
        Assert.assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE).kind(), StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_a_delete_event() throws Exception {
        Assert.assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE).kind(), StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_a_modify_event() throws Exception {
        Assert.assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY).kind(), StandardWatchEventKinds.ENTRY_MODIFY);
    }
}
