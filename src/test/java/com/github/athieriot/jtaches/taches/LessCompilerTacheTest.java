package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.Tache;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
import java.security.InvalidParameterException;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.github.athieriot.jtaches.taches.LessCompilerTache.CONFIGURATION_COMPILE_TO;
import static com.github.athieriot.jtaches.taches.LessCompilerTache.CONFIGURATION_MAKE_PATH;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Files.*;
import static java.nio.file.Paths.get;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.*;

//CLEANUP: This test is not really a beauty
public class LessCompilerTacheTest {

    private final Map<String,String> map = newHashMap();
    private final File fromTemp = createTempDir();
    private final File toTemp = createTempDir();

    @BeforeTest
    public void setup() {
        map.put(CONFIGURATION_PATH, fromTemp.getAbsolutePath());
        map.put(CONFIGURATION_COMPILE_TO, toTemp.getAbsolutePath());
    }

    @Test
    public void compile_tache_must_compile_if_a_less_file_is_created() throws IOException {
        Tache tache = spy(new LessCompilerTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.less");
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE, //
                                     get(from.getName()))); //

        assertTrue(new File(toTemp + "/" + "oma.css").exists());
    }

    @Test
    public void compile_tache_must_not_compile_if_another_file_is_created() throws IOException {
        Tache tache = spy(new LessCompilerTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.gad");
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE, //
                                     get(from.getName()))); //

        assertFalse(new File(toTemp + "/" + from.getName()).exists());
    }

    @Test
    public void compile_tache_must_delete_if_a_file_is_deleted() throws IOException {
        Tache tache = spy(new LessCompilerTache(map));

        File from = new File(toTemp.getAbsolutePath() + "/" + "oma.less");
        File to = new File(toTemp.getAbsolutePath() + "/" + "oma.css");
        to.createNewFile();

        tache.onDelete(newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE, //
                                     get(from.getName()))); //

        assertFalse(to.exists());
    }

    @Test
    public void compile_tache_must_copy_if_a_file_is_modified() throws IOException {
        Tache tache = spy(new LessCompilerTache(map));

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.less");
        from.createNewFile();
        copy(from, new File(toTemp + "/" + from.getName()));

        tache.onModify(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //

        assertTrue(new File(toTemp + "/" + "oma.css").exists());
        assertEquals(from.length(), new File(toTemp + "/oma.css").length());
    }

    @Test
    public void compile_tache_if_to_directory_not_exists_must_not_block() throws IOException {
        Map<String, String> cloneMap = newHashMap();
        cloneMap.putAll(map);
        Tache tache = spy(new LessCompilerTache(cloneMap));
        cloneMap.put(CONFIGURATION_COMPILE_TO, "./target/bullshit");
        cloneMap.put(CONFIGURATION_MAKE_PATH, "false");

        File from = new File(fromTemp.getAbsolutePath() + "/" + "oma.less");
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
        tache.onDelete(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
        tache.onModify(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                                     get(from.getName()))); //
    }

    @Test
    public void compile_tache_to_a_none_existing_directory_must_create_path_if_option() throws IOException {
        Map<String, String> cloneMap = newHashMap();
        cloneMap.putAll(map);
        Tache tache = spy(new LessCompilerTache(cloneMap));
        cloneMap.put(CONFIGURATION_COMPILE_TO, toTemp + "/truth");
        cloneMap.put(CONFIGURATION_MAKE_PATH, "true");

        File from = new File(fromTemp.getAbsolutePath() + "/ghost/" + "oma.less");
        createParentDirs(from);
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                get("ghost/", from.getName()))); //

        assertTrue(new File(toTemp + "/truth/ghost/oma.css").exists());
    }

    @Test
    public void compile_tache_to_a_none_existing_directory_must_NOT_create_path_by_default() throws IOException {
        Map<String, String> cloneMap = newHashMap();
        cloneMap.putAll(map);
        Tache tache = spy(new LessCompilerTache(cloneMap));
        cloneMap.put(CONFIGURATION_COMPILE_TO, toTemp + "/bollocs");
        cloneMap.put(CONFIGURATION_MAKE_PATH, "false");

        File from = new File(fromTemp.getAbsolutePath() + "/ghost/" + "oma.less");
        createParentDirs(from);
        from.createNewFile();

        tache.onCreate(newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY, //
                get("ghost/", from.getName()))); //

        assertFalse(new File(toTemp + "/bollocs/ghost/" + from.getName()).exists());
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void copy_tache_must_not_create_if_copyTo_include_in_path() {
        Map<String, String> bad_map = newHashMap();
        bad_map.put(CONFIGURATION_PATH, toTemp + "/un/deux");
        bad_map.put(CONFIGURATION_COMPILE_TO, toTemp + "/un/");

        new LessCompilerTache(bad_map);
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void copy_tache_must_not_create_if_path_include_in_copyTo() {
        Map<String, String> bad_map = newHashMap();
        bad_map.put(CONFIGURATION_PATH, toTemp + "/un");
        bad_map.put(CONFIGURATION_COMPILE_TO, toTemp + "/un/deux");

        new LessCompilerTache(bad_map);
    }
}
