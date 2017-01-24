/*
 * Copyright (c) 2017, tobias
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.lang.ClassLoader.*;

/**
 *
 * @author tobias
 */
public class Test {

    static void tube() {
        try {
            System.out.printf("Hello %s\n", "args");
            if (0 == 0) {
                return;
            }
        } finally {
            System.out.printf("Always %s\n", "the end");
        }
        System.out.print("haha\n");
    }

    static void print_account(AccountData ad) {
        System.out.print(
                "Account ID:"
                + ad.id
                + " Ballance:"
                + ad.money
                + " Shares:"
                + ad.shares
                + "\n"
        );
    }
    
    
    


static public String getFullClassName(String classFileName) throws IOException {           
        File file = new File(classFileName);

        FileChannel roChannel = new RandomAccessFile(file, "r").getChannel(); 
        ByteBuffer bb = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int)roChannel.size());    
        
        String x = new String();

        //x.getClass().getClassLoader().loadClass(x);
        //Class<?> clazz = defineClass((String)null, bb, (ProtectionDomain)null);
        //return clazz.getName();
        return "";
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, MalformedURLException, InstantiationException, IllegalAccessException, IOException {
        TraderLoader tl = new TraderLoader();
        
        
        tl.get();
        System.exit(0);
        
        
        

        ArrayList<File> sl = tl.getTraders("./target/classes/traders");

        File file = sl.get(0);

        System.out.printf("Filename %s\n", file.getName());

        try {
            // Convert File to a URL
            URL url = file.toURL();          // file:/c:/myclasses/
          
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
           
            
            Class cls = cl.loadClass("traders.RandomTraderConfig");
            
            System.out.printf("Loaded Class: %s\n",cls.getClass().getName());
            
            sesim.AutoTraderConfig at = (AutoTraderConfig )cls.newInstance();
           

            String cp = System.getProperty("java.class.path");
            System.out.printf("CP: %s\n", cp);

            
            
            
            
            // Load in the class; MyClass.class should be located in
            // the directory file:/c:/myclasses/com/mycompany
            //Class cls = cl.loadClass("com.mycompany.MyClass");
            
            
        } catch (ClassNotFoundException e) {
            System.out.printf("hahahah %s\n", e.getClass().getName());
System.out.print("\nException was thrown\n");
System.out.print(e.getMessage());
System.out.print("\n;;;;a\n");
        }

    }

}
