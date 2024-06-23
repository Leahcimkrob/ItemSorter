package me.clcondorcet.itemsorter.caching;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CachedMap<K, V> {

    private final long cachedTime;
    private final TimeUnit cachedTimeUnit;

    private HashMap<K, CachedData<V>> map = new HashMap<>();

    public CachedMap(long cachedTime, TimeUnit cachedTimeUnit) {
        this.cachedTime = cachedTime;
        this.cachedTimeUnit = cachedTimeUnit;
    }

    public V put (K key, V value) {
        CachedData<V> val = map.put(key, new CachedData<>(cachedTime, cachedTimeUnit, value));
        if (val != null) return val.get();
        return null;
    }

    public V remove (K key) {
        CachedData<V> val = map.remove(key);
        if (val != null) return val.get();
        return null;
    }

    public V get (K key) {
        CachedData<V> val = map.get(key);
        if (val != null) {
            V data = val.get();
            if (data == null) {
                map.remove(key);
            }
            return data;
        }
        return null;
    }

    public boolean containsKey(K key) {
        CachedData<V> d = map.get(key);
        if (d != null) {
            return d.get() != null;
        }
        return false;
    }
}
