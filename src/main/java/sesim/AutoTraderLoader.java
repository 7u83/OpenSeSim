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

    public void pf(String file) {
        System.out.printf("File\n", file);
    }

    Class<AutoTraderConfig> loadClass(String filename, String classname) {

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
            String kanone = cls.getCanonicalName();
            for (Class<?> i : cls.getInterfaces()) {
                if (i.equals(AutoTraderConfig.class)) {
                    return (Class<AutoTraderConfig>) cls;
                }
            }

        } catch (ClassNotFoundException ex) {
        }
        return null;

    }

    public ArrayList<Class<AutoTraderConfig>> getTraders() {

        int curlen = 0;

        ArrayList<Class<AutoTraderConfig>> traders;
        traders = new ArrayList<>();

        for (String classpathEntry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {

            Consumer<? super Path> pf = new Consumer() {
                @Override
                public void accept(Object t) {
                    String fn = ((Path) t).toString();
                    if (fn.toLowerCase().endsWith(".class")) {
                        Class<AutoTraderConfig> cls = loadClass(fn, fn.substring(classpathEntry.length()));
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

                              //      System.out.printf("Entry: %s\n", entry.getName());

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

        return traders;

    }

    public ArrayList<String> getDefaultStrategyNames() {
        ArrayList<Class<AutoTraderConfig>> trclasses;
        trclasses = this.getTraders();
        ArrayList<String> ret = new ArrayList<>();
        trclasses = getTraders();

        for (int i = 0; i < trclasses.size(); i++) {
            try {
                AutoTraderConfig ac = trclasses.get(i).newInstance();
                ret.add(ac.getName());
            } catch (Exception ex) {

            }

        }

        return ret;
    }

    public AutoTraderConfig getStrategy(String name) {
        ArrayList<Class<AutoTraderConfig>> traders = this.getTraders();
        for (int i = 0; i < traders.size(); i++) {
            try {
                AutoTraderConfig ac = traders.get(i).newInstance();

                if (ac.getName().equals(name)) {
                    return ac;
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
