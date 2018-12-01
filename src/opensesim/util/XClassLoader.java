/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class XClassLoader {

    /**
     * Build a list of urls for an additional class path
     *
     * @param xpath
     * @return List
     */
    public static ArrayList<URL> getXPathUrlList(String xpath) {

        ArrayList<URL> urllist = new ArrayList<>();
        URL url;

        if (xpath == null) {
            return urllist;
        }

        // add xpath to load find classes stored under xpath
        try {
            url = new File(xpath).toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            return urllist;
        }

        // add all .jar files
        urllist.add(url);
        ArrayList<File> files;
        files = Files.listFiles(xpath, ".jar");

        urllist.addAll(opensesim.util.Files.buildUrlList(files));
        return urllist;

    }

    private static Class lClass(ArrayList<URL> urllist, Class checks[], String class_name) {

        URLClassLoader cl;
        URL[] urls = urllist.toArray(new URL[urllist.size()]);
        cl = new URLClassLoader(urls);

        Class cls;
        try {
            cls = cl.loadClass(class_name);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NoClassDefFoundError e) {
            return null;
        }

        if (cls == null) {
            return null;
        }

        if (Modifier.isAbstract(cls.getModifiers())) {
            return null;
        }

        if (Modifier.isInterface(cls.getModifiers())) {
            return null;
        }
        for (Class check : checks) {
            if (check.isAssignableFrom(cls)) {
                return cls;
            }
        };

        return null;
    }

    public static ArrayList<Class> getClassesList(ArrayList<URL> urllist, Class checks[]) {

        ArrayList<Class> class_list;
        class_list = new ArrayList<>();

        if (urllist == null) {
            return class_list;
        }

        for (URL url : urllist) {
            if (url.getProtocol().equals("file")) {
                File f = new File(url.getFile());
                if (f.exists() && f.isDirectory()) {

                    System.out.println(f.getAbsolutePath());

                    ArrayList<File> classfilelist = Files.listFiles(f.getAbsolutePath(), ".class");
                    for (File classfile : classfilelist) {
                        String class_name;
                        class_name = classfile.toString().substring(f.toString().length());
                        // in case we are under Windows, replace \ with /
                        class_name = class_name.replace("\\", "/");
                        class_name = class_name.substring(1, class_name.length() - 6).replace('/', '.');

                        Class c = XClassLoader.lClass(urllist, checks, class_name);
                        if (null != c) {
                            class_list.add(c);
                        }

                        //java.util.logging.Logger.getLogger("opensesim").log(Level.SEVERE, );
                        //Class<?> c = checkClass(path, class_name);                        
                    }
                    continue;
                }
            }

            JarInputStream jarstream;
            try {
                jarstream = new JarInputStream(url.openConnection().getInputStream());

            } catch (IOException ex) {
                Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            JarEntry jarentry;
            try {
                while ((jarentry = jarstream.getNextJarEntry()) != null) {

                    String class_name = jarentry.getName();

                    if (class_name.endsWith(".class")) {

                        class_name = class_name.substring(0, class_name.length() - 6).replace('/', '.');
                        Class<?> c = XClassLoader.lClass(urllist, checks, class_name);
                        if (null != c) {
                            class_list.add(c);
                        }

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return class_list;
    }

    public static ArrayList<Class> getClassesList(ArrayList<URL> urllist, Class check){
        return XClassLoader.getClassesList(urllist,new Class[]  {check} );
    }
    
    
    public static class ClassCache {
        HashMap <String,Class> byName;
        HashMap <Class,HashSet<Class>> byInstanceOf;
                
        public ClassCache (ArrayList<URL> urllist,Class[] classes){
            byName=new HashMap<>();
            byInstanceOf=new HashMap<>();
            
            ArrayList<Class> results = getClassesList(urllist,classes);
            for (Class result:results){
                for (Class c:classes){
                    if (c.isAssignableFrom(result)) {
                        HashSet<Class> h;
                        h = byInstanceOf.get(c);
                        if (h==null){
                            h=new HashSet<>();
                            byInstanceOf.put(c, h);
                        }
                        h.add(result);
                    }
                }       
                byName.put(result.getName(), result);
            }
        }
     
        public Collection<Class> getClassCollection(Class cls){
            HashSet<Class> h;
            h=byInstanceOf.get(cls);
            if (h==null)
                return h;
            return Collections.unmodifiableCollection(h);
        }
        
    }
    /**
     *
     * @param pathlist
     * @return
     */
    /*    public static ArrayList<Class<?>> getClasses(ArrayList<String> pathlist) {

        ArrayList<Class<?>> result = new ArrayList<>();

        for (String path : pathlist) {

            ArrayList<File> files = Files.listFiles(path);

            for (File file : files) {

                if (!file.exists()) {
                    java.util.logging.Logger.getLogger("opensesim").log(Level.WARNING,
                            "File dos not exists: " + file.getAbsolutePath());
                    continue;
                }

                String fn = file.toString();

                // handle file with .class extension
                if (fn.toLowerCase().endsWith(".class")) {

                    String class_name;
                    class_name = fn.substring(path.length());
                    // in case we are under Windows, replace \ with /
                    class_name = class_name.replace("\\", "/");
                    class_name = class_name.substring(1, class_name.length() - 6).replace('/', '.');

                    //java.util.logging.Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, null);              
                    java.util.logging.Logger.getLogger("opensesim").log(Level.SEVERE, fn);
                    Class<?> c = checkClass(path, class_name);
                    //         if (null == c) {
                    //             continue;
                    //         }
                    //         result.add(c);
                }

                // handle file .jar extension
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
                                Class<?> c = checkClass(fn, class_name);
//                                if (null == c) {
//                                    continue;
                                //                               }
                                //                               result.add(c);
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

    public static ArrayList<Class<?>> getClasses(String path) {
        ArrayList<String> pathlist = new ArrayList();
        pathlist.add(path);
        return getClasses(pathlist);

    }
    
     */
 /*
    private static Class<?> checkClass(String directory, String class_name) {

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

            System.out.printf("ClassChecker: %s\n", class_name);

            String gn = cls.toGenericString();

            ArrayList<Class<UIManager.LookAndFeelInfo>> res;

            Class<?> xxx = javax.swing.LookAndFeel.class;

            if (Modifier.isAbstract(cls.getModifiers())) {
                return null;
            }

            if (Modifier.isInterface(cls.getModifiers())) {
                return null;
            }

            boolean rrr = xxx.isAssignableFrom(cls);

            if (rrr) {
                System.out.print(class_name);
                System.out.print("\n");
                //javax.swing.LookAndFeel laf;

                //UIManager.installLookAndFeel(class_name, class_name);
                //UIManager.installLookAndFeel(class_name, class_name);
            }

            //res.getClass().isAssignableFrom(cls);

            return (Class<?>) cls;

        } catch (NoClassDefFoundError ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
     */
}
