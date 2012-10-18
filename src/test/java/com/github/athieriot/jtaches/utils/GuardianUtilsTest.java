package com.github.athieriot.jtaches.utils;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static com.github.athieriot.jtaches.utils.GuardianUtils.relativizedEvent;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.testng.AssertJUnit.assertEquals;

public class GuardianUtilsTest {

    @Test
    public void relativize_an_even_should_work() throws Exception {
        Path relativePath = get("src/main");
        WatchEvent<?> event = (WatchEvent<?>) newWatchEvent(ENTRY_CREATE);

        Path expectedContext = get(relativePath.toString(), event.context().toString());
        WatchEvent<?> expectedEvent = relativizedEvent(event, relativePath);

        assertEquals(expectedContext, expectedEvent.context());
    }
}
