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

import java.util.Date;

/**
 *
 * @author tobias
 *
 * Implements an adjustable clock
 *
 */
public class Clock {

    private double acceleration=1.0;
    private double current_time_millis=0.0;
    private long last_time_millis=System.currentTimeMillis();
    private boolean pause=false;
    
    
    
    public synchronized long currentTimeMillis1() {

        long cur = System.currentTimeMillis();

        double diff = cur - last_time_millis;
        last_time_millis = cur;
  
        
        if (pause) {
            return (long) this.current_time_millis;
        }
        
  //      System.out.printf("Floaf TM: %f\n", this.current_time_millis);
   
        this.current_time_millis +=  diff * acceleration;
        return (long) this.current_time_millis;
    }

   /**
    * Set the clock acceleration
    * @param acceleration 
    */
    public void setAcceleration(double acceleration){
        this.acceleration=acceleration;
    }
    
    
    public void setPause(boolean p){
        pause=p;
    }
    
    
}
