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
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AutoTraderLoader {

    /**
     * Check if a given class can instaciated as AutoTrader.
     *
     * @param cls Class to check
     * @return true if it is an AutoTrader, otherwise false
     */
    public boolean isAutoTrader(Class<?> cls) {
        if (Modifier.isAbstract(cls.getModifiers())) {
            return false;
        }

        do {
            for (Class<?> i : cls.getInterfaces()) {
                if (i.equals(AutoTraderInterface.class)) {
                    return true;
                }
            }

        } while ((cls = cls.getSuperclass()) != null);
        return false;
    }

    Class<AutoTraderInterface> loadAutoTraderClass(String filename, String classname) {

        String clnam = classname.substring(1, classname.length() - 6).replace('/', '.');
        File f = new File(filename);

        URL url = null;
        try {
            url = f.toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        URL[] urls = new URL[]{url};

        // Create a new class loader with the directory
        ClassLoader cl = new URLClassLoader(urls);

        try {
            Class<?> cls = cl.loadClass(clnam);
            System.out.printf("Check Class: %s\n", cls.getCanonicalName());
            if (isAutoTrader(cls)) {
                return (Class<AutoTraderInterface>) cls;

            }
        } catch (ClassNotFoundException ex) {
            // something wnet wrong, but we ignore it
        }
        return null;

    }

    ArrayList<Class<AutoTraderInterface>> traders_cache = null;

    public ArrayList<Class<AutoTraderInterface>> getTraders() {

        if (traders_cache != null) {
            return traders_cache;
        }

        int curlen = 0;

        ArrayList<Class<AutoTraderInterface>> traders;
        traders = new ArrayList<>();

        for (String classpathEntry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {

            Consumer<? super Path> pf = new Consumer() {
                @Override
                public void accept(Object t) {
                    String fn = ((Path) t).toString();
                    if (fn.toLowerCase().endsWith(".class")) {
                        Class<AutoTraderInterface> cls = loadAutoTraderClass(fn, fn.substring(classpathEntry.length()));
                        if (cls == null) {
                            return;
                        }
                        traders.add(cls);
                    }
                    if (fn.toLowerCase().endsWith(".jar")) {
                        JarInputStream is = null;
                        try {
                            File jar = new File(fn);
                            is = new JarInputStream(new FileInputStream(jar));
                            JarEntry entry;
                            while ((entry = is.getNextJarEntry()) != null) {
                                if (entry.getName().endsWith(".class")) {

                                    //      System.out.printf("Entry: %s\n", entry.getDisplayName());
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                is.close();
                            } catch (IOException ex) {
                                Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                }

            };

            try {
                Files.walk(Paths.get(classpathEntry))
                        .filter(Files::isRegularFile)
                        .forEach(pf);
            } catch (IOException ex) {
                Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        traders_cache = traders;
        return traders;

    }

    public ArrayList<String> getDefaultStrategyNames(boolean devel) {
        ArrayList<Class<AutoTraderInterface>> trclasses;
        trclasses = this.getTraders();
        ArrayList<String> ret = new ArrayList<>();
        trclasses = getTraders();

        for (int i = 0; i < trclasses.size(); i++) {
            try {

                AutoTraderInterface ac = trclasses.get(i).newInstance();
                if (ac.getDevelStatus() && devel == false) {
                    continue;
                }
                ret.add(ac.getClass().getCanonicalName());
            } catch (Exception e) {
                System.out.printf("Can't load \n");

            }

        }

        return ret;
    }

    public ArrayList<String> getDefaultStrategyNames() {
        return this.getDefaultStrategyNames(true);
    }

    public AutoTraderInterface getStrategyBase(String name) {
        ArrayList<Class<AutoTraderInterface>> traders = this.getTraders();
        for (int i = 0; i < traders.size(); i++) {
            try {
                AutoTraderInterface ac = traders.get(i).newInstance();

                System.out.printf("Looking for in %s == %s\n", ac.getClass().getCanonicalName(), name);

                if (ac.getClass().getCanonicalName().equals(name)) {
                    return ac;

                    // if (ac.getDisplayName().equals(name)) {
                    //     return ac;}
                }
            } catch (Exception ex) {
            }
        }

        return null;
    }

    public ArrayList getTraders(String dir) {
        File f = new File(dir);
        File[] ff = f.listFiles();
        ArrayList a = new ArrayList();
        a.addAll(Arrays.asList(ff));
        return a;
    }

}
