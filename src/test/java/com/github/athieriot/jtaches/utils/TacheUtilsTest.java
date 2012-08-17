package com.github.athieriot.jtaches.utils;

import com.github.athieriot.jtaches.DummyTache;
import com.github.athieriot.jtaches.Tache;
import com.github.athieriot.jtaches.taches.SysoutTache;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

import static com.github.athieriot.jtaches.utils.TacheUtils.constructionByReflection;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TacheUtilsTest {

    @Test
    public void main_command_must_be_able_to_construct_tache_by_reflection() throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Tache expectedTache = constructionByReflection("com.github.athieriot.jtaches.taches.SysoutTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof SysoutTache);
    }

    @Test
    public void main_command_must_be_able_to_construct_tache_with_simple_constructor() throws InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Tache expectedTache = constructionByReflection("com.github.athieriot.jtaches.DummyTache", null);

        assertNotNull(expectedTache);
        assertTrue(expectedTache instanceof DummyTache);
    }
}
