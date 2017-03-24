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

    private ArrayList<String> pathlist;
    private ArrayList<Class<AutoTraderInterface>> traders_cache;

    public AutoTraderLoader(ArrayList<String> pathlist) {
        setPathList(pathlist);
    }

    public final void setPathList(ArrayList<String> pathlist) {
        this.pathlist = pathlist;
        this.traders_cache = null;
    }

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
                Globals.LOGGER.info(String.format("Interface: %s", i.getCanonicalName()));

                String cn = AutoTraderInterface.class.getCanonicalName();
                Globals.LOGGER.info(String.format("Interface1: %s", cn));

                //  if (i == (AutoTraderInterface.class)) {
                if (cn.endsWith(i.getCanonicalName())) {

                    Globals.LOGGER.info("YEEEEA");

                    return true;
                }
            }

        } while ((cls = cls.getSuperclass()) != null);
        return false;
    }
    
    
    private ClassLoader cl;
    
    public AutoTraderInterface MakeInstance(Class<?> cls){
        ClassLoader cur = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        
        AutoTraderInterface ai;
        Globals.LOGGER.info("Going to load");
        try {
            ai = (AutoTraderInterface)cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Globals.LOGGER.info("Ist was passiert");
            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
            ai=null;
        }
        Thread.currentThread().setContextClassLoader(cur);

        return ai;
    }
    

    Class<AutoTraderInterface> loadAutoTraderClass(String filename, String classname) {

        System.out.printf("Comming in width %s %s", filename, classname);
        Globals.LOGGER.info(String.format("Comming in width %s %s\n", filename, classname));

        if (classname == null) {
            System.out.printf("Calssname is null\n");

            return null;

        }

        String clnam = classname.substring(1, classname.length() - 6).replace('/', '.');
        File f = new File(filename);

        URL url = null;
        try {
            url = f.toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        Globals.LOGGER.info(String.format("URL: %s", url.toString()));

        URL[] urls = new URL[]{url};


        // Create a new class loader with the directory
        cl = new URLClassLoader(urls);
        
        
        //Thread.currentThread().setContextClassLoader(cl);
        
        cl = Thread.currentThread().getContextClassLoader();
        

        try {
            Globals.LOGGER.info("try cl");
            //          Class<?> cls = cl.loadClass(clnam);
            Class<?> c = cl.loadClass(clnam);
            if (c == null) {
                Globals.LOGGER.info("loader c was null");
            }
            Globals.LOGGER.info("Ccast");
            Class<AutoTraderInterface> cls = (Class<AutoTraderInterface>) c; // cl.loadClass(clnam);
            //   return cls;
            if (cls == null) {

            }

            System.out.printf("Check Class: %s\n", cls.getCanonicalName());
            Globals.LOGGER.info(String.format("Class prope %s", cls.getCanonicalName()));

            if (isAutoTrader(cls)) {
                Globals.LOGGER.info("We have found an autotrader interface");
                Class<AutoTraderInterface> claa;
                claa = (Class<AutoTraderInterface>) cls;

                if (claa == null) {
                    Globals.LOGGER.info("claa = null");
                }

                Globals.LOGGER.info("return ok");
                return claa;
                //return (Class<AutoTraderInterface>) cls;
                //return (Class<AutoTraderInterface>) cls;
            }

        } catch (ClassNotFoundException ex) {
            // something wnet wrong, but we ignore it
            System.out.printf("Class not found\n");
            Globals.LOGGER.info("Class not loadable");
        }
        return null;

    }

    /* public ArrayList<Class<AutoTraderInterface>> getTradersX() {    
            String[] a = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
            this.pathlist=new ArrayList<>(Arrays.asList(a));
            return getTraders0();
    }
     */
    public ArrayList<Class<AutoTraderInterface>> getTraders() {

        if (traders_cache != null) {
            return traders_cache;
        }

        ArrayList<Class<AutoTraderInterface>> traders;
        traders = new ArrayList<>();

        for (String classpathEntry : pathlist) {

            Globals.LOGGER.info(String.format("Here we ar looking now %s", classpathEntry));

            Consumer<? super Path> pf = (Object t) -> {

                String fn = ((Path) t).toString();
//                System.out.printf("Checking out file %s\n", fn);
                Globals.LOGGER.info(String.format("Checking out file %s\n", fn));

                if (fn.toLowerCase().endsWith(".class")) {
                    String cl = fn.substring(classpathEntry.length());
                    System.out.printf("The CL: %s\n", cl);

                    Class<AutoTraderInterface> cls = loadAutoTraderClass(fn, cl);
                    if (cls == null) {
                        return;
                    }
                    traders.add(cls);
                }
                if (fn.toLowerCase().endsWith(".jar")) {
                    System.out.printf("Its a jar!\n");
                    Globals.LOGGER.info("Its a jar");

                    JarInputStream is = null;
                    try {
                        File jar = new File(fn);
                        is = new JarInputStream(new FileInputStream(jar));
                        JarEntry entry;

                        Globals.LOGGER.info("starting entries");
                        while ((entry = is.getNextJarEntry()) != null) {
                            Globals.LOGGER.info(String.format("Jar entry: %s", entry));

                            if (entry.getName().endsWith(".class")) {

                                System.out.printf("Entry: %s\n", entry.getName());

                                String fn0 = entry.getName();
                                Class<AutoTraderInterface> cls = loadAutoTraderClass(fn, "/" + entry.getName());
                                if (cls != null) {
                                    Globals.LOGGER.info("Class is not null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                                    traders.add(cls);
                                }
                                //Globals.LOGGER.info("clas was null");

                            }
                        }
                    } catch (IOException ex) {
                        Globals.LOGGER.info("ioeception");
                        Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            is.close();
                        } catch (IOException ex) {

                            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
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
        
        Globals.LOGGER.info(String.format("We hav found %d traders", traders.size()));
        
        return traders;

    }

    public ArrayList<String> getDefaultStrategyNames(boolean devel) {
        ArrayList<Class<AutoTraderInterface>> trclasses;
        trclasses = this.getTraders();

        ArrayList<String> ret = new ArrayList<>();
        trclasses = getTraders();

        for (int i = 0; i < trclasses.size(); i++) {
            try {

                //AutoTraderInterface ac = trclasses.get(i).newInstance();
                AutoTraderInterface ac = this.MakeInstance(trclasses.get(i));
                
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
                Globals.LOGGER.info(String.format("Making lll instance of %s", traders.get(i).getCanonicalName()));
                
                if (traders.get(i)==null){
                    Globals.LOGGER.info("We have null");
                }
                
//                AutoTraderInterface ac = traders.get(i).newInstance();
                AutoTraderInterface ac = this.MakeInstance(traders.get(i));

                System.out.printf("Looking for in %s == %s\n", ac.getClass().getCanonicalName(), name);

                if (ac.getClass().getCanonicalName().equals(name)) {
                    return ac;

                    // if (ac.getDisplayName().equals(name)) {
                    //     return ac;}
                }
            } catch (Exception ex) {
                Globals.LOGGER.info(String.format("Instance failed %s", ex.getMessage()));
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
