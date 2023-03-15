package me.clcondorcet.itemsorter.caching;

import java.util.concurrent.TimeUnit;

public class CachedData<T> {

    private final long cachedTime;
    private final TimeUnit cachedTimeUnit;
    private final DataGetter<T> getter;

    private T data;
    private long timeEnd;

    public CachedData (long cachedTime, TimeUnit cachedTimeUnit, DataGetter<T> getter) {
        this.cachedTime = cachedTime;
        this.cachedTimeUnit = cachedTimeUnit;
        this.getter = getter;
    }

    public CachedData (long cachedTime, TimeUnit cachedTimeUnit, T data) {
        this.cachedTime = cachedTime;
        this.cachedTimeUnit = cachedTimeUnit;
        this.getter = null;
        this.data = data;
        timeEnd = System.currentTimeMillis() + cachedTimeUnit.toMillis(cachedTime);
    }

    public T get() {
        long now = System.currentTimeMillis();
        if (now > timeEnd) {
            if (getter != null) {
                try {
                    data = getter.get();
                    timeEnd = now + cachedTimeUnit.toMillis(cachedTime);
                } catch (Throwable ignored) {}
            } else {
                data = null;
                timeEnd = Long.MAX_VALUE;
            }
        }
        return data;
    }

    public void clear() {
        data = null;
        timeEnd = 0;
    }

    public void set(T data) {
        this.data = data;
        timeEnd = System.currentTimeMillis() + cachedTimeUnit.toMillis(cachedTime);
    }

/*    @Override
    public boolean equals(Object obj) {
        if (get() == null || obj == null)
            return false;
        if (obj instanceof CachedData)
            return data.equals(((CachedData<?>) obj).get());
        if (obj.getClass().isInstance(data.getClass())) {
            return data.equals(obj);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        if (get() != null) {
            return data.hashCode();
        }
        return super.hashCode();
    }*/
}
