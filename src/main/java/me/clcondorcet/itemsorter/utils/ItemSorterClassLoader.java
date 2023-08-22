package me.clcondorcet.itemsorter.utils;
import me.clcondorcet.itemsorter.ItemSorter;

import java.io.*;

public class ItemSorterClassLoader extends ClassLoader {
    public static ItemSorterClassLoader SQLITE_CLASS_LOADER = new ItemSorterClassLoader("sqlite-jdbc-3.40.1.0.jar");

    private String classPath;

    private ItemSorterClassLoader(String path) {
        this.classPath = path;
    }

    protected Class<?> findClassCustom(String name) throws ClassNotFoundException {
        byte[] classBytes = loadClassBytesCustom(name);
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] loadClassBytesCustom(String name) throws ClassNotFoundException {
        name = name + ".class";
        try {
            InputStream is = ItemSorter.getInstance().getResource(name);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int nextValue = is.read();
            while (-1 != nextValue) {
                stream.write(nextValue);
                nextValue = is.read();
            }

            return stream.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        }
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = loadClassBytes(name);
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] loadClassBytes(String name) throws ClassNotFoundException {
        name = name.replace(".", "/") + ".class";
        try {
            InputStream is = ItemSorter.getInstance().getResource(classPath + "/" + name);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int nextValue = is.read();
            while (-1 != nextValue) {
                stream.write(nextValue);
                nextValue = is.read();
            }

            return stream.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        }
    }
}
