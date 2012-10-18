package com.github.athieriot.jtaches;

import com.google.common.collect.Multimap;

import java.nio.file.WatchKey;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.ArrayListMultimap.create;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Corresponding store between parent and sub metadata watched.
 * Used internally by the Guardian to make recursive watching working
 *
 * The goal is to register sub items but keep a relation with one parent
 * In the case of the guardian, it's to deal with recursive paths watching
 */
//TODO: Generic needed?
//TODO: Refactor names and meanings
public class WatchingStore<I, M> {

    private Multimap<I, WatchKey> items = create();

    private Map<WatchKey, M> metadatas = newHashMap();

    /*
     * Store watched metadata for an item
     *
     * If item don't exists in the store, it's created
     */
    public void store(I item, WatchKey watchKey, M metadata) {
        items.put(item, watchKey);
        metadatas.put(watchKey, metadata);
    }

    /*
     * Retrieve all items
     */
    public Collection<I> retrieveItems() {
        return items.keySet();
    }

    /*
     * Retrieve all watch keys
     * Mostly for tests
     */
    public Collection<WatchKey> retrieveWatchKeys() {
        return metadatas.keySet();
    }

    /*
     * Retrieve specifically watched metadata
     * Mostly for tests
     */
    public M retreiveMetadata(WatchKey watchKey) {
        return metadatas.get(watchKey);
    }

    /*
     * Verify if the store contains information about this watch
     */
    public boolean isWatched(WatchKey watchKey) {
        return metadatas.containsKey(watchKey);
    }

    /*
     * Verify if the store contains information about this watch for a given item
     */
    public boolean isWatchedByItem(I item, WatchKey watchKey) {
        return items.containsEntry(item, watchKey);
    }

    /*
     * Verify if the store is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
