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
import java.util.logging.Level;
import javax.swing.JPanel;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SeSimClassLoader {

    protected ArrayList<String> pathlist;

    /**
     * Set the path list where to search for traders
     *
     * @param pathlist List of paths
     */
    public final void setPathList(ArrayList<String> pathlist) {
        this.pathlist = pathlist;

    }

    public SeSimClassLoader(ArrayList<String> pathlist) {
        setPathList(pathlist);
    }

    public ArrayList<File> listFiles(String path) {

        ArrayList<File> files = new ArrayList<>();

        // get all the files from a directory
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

    public Object MakeInstance(Class<?> cls) {
        //      ClassLoader cur = Thread.currentThread().getContextClassLoader();
        //      Thread.currentThread().setContextClassLoader(cl);

        

        try {
             return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {

            System.out.printf("Error: %s\n",ex.getMessage());
            
        }
        //      Thread.currentThread().setContextClassLoader(cur);

        return null;
    }

    /**
     * Check if a given class provides an certain interface
     * and also if the class is not abstract, so it could be
     * instanciated.
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

    Class<?> xloadClass(String filename, String classname) {

        if (classname == null) {
            return null;
        }

        String clnam = classname.substring(1, classname.length() - 6).replace('/', '.');

        try {
            
            Class<?> cls = Class.forName(clnam);
            if (cls == null) {
                System.out.printf("nullclass\n");
            }
            return cls;

        } catch (ClassNotFoundException ex) {
            // something wnet wrong, but we ignore it
            System.out.printf("Class not found\n");

        }
        return null;

    }

    /**
     * 
     * @param iface 
     */
    public void getInstalledClasses(Class<?> iface) {
       
        for (String classpathEntry : pathlist) {
            System.out.printf("Classpath Entry: %s\n", classpathEntry);

            ArrayList<File> files = listFiles(classpathEntry);
            System.out.printf("Number of entries: %d\n", files.size());
            for (File file : files) {
                //        System.out.printf("File: %s\n", file.toString());

                String fn = file.toString();
                if (fn.toLowerCase().endsWith(".class")) {
                    String class_name = fn.substring(classpathEntry.length());

                    Class<AutoTraderInterface> cls;
                    Class<?> c = xloadClass(fn, class_name);

                    if (this.isInstance(c, AutoTraderInterface.class)) {
                        System.out.printf("Her is an autotrader %s\n", class_name);
                        
                       
                        AutoTraderInterface a = (AutoTraderInterface)MakeInstance(c);
                        if (a==null)
                            continue;
                        System.out.printf("AutoName: %s\n", a.getConfig().toString());
                    }

                }

            }
            //System.exit(0);
        }
    }

}
