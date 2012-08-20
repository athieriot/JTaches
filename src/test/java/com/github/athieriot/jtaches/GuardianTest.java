package com.github.athieriot.jtaches;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.security.InvalidParameterException;

import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static java.nio.file.Files.createFile;
import static java.nio.file.Paths.get;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class GuardianTest {

    private Path temporary_directory;

    @BeforeTest
    public void setup() throws IOException {
        temporary_directory = Files.createTempDirectory("_awesomeless");
    }

    @Test
    public void a_guardian_must_be_create() throws IOException {
        assertNotNull(new Guardian());
    }

    @Test
    public void a_guardian_can_be_create_by_builder() {
        assertNotNull(Guardian.create());
    }

    @Test
    public void a_guardian_must_accept_tache_registering() throws IOException {
        WatchKey key = Guardian.create().registerTache(new DummyTache(temporary_directory));

        assertNotNull(key);
        assertTrue(key.isValid());
    }

    @Test
    public void a_guardian_must_accept_null_tache_registering() throws IOException {
        WatchKey key = Guardian.create().registerTache(null);

        assertNull(key);
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void a_guardian_must_not_accept_null_path() throws IOException {
        Guardian.create().registerTache(new DummyTache());
    }

    @Test(timeOut = 1000)
    public void a_guardian_must_do_nothing_if_no_tache() throws IOException, InterruptedException {
        Guardian guardian = spy(Guardian.create());
        guardian.registerTache(null);

        guardian.watch();

        verify(guardian, never()).onEvent(any(WatchEvent.class));
    }

    @Test(timeOut = 2000)
    public void a_guardian_must_watch_true_file_creation() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());

        Tache cancelling = spy(new Tache() {
            public Path getPath() {return temporary_directory;}
            public void onCreate(WatchEvent<?> event) {
                try {
                    guardian.cancel();
                } catch (IOException e) {System.out.println("Cancelling guardian impossible"); e.printStackTrace();}
            }
            public void onDelete(WatchEvent<?> event) {}
            public void onModify(WatchEvent<?> event) {}
        });
        guardian.registerTache(cancelling);

        Thread creatorThread = new Thread(
            new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            createFile(get(temporary_directory.toString(), "areyouwatchingtome"));
                        } catch (IOException e) {}
                    }
                }
            }
        );
        creatorThread.start();

        guardian.watch();

        verify(cancelling).onCreate(any(WatchEvent.class));
        verify(guardian, atLeastOnce()).cancel();
    }

    @Test
    public void a_guardian_must_fire_onCreate_events() throws IOException {
        Guardian guardian = Guardian.create();
        Tache dummy = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> createEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE);

        guardian.registerTache(dummy);
        guardian.onEvent(createEvent);

        verify(dummy).onCreate(createEvent);
        verify(dummy, never()).onDelete(createEvent);
        verify(dummy, never()).onModify(createEvent);
    }

    @Test
    public void a_guardian_must_fire_onCreate_events_more_than_on_if_more_taches() throws IOException {
        Guardian guardian = Guardian.create();
        Tache dummy = spy(new DummyTache(temporary_directory));
        Tache dummy2 = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> createEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE);

        guardian.registerTache(dummy);
        guardian.registerTache(dummy2);
        guardian.onEvent(createEvent);

        //TODO: Tests deserve a refactor
        verify(dummy).onCreate(createEvent);
        verify(dummy, never()).onDelete(createEvent);
        verify(dummy, never()).onModify(createEvent);

        verify(dummy2).onCreate(createEvent);
        verify(dummy2, never()).onDelete(createEvent);
        verify(dummy2, never()).onModify(createEvent);
    }

    @Test
    public void a_guardian_must_fire_onDelete_events() throws IOException {
        Guardian guardian = spy(Guardian.create());
        Tache dummy = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> deleteEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE);

        guardian.registerTache(dummy);
        guardian.onEvent(deleteEvent);

        verify(dummy, never()).onCreate(deleteEvent);
        verify(dummy).onDelete(deleteEvent);
        verify(dummy, never()).onModify(deleteEvent);
    }

    @Test
    public void a_guardian_must_fire_onModify_events() throws IOException {
        Guardian guardian = spy(Guardian.create());
        Tache dummy = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> modifyEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY);

        guardian.registerTache(dummy);
        guardian.onEvent(modifyEvent);

        verify(dummy, never()).onCreate(modifyEvent);
        verify(dummy, never()).onDelete(modifyEvent);
        verify(dummy).onModify(modifyEvent);
    }
}
