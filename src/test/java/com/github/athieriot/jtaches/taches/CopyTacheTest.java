package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.Tache;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.naming.spi.DirectoryManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.github.athieriot.jtaches.taches.CopyTache.CONFIGURATION_COPY_TO;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Files.copy;
import static com.google.common.io.Files.createTempDir;
import static java.nio.file.Paths.get;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.*;

//CLEANUP: This test is not really a beauty
public class CopyTacheTest {

    private final Map<String,String> map = newHashMap();
    private final File fromTemp = createTempDir();
    private final File toTemp = createTempDir();

    @BeforeTest
    public void setup() {
        map.put(CONFIGURATION_PATH, fromTemp.getAbsolutePath());
        map.put(CONFIGURATION_COPY_TO, toTemp.getAbsolutePath());
    }

    @Test
    public void copy_tache_must_copy_if_a_file_is_created() throws IOException {
        Tache tache = spy(new CopyTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.gad");
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE, //
                                     get(from.getName()))); //

        assertTrue(new File(toTemp + "/" + from.getName()).exists());
    }

    @Test
    public void copy_tache_must_copy_if_a_directory_is_created() throws IOException {
        Tache tache = spy(new CopyTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "omagad");
        from.mkdir();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE, //
                                     get(from.getName()))); //

        assertTrue(new File(toTemp + "/" + from.getName()).exists());
        assertTrue(new File(toTemp + "/" + from.getName()).isDirectory());
    }

    @Test
    public void copy_tache_must_delete_if_a_file_is_deleted() throws IOException {
        Tache tache = spy(new CopyTache(map));

        File to = new File(toTemp.getAbsolutePath() + "/" + "oma.gad");
        to.createNewFile();

        tache.onDelete(newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE, //
                                     get(to.getName()))); //

        assertFalse(to.exists());
    }

    @Test
    public void copy_tache_must_copy_if_a_file_is_modified() throws IOException {
        Tache tache = spy(new CopyTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.gad");
        from.createNewFile();
        copy(from, new File(toTemp + "/" + from.getName()));

        tache.onModify(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //

        assertTrue(new File(toTemp + "/" + from.getName()).exists());
        assertEquals(from.length(), new File(toTemp + "/" + from.getName()).length());
    }

    @Test
    public void copy_tache_if_to_directory_not_exists_must_not_block() throws IOException {
        Map<String, String> cloneMap = newHashMap();
        cloneMap.putAll(map);
        Tache tache = spy(new CopyTache(cloneMap));
        cloneMap.put(CONFIGURATION_COPY_TO, "bullshit");

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.gad");
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
        tache.onDelete(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
        tache.onModify(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
    }
}
