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
package gui;

import java.util.*;

/**
 *
 * @author tobias
 */
public class test {

    static class Problem {

        class Elem implements Comparable {

            public int id;

            public Elem(int id) {
                this.id = id;

            }

            @Override
            public int compareTo(Object o) {
                Elem e = (Elem) o;
                return id - e.id;

            }

        }

        public void run() {
            SortedSet<Elem> s = new TreeSet<>();
            s.add(new Elem(1));
            s.add(new Elem(7));
            s.add(new Elem(12));
            Elem e = new Elem(5);

            SortedSet<Elem> ts = exclusiveTailSet(s, e);

            Elem e2 = new Elem(0);

//        SortedSet<Elem> ts2 = exclusiveTailSet(ts,e);
            SortedSet<Elem> ts2 = ts.tailSet(e2);

            e.id = 99;

            System.out.print(String.format("First: %s\n", ts.first().id));
        }

    }
    
    
    static class NoProblem {
        public void run(){
            SortedSet<Integer> s=new TreeSet<>();
            
            s.add(10);
            s.add(20);
            s.add(30);
            s.add(40);
            s.add(50);
            s.add(60);

            int e1 = 15;
            SortedSet l1 = s.tailSet(e1);
            
            int e2 = -1;
            
            SortedSet l2 = l1.tailSet(e2);
            
            System.out.print("First:"+l2.first()+"\n");
            
            
            
        }
    }

    public static <Ta> SortedSet<Ta> exclusiveTailSet(SortedSet<Ta> ts, Ta elem) {
        Iterator<Ta> iter = ts.tailSet(elem).iterator();

        return ts.tailSet(iter.next());
    }
    
    
    static abstract interface Rc {
        public abstract int inc(int x);
  //      public abstract int dec(int x);
     
        
        
    }
    
    

    public static void main(String args[]) {
//        NoProblem p = new NoProblem();
//        p.run();

        Rc a = new Rc(){
            @Override
            public int inc(int x) {
                return x+1;
            }
            
        /*    @Override
            public int dec(int x) {
                return x+1;
            }
*/
            
        };
        
        
        Rc l = (x) -> x+1;

        
        System.out.print("A:"
                +a.inc(17)
                +"  "
                +l.inc(4)
        
        );
        
       

    }
}
