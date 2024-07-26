package me.vlink102.melomod.world;

import java.util.Iterator;

public class IteratorUtils {

    public static <T> T getOnlyElement(Iterator<T> it, T defaultValue) {
        if (!it.hasNext()) return defaultValue;
        T ret = it.next();
        if (it.hasNext()) return defaultValue;
        return ret;
    }

    public static <T> T getOnlyElement(Iterable<T> it, T defaultValue) {
        return getOnlyElement(it.iterator(), defaultValue);
    }
}