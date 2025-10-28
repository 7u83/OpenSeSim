/*
 * Copyright (c) 2025, tube
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

/**
 *
 * @author tube
 */


public class Benchmark {

    private static final long N = 1000000000L; // 100 Millionen Operationen

    public static void main(String[] args) {
        benchmarkFloat();
        benchmarkDouble();
        benchmarkInt();
        benchmarkLong();
    }

    private static void benchmarkFloat() {
        float a = 1.2345f, b = 2.3456f, c = 0f;
        long t0 = System.nanoTime();
        for (long i = 0; i < N; i++) {
            c += a * b;
      //      c /= 1.0001f;
            c += a - b;
        }
        long t1 = System.nanoTime();
        System.out.printf("float:  %.3f ms (%.2f ns/op)%n",
                (t1 - t0) / 1e6, (double)(t1 - t0) / N);
    }

    private static void benchmarkDouble() {
        double a = 1.23456789, b = 2.3456789, c = 0.0;
        long t0 = System.nanoTime();
        for (long i = 0; i < N; i++) {
            c += a * b;
      //      c /= 1.0000001;
            c += a - b;
        }
        long t1 = System.nanoTime();
        System.out.printf("double: %.3f ms (%.2f ns/op)%n",
                (t1 - t0) / 1e6, (double)(t1 - t0) / N);
    }

    private static void benchmarkInt() {
        int a = 12345, b = 23456, c = 0;
        long t0 = System.nanoTime();
        for (long i = 0; i < N; i++) {
            c += a * b;
      //      c /= 3;
            c += a - b;
        }
        long t1 = System.nanoTime();
        System.out.printf("int:    %.3f ms (%.2f ns/op)%n",
                (t1 - t0) / 1e6, (double)(t1 - t0) / N);
    }

    private static void benchmarkLong() {
        long a = 123456789L, b = 234567891L, c = 0;
        long t0 = System.nanoTime();
        for (long i = 0; i < N; i++) {
            c += a * b;
    //        c /= 3;
            c += a - b;
        }
        long t1 = System.nanoTime();
        System.out.printf("long:   %.3f ms (%.2f ns/op)%n %d",
                (t1 - t0) / 1e6, (double)(t1 - t0) / N,c);
    }
}

