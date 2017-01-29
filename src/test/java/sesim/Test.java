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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, MalformedURLException, InstantiationException, IllegalAccessException, IOException {

        Float x0;
        x0 = 3.1f;

        int x1; // = new Integer(0);

        x1 = 4;

        //x1 = (Integer)(Number)x0;
        Double z = 0.99;
        // to(x0,z);

        System.out.printf("Erg: %f\n", x0);

        System.out.printf("Hello world\n", "");
        System.exit(0);

    }

}
