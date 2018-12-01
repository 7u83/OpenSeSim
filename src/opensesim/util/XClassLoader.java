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
        
        public Class getClassByName(String name){
            return byName.get(name);
        }
        
    }

}
