package com.github.athieriot.jtaches.utils;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import static com.github.athieriot.jtaches.utils.TestUtils.*;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static org.testng.Assert.*;

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
        assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE).kind(), StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_a_delete_event() throws Exception {
        assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE).kind(), StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_a_modify_event() throws Exception {
        assertEquals(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY).kind(), StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Test
    public void newWatchEvent_must_accept_a_path() {
        Path expectedPath = get(".");
        WatchEvent<Path> testable = newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, expectedPath);

        assertEquals(testable.kind(), StandardWatchEventKinds.ENTRY_MODIFY);
        assertEquals(testable.context(), expectedPath);
    }

    @Test
    public void newWatchEvent_must_be_able_to_create_an_overflow_event() throws Exception {
        assertEquals(newOverFlowEvent().kind(), StandardWatchEventKinds.OVERFLOW);
    }

    @Test
    public void launchThreadedCreation_must_create_a_file() throws IOException, InterruptedException {
        Path testedPath = get(createTempDirectory("_awesomeless").toString(), "backtothefuture");

        launchThreadedCreation(testedPath);
        Thread.sleep(10);

        assertTrue(exists(testedPath));
    }

    @Test
    public void launchThreadedDelection_must_delete_a_file() throws IOException, InterruptedException {
        Path testedPath = createDirectories(get(createTempDirectory("_awesomeless").toString(), "backtothefuture2"));

        launchThreadedDeletion(testedPath);
        Thread.sleep(10);

        assertFalse(exists(testedPath));
    }
}
