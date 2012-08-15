package com.github.awesomeless.jtaches.utils;

import com.github.awesomeless.jtaches.DummyTache;
import com.github.awesomeless.jtaches.Tache;
import com.github.awesomeless.jtaches.taches.SysoutTache;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

import static com.github.awesomeless.jtaches.utils.TacheUtils.constructionByReflection;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TacheUtilsTest {

    @Test
    public void main_command_must_be_able_to_construct_tache_by_reflection() throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Tache expectedTache = constructionByReflection("com.github.awesomeless.jtaches.taches.SysoutTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof SysoutTache);
    }

    @Test
    public void main_command_must_be_able_to_construct_tache_with_simple_constructor() throws InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Tache expectedTache = constructionByReflection("com.github.awesomeless.jtaches.DummyTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof DummyTache);
    }
}
