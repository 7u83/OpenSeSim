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
public class TraderLoader {

    public void pf(String file) {
        System.out.printf("File\n", file);
    }

    Class <AutoTraderConfig> loadClass(String filename, String classname) throws MalformedURLException {

        String clnam = classname.substring(1, classname.length() - 6).replace('/', '.');
      //  System.out.printf("Load class name: %s\n", clnam);

        //     Class<?> cls = ClassLoader.loadClass(className);
        File f = new File(filename);

        URL url = f.toURL();          // file:/c:/myclasses/

        URL[] urls = new URL[]{url};

        // Create a new class loader with the directory
        ClassLoader cl = new URLClassLoader(urls);

        try {
            Class<?> cls = cl.loadClass(clnam);
            
            String kanone = cls.getCanonicalName();
//            System.out.printf("%s --------------------------------------\n",kanone);
            
            
/*            for (Class<?> i : cls.getClasses()){
                
                
                String iname = i.getCanonicalName();
                System.out.printf("Checking iname %s\n", iname);
                
                if (i.equals(AutoTraderConfig.class)){
                System.out.printf("Yea an autotrader found\n");
                
                }
            }
  */          
            
            for(Class<?> i : cls.getInterfaces()) {
                    
  
    
                
                  if(i.equals(AutoTraderConfig.class)) {
                      System.out.printf("Loading class %s\n", clnam);
                               
                      try {
                          String cname = i.getCanonicalName();
                          System.out.printf("CAnonical name: %s\n", cname);
                          
                         
                          
                          
                      } catch (Exception ex) {
                          System.out.printf("Ex: %s\n", ex.getClass().getName());
                          ex.printStackTrace();
                     }
                      return (Class <AutoTraderConfig>)cls;
                     //System.out.printf("Have found an Auto Trader %s\n", clnam);
                     //break;
                  }
               }
            
            
        } catch (ClassNotFoundException ex) {
            System.out.printf("Cant load class %s\n", clnam);
            
            
            //Logger.getLogger(TraderLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public ArrayList <Class <AutoTraderConfig>> get() throws IOException {

        int curlen = 0;

        /* Consumer<? super Path> pf = (Object t) -> {
            String fn = ((Path)t).toString();
            
            
            
            System.out.printf("Have it %s %d %s\n", fn,curlen,fn.substring(curlen));
        };
         */
        
        ArrayList <Class <AutoTraderConfig>> traders;
        traders = new ArrayList <> ();
        
        for (String classpathEntry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {

            
            
            Consumer<? super Path> pf = new Consumer() {
                @Override
                public void accept(Object t) {
                    String fn = ((Path) t).toString();
                    if (fn.toLowerCase().endsWith(".class")) {
                        try {
                            //System.out.printf("Halloe: %s %s\n", fn, fn.substring(classpathEntry.length()));
                            Class <AutoTraderConfig> cls = loadClass(fn, fn.substring(classpathEntry.length()));
                            if (cls==null)
                                return;
                            traders.add(cls);
                            
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(TraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (fn.toLowerCase().endsWith(".jar")) {
                        JarInputStream is = null;
                        try {
                            File jar = new File(fn);
                            is = new JarInputStream(new FileInputStream(jar));
                            JarEntry entry;
                            while ((entry = is.getNextJarEntry()) != null) {
                                if (entry.getName().endsWith(".class")) {

                                    System.out.printf("Entry: %s\n", entry.getName());

                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(TraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                is.close();
                            } catch (IOException ex) {
                                Logger.getLogger(TraderLoader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                }

            };

            Files.walk(Paths.get(classpathEntry))
                    .filter(Files::isRegularFile)
                    .forEach(pf);
 
        }

        return traders;

    }

    public ArrayList getTraders(String dir) {
        File f = new File(dir);
        File[] ff = f.listFiles();
        ArrayList a = new ArrayList();
        a.addAll(Arrays.asList(ff));
        return a;
    }

}
