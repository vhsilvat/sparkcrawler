package com.victor.sparkcrawler.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentLinkedSet<E> extends AbstractSet<E> implements Set<E>, Serializable {

    private final transient ConcurrentHashMap<E, Boolean> map;

    // Dummy value to associate with an Object in the backing Map
    private static final Boolean PRESENT = Boolean.TRUE;

    public ConcurrentLinkedSet() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }
}
