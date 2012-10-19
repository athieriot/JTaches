package com.github.athieriot.jtaches;

import com.google.common.collect.Multimap;

import java.nio.file.WatchKey;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.ArrayListMultimap.create;
import static com.google.common.collect.Maps.newConcurrentMap;

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

    private Map<WatchKey, AdditionalMetadata<M>> metadatas = newConcurrentMap();

    /*
     * Store watched metadata for an item
     *
     * If item don't exists in the store, it's created
     */
    public void store(I item, WatchKey watchKey, M metadata) {
        store(item, watchKey, metadata, false);
    }
    public void store(I item, WatchKey watchKey, M metadata, boolean flag) {
        items.put(item, watchKey);
        metadatas.put(watchKey, new AdditionalMetadata<M>(metadata, flag));
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
        return isWatched(watchKey) ? metadatas.get(watchKey).getUserMetadata() : null;
    }

    /*
     * Verify if the store contains information about this watch
     */
    public boolean isWatched(WatchKey watchKey) {
        return watchKey == null ? false : metadatas.containsKey(watchKey);
    }

    /*
     * Verify if the store contains information about this watch for a given item
     */
    public boolean isWatchedByItem(I item, WatchKey watchKey) {
        return items.containsEntry(item, watchKey);
    }

    /*
     * Possiblity of "flagging" a metadata
     */
    public boolean hasFlag(WatchKey watchKey) {
        return metadatas.get(watchKey).hasFlag();
    }

    /*
     * Verify if the store is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    //TODO: Test
    public void removeWatchKey(WatchKey watchKey) {
        //FIXME: Remove from items as well
        metadatas.remove(watchKey);
        watchKey.cancel();
    }

    private final class AdditionalMetadata<M> {

        private M userMetadata;

        private boolean flag;

        private AdditionalMetadata(M userMetadata, boolean flag) {
            this.userMetadata = userMetadata;
            this.flag = flag;
        }

        public M getUserMetadata() {
            return userMetadata;
        }

        public boolean hasFlag() {
            return flag;
        }
    }
}
