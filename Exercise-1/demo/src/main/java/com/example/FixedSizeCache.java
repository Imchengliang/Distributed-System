package com.example;

import java.util.Map;
import java.util.LinkedHashMap;

public class FixedSizeCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public FixedSizeCache(int size) {
        super(size, 1F, true); // No buffer, load factor of 1.0, and access-order enabled
        this.maxSize = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize; // Evict the eldest entry when the cache exceeds the max size
    }
}
