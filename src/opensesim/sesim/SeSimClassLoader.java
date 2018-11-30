/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package opensesim.sesim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 * @param <T>
 */
public class SeSimClassLoader<T> {

    protected ArrayList<String> default_pathlist;
    private ArrayList<Class<T>> cache;
    final Class<T> class_type;

    /**
     * Create a SeSimClassLoader object with an empty default path
     *
     * @param class_type
     */
    public SeSimClassLoader(Class<T> class_type) {
        this(class_type, new ArrayList<>());
    }

    /**
     * Create a SeSimClassLoader object with given default path
     *
     * @param class_type
     * @param pathlist Default path to search classes for
     */
    public SeSimClassLoader(Class<T> class_type, ArrayList<String> pathlist) {
        this.class_type = class_type;
        setDefaultPathList(pathlist);
    }

    /**
     * Set the path list where to search for classes
     *
     * @param pathlist List of paths
     */
    public final void setDefaultPathList(ArrayList<String> pathlist) {
        this.default_pathlist = pathlist;

    }

    /**
     * Get a list of all files in a given directory and its sub-directories.
     *
     * @param path Directory to list
     * @return List of files
     */
    public ArrayList<File> listFiles(String path) {

        ArrayList<File> files = new ArrayList<>();

        File fp = new File(path);
        if (!fp.isDirectory()) {
            files.add(fp);
            return files;
        }

        File[] fList = new File(path).listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(listFiles(file.getAbsolutePath()));
            }
        }
        return files;
    }

    /**
     * Create a new instance of specified class
     *
     * @param cls Class to create an instance of
     * @return the instance, null if not successful
     */
    public Object newInstance(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }

    /**
     * Check if a given class provides a certain interface and also if the class
     * is not abstract, so it could be instantiated.
     *
     * @param cls Class to check
     * @param iface Interface which the class should provide
     * @return true if it is an instance of insclass, otherwise false
     */
    public boolean hasInterface(Class<?> cls, Class<?> iface) {

        if (Modifier.isAbstract(cls.getModifiers())) {
            return false;
        }

        do {
           
            for (Class<?> i : cls.getInterfaces()) {
                if (i == iface) {
                    return true;
                }
            }

        } while ((cls = cls.getSuperclass()) != null);
        return false;
    }

    private Class<T> loadClass(String directory, String class_name) {

        if (class_name == null) {
            return null;
        }

        URL url;
        try {
            url = new File(directory).toURI().toURL();

        } catch (MalformedURLException ex) {
            return null;
        }
        URL[] urls = new URL[]{url};

        ClassLoader cl;
        cl = new URLClassLoader(urls);

        try {
            Class<?> cls;
            cls = cl.loadClass(class_name);
            if (cls == null) {
                return null;
            }

            if (class_type != null) {
                if (!hasInterface(cls, class_type)) {
                    return null;
                }
            }

/*            if (newInstance(cls) == null) {
                return null;
            }
*/
            return (Class<T>) cls;

        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoClassDefFoundError ex){
            return null;
        }

    }

    /**
     *
     * @param pathlist
     * @return
     */
    public ArrayList<Class<T>> getInstalledClasses0(ArrayList<String> pathlist) {

        ArrayList<Class<T>> result = new ArrayList<>();

        for (String path : pathlist) {

            ArrayList<File> files = listFiles(path);

            for (File file : files) {

                String fn = file.toString();

                if (fn.toLowerCase().endsWith(".class")) {

                    String class_name;
                    class_name = fn.substring(path.length());
                    // in case we are under Windows, replace \ width /
                    class_name = class_name.replace("\\", "/");
                    class_name = class_name.substring(1, class_name.length() - 6).replace('/', '.');

                    Class<T> c = loadClass(path, class_name);
                    if (null == c) {
                        continue;
                    }
                    result.add(c);
                }

                if (fn.toLowerCase().endsWith(".jar")) {
                    JarInputStream jarstream = null;
                    try {
                        File jarfile = new File(fn);
                        jarstream = new JarInputStream(new FileInputStream(jarfile));
                        JarEntry jarentry;

                        while ((jarentry = jarstream.getNextJarEntry()) != null) {

                            String class_name = jarentry.getName();

                            if (class_name.endsWith(".class")) {

                                class_name = class_name.substring(0, class_name.length() - 6).replace('/', '.');
                                Class<T> c = loadClass(path, class_name);
                                if (null == c) {
                                    continue;
                                }
                                result.add(c);
                            }
                        }
                    } catch (IOException ex) {

                    } finally {
                        try {
                            if (jarstream != null) {
                                jarstream.close();
                            }
                        } catch (IOException ex) {
                       //     java.util.logging.Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }

        }
        return result;
    }

    /**
     * Get a list of all traders found in class path
     *
     * @param additional_pathlist
     * @return List of installed Classes
     */
    public ArrayList<Class<T>> getInstalledClasses(ArrayList<String> additional_pathlist ) {

        if (cache != null) {
            return cache;
        }

        ArrayList<String> pathlist;
        pathlist = new ArrayList<>();

        pathlist.addAll(default_pathlist);
        pathlist.addAll(additional_pathlist);

        cache = getInstalledClasses0(pathlist);

        return cache;

    }
    
    public ArrayList<Class<T>> getInstalledClasses(){
        return getInstalledClasses(new ArrayList<>());
    }
}
