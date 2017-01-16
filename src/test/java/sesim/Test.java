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

import java.util.SortedMap;
import java.util.TreeMap;





/**
 *
 * @author tobias
 */
public class Test {
    
   static void tube(){
       try{
           System.out.printf("Hello %s\n", "args");
           if (0==0) 
               return;
       }
       finally {
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        
        Scheduler s = new Scheduler();
        s.start();
      
        class Ev implements Scheduler.TimerEvent{

           @Override
           public long timerEvent() {
               System.out.printf("Timer Event Occured %s\n",name);
               if ("Ev1".equals(this.name))
                    return 2000;
               else
                   return 4000;
           }
           
           String name;
           Ev(String name){
               this.name=name;
           }
            
        }
        
        Ev e1 = new Ev("Ev1");
        Ev e2 = new Ev("Eb2");
        
        
        s.startEvent(e1, 0);
            s.startEvent(e2, 0);
        
    try
    {
        Thread.sleep(90000);
    }
    catch(Exception e) {
        
    }
    
    s.halt();
        while (s.isAlive()){
           
        }
        
        System.out.print("All isstopped\n");            

       // s.startEvent(e2, 100);
        
        
        
    
       /* long starttime=System.currentTimeMillis();
        while (s.isAlive()){
            if (System.currentTimeMillis()>starttime+6650){
                    s.stop();
                    break;
            }
        }
        System.out.print("Waiting fpor Stop\n"); 
        while (s.isAlive()){
           
        }
        
        System.out.print("All isstopped\n");
    */
      
       
    }

}
