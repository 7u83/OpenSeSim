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
package sesim;

import gui.Globals;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SeSimClassLoader {

    protected ArrayList<String> default_pathlist;

    /**
     * Set the path list where to search for traders
     *
     * @param pathlist List of paths
     */
    public final void setDefaultPathList(ArrayList<String> pathlist) {
        this.default_pathlist = pathlist;

    }

    /**
     * Create a SeSimClassLoader object with an empty default path
     */
    public SeSimClassLoader() {
        this(new ArrayList<String>());
    }

    /**
     * Create a SeSimClassLoader object with fiven default path
     *
     * @param pathlist Default path to search classes for
     */
    public SeSimClassLoader(ArrayList<String> pathlist) {
        setDefaultPathList(pathlist);
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
     * @return the instance, null if not successfull
     */
    public Object newInstance(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }

    /**
     * Check if a given class provides an certain interface and also if the
     * class is not abstract, so it could be instanciated.
     *
     * @param cls Class to check
     * @param iface Interface which the class should provide
     * @return true if it is an instance of insclass, otherwise false
     */
    public boolean isInstance(Class<?> cls, Class<?> iface) {

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

    private Class<?> loadClass(String directory, String class_name, Class<?> iface) {

        if (class_name == null) {
            return null;
        }

        URL url = null;
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

            if (iface != null){
                if (!isInstance(cls, iface)) {
                    return null;
                }
            }
            
            if (newInstance(cls) == null) {
                return null;
            }

            return cls;

        } catch (ClassNotFoundException ex) {
            return null;
        }

    }

    /**
     *
     * @param additional_pathlist
     * @param iface
     * @return 
     */
    public ArrayList<Class<?>> getInstalledClasses(ArrayList<String> additional_pathlist, Class<?> iface) {

        ArrayList<Class<?>> result = new ArrayList<>();
        
        ArrayList<String> pathlist = new ArrayList<>();
        pathlist.addAll(default_pathlist);
        pathlist.addAll(additional_pathlist);

        for (String path : pathlist) {

            ArrayList<File> files = listFiles(path);

            for (File file : files) {

                String fn = file.toString();

                if (fn.toLowerCase().endsWith(".class")) {

                    String class_name;
                    class_name = fn.substring(path.length());
                    class_name = class_name.substring(1, class_name.length() - 6).replace('/', '.');

                    Class<?> c = loadClass(path, class_name, iface);
                    if (null == c) {
                        continue;
                    }
                    result.add(c);
                    //System.out.printf("Here is an instance for %s: %s\n", iface.getName(), class_name);

                }

                if (fn.toLowerCase().endsWith(".jar")) {
                    JarInputStream jarstream = null;
                    try {
                        File jarfile = new File(fn);
                        jarstream = new JarInputStream(new FileInputStream(jarfile));
                        JarEntry jarentry;

                        while ((jarentry = jarstream.getNextJarEntry()) != null) {

                            String class_name = jarentry.getName();

                            System.out.printf("Looking into jar: %s - %s\n", path, class_name);

                            if (class_name.endsWith(".class")) {

                                class_name = class_name.substring(0, class_name.length() - 6).replace('/', '.');

                                Class<?> c = loadClass(path, class_name, iface);
                                if (null == c) {
                                    continue;
                                }
                                result.add(c);
                                //System.out.printf("Here is an instance for %s: %s\n", iface.getName(), class_name);

                            }
                        }
                    } catch (IOException ex) {

                    } finally {
                        try {
                            jarstream.close();
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }
            //      System.exit(0);
        }
        return result;
    }

}
