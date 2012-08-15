package com.github.awesomeless.jtaches.utils;

import com.github.awesomeless.jtaches.Tache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class TacheUtils {

    public static String tacheToString(Tache tache) {
        return tache.getClass().getSimpleName() + " watching on directory: " + tache.getPath();
    }

    public static Tache constructionByReflection(String key, Map<String, String> configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName(key);

        //TODO: Need to support more constructors
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (Tache) constructor.newInstance(configuration);

        } catch(NoSuchMethodException e) {
            return (Tache) clazz.newInstance();
        }
    }
}
