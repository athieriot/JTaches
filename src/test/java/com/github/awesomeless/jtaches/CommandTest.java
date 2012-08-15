package com.github.awesomeless.jtaches;

import com.github.awesomeless.jtaches.taches.SysoutTache;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import static java.nio.file.Paths.get;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CommandTest {

    @Test(timeOut = 2000)
    public void main_command_must_at_least_register_taches() throws Exception {
        String[] argv = { "--registerOnly" };
        Command.executeMain(argv);
    }

    @Test
    public void main_command_must_be_able_to_construct_tache_by_reflection() throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Tache expectedTache = Command.constructionByReflection("com.github.awesomeless.jtaches.taches.SysoutTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof SysoutTache);
    }

    @Test
    public void main_command_must_be_able_to_construct_tache_with_simple_constructor() throws InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Tache expectedTache = Command.constructionByReflection("com.github.awesomeless.jtaches.DummyTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof DummyTache);
    }
}
