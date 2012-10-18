package com.github.athieriot.jtaches;

import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.WatchKey;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class WatchingStoreTest {

    private final static String TEST_ITEM = "Yo";
    private final static String TEST_ITEM2 = "Man";

    private final static String TEST_META = "Rasta";
    private final static String TEST_META2 = "Rocket";
    private final static String TEST_META3 = "Winter";

    private WatchingStore<String, String> testStore = new WatchingStore<>();

    @Mock
    WatchKey watchKey;
    @Mock
    WatchKey watchKey2;
    @Mock
    WatchKey watchKey3;
    @Mock
    WatchKey watchKey4;

    @BeforeClass
    public void testStore() throws Exception {
        initMocks(this);

        testStore.store(TEST_ITEM, watchKey, TEST_META);
        testStore.store(TEST_ITEM, watchKey2, TEST_META2);
        testStore.store(TEST_ITEM2, watchKey3, TEST_META3);
    }

    @Test
    public void testRetrieveItems() throws Exception {
        assertEquals(2, testStore.retrieveItems().size());
        assertTrue(testStore.retrieveItems().contains(TEST_ITEM));
        assertTrue(testStore.retrieveItems().contains(TEST_ITEM2));
    }

    @Test
    public void testRetrieveWatchKeys() throws Exception {
        assertEquals(3, testStore.retrieveWatchKeys().size());
        assertTrue(testStore.retrieveWatchKeys().contains(watchKey));
        assertTrue(testStore.retrieveWatchKeys().contains(watchKey2));
        assertTrue(testStore.retrieveWatchKeys().contains(watchKey3));
    }

    @Test
    public void testRetreiveMetadata() throws Exception {
        assertEquals(TEST_META, testStore.retreiveMetadata(watchKey));
        assertEquals(TEST_META3, testStore.retreiveMetadata(watchKey3));
        assertEquals(null, testStore.retreiveMetadata(null));
    }

    @Test
    public void testIsWatched() throws Exception {
        assertTrue(testStore.isWatched(watchKey));
        assertFalse(testStore.isWatched(watchKey4));
        assertFalse(testStore.isWatched(null));
    }

    @Test
    public void testIsWatchedByItem() throws Exception {
        assertTrue(testStore.isWatchedByItem(TEST_ITEM, watchKey));
        assertFalse(testStore.isWatchedByItem(TEST_ITEM, watchKey3));
        assertFalse(testStore.isWatchedByItem(TEST_ITEM2, watchKey4));
        assertFalse(testStore.isWatchedByItem(null, null));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(testStore.isEmpty());
        assertTrue(new WatchingStore<String, String>().isEmpty());
    }
}
