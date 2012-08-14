package com.github.awesomeless;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.WatchKey;

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
        WatchKey key = Guardian.create().register(Paths.get("."));

        assertNotNull(key);
        assertTrue(key.isValid());
    }
}
