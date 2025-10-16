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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sesim.Scheduler.Event;
import sesim.Scheduler.EventProcessor;

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

    static public String getFullClassName(String classFileName) throws IOException {
        File file = new File(classFileName);

        FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
        ByteBuffer bb = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());

        String x = new String();

        //x.getClass().getClassLoader().loadClass(x);
        //Class<?> clazz = defineClass((String)null, bb, (ProtectionDomain)null);
        //return clazz.getName();
        return "";
    }

    /*  static private <T extends Number> void to(T n, Double o) {
        if (Float == T) {
            System.out.printf("Double ret %", o.floatValue());

            n = (T) (Number) o.floatValue();
        }

    }
     */
    static class Exer extends Thread {

        int value = 0;

        @Override
        public void run() {

            while (true) {
                try {
                    System.out.printf("Exer getting Exer Lock");
                    synchronized (this) {
                        System.out.printf("Exer having Exer Lock wait 30000\n");
                        this.wait();
                    }

                } catch (InterruptedException ex) {
                    System.out.printf("Interrupted\n");
                }

                System.out.printf("Exer Value %d\n", value);
            }
        }

    }

    static class Runner extends Thread {

    }
    static Scheduler s = new Scheduler();

    static class MyTask implements EventProcessor {

        long ctr = 0;

        @Override
        public long processEvent(Event e) {
            ctr++;
            double r = 1;
            for (int i = 0; i < 100000; i++) {
                r = r + i * r;
                r = r + 1.0;
            }
            synchronized (this) {
                try {
                    wait(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.printf("TimerTask %d %d %f\n", ctr, s.getCurrentTimeMillis(), r);

            return 1000;
        }

        @Override
        public long getID() {
            return 0;
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, MalformedURLException, InstantiationException, IllegalAccessException, IOException {

        double val = Math.log(12);
        double rval = Math.exp(val);
        
        
        System.out.printf("Result: %f, %f\n", val,rval);
        
        
      
    }

}
