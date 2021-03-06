package com.github.athieriot.jtaches;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.concurrent.Future;

import static com.github.athieriot.jtaches.utils.TestUtils.newOverFlowEvent;
import static com.github.athieriot.jtaches.utils.TestUtils.newWatchEvent;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

public class GuardianTest {

    private Path temporary_directory;
    private Path another_temporary_directory;

    @BeforeTest
    public void setup() throws IOException {
        temporary_directory = Files.createTempDirectory("_awesomeless");
        another_temporary_directory = Files.createTempDirectory("_awesomemore");
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
        Guardian.create().registerTache(new DummyTache(temporary_directory));
    }

    @Test
    public void a_guardian_must_accept_null_tache_registering() throws IOException {
        Guardian.create().registerTache(null);
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

        verify(guardian, never()).dispatch(any(WatchEvent.class));
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_watch_true_file_creation() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());
        Tache testedTache = spy(newCreateTache(guardian, temporary_directory));

        guardian.registerTache(testedTache);

        launchThreadedCreation(get(temporary_directory.toString(), "areyouwatchingtome"));

        guardian.watch(3000L);

        verify(testedTache).onCreate(any(WatchEvent.class));
        verify(guardian, atLeastOnce()).close();
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_watch_true_sub_file_creation() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());
        Tache testedTache = spy(newCreateTache(guardian, temporary_directory));

        createDirectories(get(temporary_directory.toString(), "src", "main"));
        guardian.registerTache(testedTache, true);

        launchThreadedCreation(get(temporary_directory.toString(), "src", "main", "areyouwatchingtome"));

        guardian.watch(3000L);

        ArgumentCaptor<WatchEvent> argument = ArgumentCaptor.forClass(WatchEvent.class);
        verify(testedTache).onCreate(argument.capture());
        verify(guardian, atLeastOnce()).close();

        assertEquals(get("src/main/areyouwatchingtome"), argument.getValue().context());
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_not_watch_sub_file_creation_if_no_recursive() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());
        Tache testedTache = spy(newCreateTache(guardian, temporary_directory));

        createDirectories(get(temporary_directory.toString(), "src", "main"));
        guardian.registerTache(testedTache, false);

        launchThreadedCreation(get(temporary_directory.toString(), "src", "main", "hopeyournotwatchingtome"));

        guardian.watch(3000L);

        verify(testedTache, never()).onCreate(any(WatchEvent.class));
        verify(guardian, never()).close();
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_not_watch_sub_file_creation_if_not_related_task() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());
        Tache testedTache = spy(newCreateTache(guardian, temporary_directory));
        Tache notExpectedTache = spy(newCreateTache(guardian, another_temporary_directory));

        createDirectories(get(temporary_directory.toString(), "src", "main"));
        createDirectories(get(another_temporary_directory.toString(), "src", "main"));

        guardian.registerTache(testedTache, true);
        guardian.registerTache(notExpectedTache, true);

        launchThreadedCreation(get(temporary_directory.toString(), "src", "main", "hopeyournotwatchingtomybuddy"));

        guardian.watch(3000L);

        verify(testedTache).onCreate(any(WatchEvent.class));
        verify(notExpectedTache, never()).onCreate(any(WatchEvent.class));
        verify(guardian, atLeastOnce()).close();
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_watch_sub_file_creation_for_two_different_tasks() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());
        Tache testedTache = spy(newCreateTache(guardian, temporary_directory));
        Tache expectedTache = spy(newCreateTache(guardian, temporary_directory));

        createDirectories(get(temporary_directory.toString(), "src", "main"));

        guardian.registerTache(testedTache, true);
        guardian.registerTache(expectedTache, true);

        launchThreadedCreation(get(temporary_directory.toString(), "src", "main", "hopeyouarewatchingforbothofus"));

        guardian.watch(3000L);

        verify(testedTache).onCreate(any(WatchEvent.class));
        verify(expectedTache).onCreate(any(WatchEvent.class));
        verify(guardian, atLeastOnce()).close();
    }

    @Test(timeOut = 5000)
    public void a_guardian_must_not_stop_if_a_sub_directory_is_deleted() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());

        Tache testedTache = spy(new Tache() {
            public Path getPath() {return temporary_directory;}
            public Collection<String> getExcludes() {return emptyList();}
            public void onCreate(WatchEvent<?> event) {}
            public void onDelete(WatchEvent<?> event) {
                try {
                    guardian.close();
                } catch (IOException e) {System.out.println("Cancelling guardian impossible"); e.printStackTrace();}
            }
            public void onModify(WatchEvent<?> event) {}
        });
        createDirectories(get(temporary_directory.toString(), "src", "main", "iamnotyourpupet"));

        guardian.registerTache(testedTache, true);

        launchThreadedDeletion(get(temporary_directory.toString(), "src", "main", "iamnotyourpupet"));

        guardian.watch(3000L);

        verify(testedTache).onDelete(any(WatchEvent.class));
        //Not quit clear really. Related to the "blocking" nature of the guardian.
        verify(guardian).close();
    }

    //@Test(timeOut = 5000)
    //TODO: Failing on Travis. Need to find out why
    public void a_guardian_must_stop_if_a_root_directory_is_deleted() throws IOException, InterruptedException {
        final Guardian guardian = spy(Guardian.create());

        createDirectories(get(temporary_directory.toString(), "src", "backtotheprimitives"));
        Tache testedTache = spy(new Tache() {
            public Collection<String> getExcludes() {return emptyList();}
            public Path getPath() {return get(temporary_directory.toString(), "src", "backtotheprimitives");}
            public void onCreate(WatchEvent<?> event) {}
            public void onDelete(WatchEvent<?> event) {}
            public void onModify(WatchEvent<?> event) {}
        });

        guardian.registerTache(testedTache, false);

        launchThreadedDeletion(get(temporary_directory.toString(), "src", "backtotheprimitives"));

        guardian.watch(3000L);

        verify(testedTache, never()).onDelete(any(WatchEvent.class));
        verify(guardian).close();
    }

    @Test
    public void a_guardian_must_fire_onCreate_events() throws IOException {
        Guardian guardian = Guardian.create();
        Tache dummy = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> createEvent = newWatchEvent(ENTRY_CREATE);

        guardian.registerTache(dummy);
        guardian.dispatch(createEvent);

        verify(dummy).onCreate(createEvent);
        verify(dummy, never()).onDelete(createEvent);
        verify(dummy, never()).onModify(createEvent);
    }

    @Test
    public void a_guardian_must_fire_onCreate_events_more_than_on_if_more_taches() throws IOException {
        Guardian guardian = Guardian.create();
        Tache dummy = spy(new DummyTache(temporary_directory));
        Tache dummy2 = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> createEvent = newWatchEvent(ENTRY_CREATE);

        guardian.registerTache(dummy);
        guardian.registerTache(dummy2);
        guardian.dispatch(createEvent);

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

        WatchEvent<Path> deleteEvent = newWatchEvent(ENTRY_DELETE);

        guardian.registerTache(dummy);
        guardian.dispatch(deleteEvent);

        verify(dummy, never()).onCreate(deleteEvent);
        verify(dummy).onDelete(deleteEvent);
        verify(dummy, never()).onModify(deleteEvent);
    }

    @Test
    public void a_guardian_must_fire_onModify_events() throws IOException {
        Guardian guardian = spy(Guardian.create());
        Tache dummy = spy(new DummyTache(temporary_directory));

        WatchEvent<Path> modifyEvent = newWatchEvent(ENTRY_MODIFY);

        guardian.registerTache(dummy);
        guardian.dispatch(modifyEvent);

        verify(dummy, never()).onCreate(modifyEvent);
        verify(dummy, never()).onDelete(modifyEvent);
        verify(dummy).onModify(modifyEvent);
    }

    @Test
    public void a_guardian_must_fire_overflow_events() throws IOException {
        Guardian guardian = spy(Guardian.create());
        Tache dummy = new DummyTache(temporary_directory);
        WatchEvent overFlowEvent = newOverFlowEvent();

        guardian.registerTache(dummy);
        guardian.dispatch(overFlowEvent);

        verify(guardian).dealWithOverFlow(overFlowEvent);
    }

    @Test
    public void decorate_an_event_should_relativize_the_context_if_key_present() throws IOException {
        Guardian guardian = spy(Guardian.create());
        WatchingStore<Tache, Path> testStore = new WatchingStore<>();
        doReturn(testStore).when(guardian).getGlobalStorage();

        Path relativePath = get("src/main");
        Path globalPath = get("src");
        guardian.registerDirectory(relativePath, globalPath, new DummyTache());

        WatchEvent<?> event = (WatchEvent<?>) newWatchEvent(ENTRY_CREATE);

        Path expectedContext = get("main", event.context().toString());
        WatchEvent<?> decoratedEvent = guardian.decoratedEvent(event, testStore.retrieveWatchKeys().iterator().next());

        assertEquals(expectedContext, decoratedEvent.context());
    }

    private Tache newCreateTache(final Guardian guardian, final Path path) {
        return new Tache() {
            public Path getPath() {return path;}
            public Collection<String> getExcludes() {return emptyList();}
            public void onCreate(WatchEvent<?> event) {
                try {
                    guardian.close();
                } catch (IOException e) {System.out.println("Cancelling guardian impossible"); e.printStackTrace();}
            }
            public void onDelete(WatchEvent<?> event) {}
            public void onModify(WatchEvent<?> event) {}
        };
    }

    private Tache newDeleteTache(final Guardian guardian, final Path path) {
        return new Tache() {
            public Path getPath() {return path;}
            public Collection<String> getExcludes() {return emptyList();}
            public void onCreate(WatchEvent<?> event) {}
            public void onDelete(WatchEvent<?> event) {
                try {
                    guardian.close();
                } catch (IOException e) {System.out.println("Cancelling guardian impossible"); e.printStackTrace();}
            }
            public void onModify(WatchEvent<?> event) {}
        };
    }

    private Future launchThreadedCreation(final Path path) {
        return newSingleThreadExecutor().submit(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        createFile(path);
                    } catch (IOException e) {}
                }
            }
        });
    }

    private Future launchThreadedDeletion(final Path path) {
        return newSingleThreadExecutor().submit(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        delete(path);
                    } catch (IOException e) {}
                }
            }
        });
    }
}
