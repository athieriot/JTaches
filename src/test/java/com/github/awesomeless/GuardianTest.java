package com.github.awesomeless;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.Files.createFile;
import static java.nio.file.Paths.get;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class GuardianTest {

    @Test
    public void a_guardian_must_be_create() throws IOException {
        assertNotNull(new Guardian());
    }

    @Test
    public void a_guardian_can_be_create_by_builder() {
        assertNotNull(Guardian.create());
    }

    @Test
    public void a_guardian_must_accept_path_registering() throws IOException {
        WatchKey key = Guardian.create().register(get("."));

        assertNotNull(key);
        assertTrue(key.isValid());
    }

    @Test(timeOut = 2000)
    public void a_guardian_must_watch_true_file_creation() throws IOException {
        final Guardian guardian = spy(Guardian.create());
        final Path temp = Files.createTempDirectory("_awesomeless");

        Thread creatorThread = new Thread(
            new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                        createFile(get(temp.toString(), "areyouwatchingtome"));

                        guardian.cancel();
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
        creatorThread.start();

        guardian.register(temp);
        guardian.watch();

        verify(guardian).onCreate(any(WatchEvent.class));
        verify(guardian, atLeastOnce()).cancel();
    }

    @Test
    public void a_guardian_must_fire_onCreate_events() {
        Guardian guardian = spy(Guardian.create());
        WatchEvent<Path> createEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_CREATE);

        guardian.onEvent(createEvent);

        verify(guardian).onCreate(createEvent);
        verify(guardian, never()).onDelete(createEvent);
        verify(guardian, never()).onModify(createEvent);
    }

    @Test
    public void a_guardian_must_fire_onDelete_events() {
        Guardian guardian = spy(Guardian.create());
        WatchEvent<Path> deleteEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_DELETE);

        guardian.onEvent(deleteEvent);

        verify(guardian, never()).onCreate(deleteEvent);
        verify(guardian).onDelete(deleteEvent);
        verify(guardian, never()).onModify(deleteEvent);
    }

    @Test
    public void a_guardian_must_fire_onModify_events() {
        Guardian guardian = spy(Guardian.create());
        WatchEvent<Path> modifyEvent = newWatchEvent(StandardWatchEventKinds.ENTRY_MODIFY);

        guardian.onEvent(modifyEvent);

        verify(guardian, never()).onCreate(modifyEvent);
        verify(guardian, never()).onDelete(modifyEvent);
        verify(guardian).onModify(modifyEvent);
    }

    private WatchEvent<Path> newWatchEvent(final WatchEvent.Kind<Path> kind) {
        return new WatchEvent<Path>() {
            @Override
            public Kind<Path> kind() {return kind;}
            @Override
            public int count() {return 0;}
            @Override
            public Path context() {
                try {return Files.createTempFile("noevent", "");
                } catch (IOException e) {return null;}
            }
        };
    }
}
