package com.github.athieriot.jtaches.utils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

import static com.github.athieriot.jtaches.utils.GuardianUtils.pairCollectionAsMap;
import static com.github.athieriot.jtaches.utils.GuardianUtils.relativizedEvent;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
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

    public void a_collection_of_pair_must_give_a_map() throws IOException {
        FileSystem fileSystem = FileSystems.getDefault();

        Path tmp = Paths.get("/tmp");
        List<Pair<WatchKey, Path>> origin = newArrayList();
        WatchKey watchKey = tmp.register(fileSystem.newWatchService(), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        origin.add(new ImmutablePair(watchKey, tmp));

        Map<WatchKey,Path> expectedMap = pairCollectionAsMap(origin);

        assertNotNull(expectedMap);
        assertTrue(expectedMap.size() > 0);
        assertTrue(expectedMap instanceof Map);
        assertEquals(tmp, expectedMap.get(watchKey));
    }
}
