package com.github.athieriot.jtaches.taches;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.github.athieriot.jtaches.taches.ScriptTache.CONFIGURATION_SCRIPT;
import static com.github.athieriot.jtaches.taches.ScriptTache.CONFIGURATION_WORKING_DIRECTORY;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Files.createTempDir;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ScriptTacheTest {

    private final Map<String,String> map = newHashMap();
    private final File path = createTempDir();

    @BeforeTest
    public void setup() {
        map.put(CONFIGURATION_SCRIPT, "ls");
        map.put(CONFIGURATION_PATH, path.getAbsolutePath());
    }

    @Test
    public void script_tache_should_replace_things_from_script_line() throws IOException {
        map.put(CONFIGURATION_SCRIPT, "echo <path> <filename> <shortname> <ext> <event>");

        File file = new File("/tmp/test.txt");
        file.createNewFile();

        String resultScript = new ScriptTache(map).manufacturingScript(file, "EVENT_CREATE");

        assertEquals("echo /tmp test.txt test txt EVENT_CREATE", resultScript);
    }

    @Test
    public void script_tache_must_execute_a_script() throws IOException, InterruptedException {
        map.put(CONFIGURATION_SCRIPT, "mv <path>/<filename> <path>/<event>");
        map.put(CONFIGURATION_WORKING_DIRECTORY, ".");
        File file = new File(path.toString() + "/test");
        file.createNewFile();

        ScriptTache tache = new ScriptTache(map);

        tache.executeScript(newWatchEvent(ENTRY_MODIFY, get(file.getName())));

        Thread.sleep(200);
        assertTrue(new File(path.toString() + "/ENTRY_MODIFY").exists());
    }

    @Test
    public void script_tache_must_not_block_if_bad_command() {
        map.put(CONFIGURATION_SCRIPT, "youbastard");
        ScriptTache tache = new ScriptTache(map);

        tache.executeScript(newWatchEvent(ENTRY_MODIFY, get(".")));
    }

    @Test
    public void script_must_trigger_onCreate() {
        ScriptTache tache = spy(new ScriptTache(map));
        WatchEvent<Path> watchEvent = newWatchEvent(ENTRY_CREATE);
        doNothing().when(tache).executeScript(any(WatchEvent.class));

        tache.onCreate(watchEvent);

        verify(tache).executeScript(watchEvent);
    }

    @Test
    public void script_must_trigger_onDelete() {
        ScriptTache tache = spy(new ScriptTache(map));
        WatchEvent<Path> watchEvent = newWatchEvent(ENTRY_DELETE);
        doNothing().when(tache).executeScript(any(WatchEvent.class));

        tache.onDelete(watchEvent);

        verify(tache).executeScript(watchEvent);
    }

    @Test
    public void script_must_trigger_onModify() {
        ScriptTache tache = spy(new ScriptTache(map));
        WatchEvent<Path> watchEvent = newWatchEvent(ENTRY_DELETE);
        doNothing().when(tache).executeScript(any(WatchEvent.class));

        tache.onModify(watchEvent);

        verify(tache).executeScript(watchEvent);
    }
}
