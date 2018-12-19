/*
 * Copyright (c) 2018, tohe
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
package opensesim.world.scheduler;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Clock {


    private double current_millis = 0.0;
    private long last_nanos = System.nanoTime();
    private double current_nanos = 0;
    private double acceleration = 1.0;

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }
    private boolean pause = false;

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
    

    /**
     *
     * @return
     */
    private long currentTimeMillis1() {

        long cur = System.nanoTime();
        long diff = cur - last_nanos;
        last_nanos = cur;

        if (pause) {
            return (long) this.current_millis;
        }

        //  this.cur_nano += (((double)diff_nano)/1000000.0)*this.acceleration;
        //     return (long)(cur_nano/1000000.0);        
        this.current_nanos += (double) diff * (double) this.acceleration;

//        this.current_time_millis += ((double) diff) * this.acceleration;
        this.current_millis = this.current_nanos / 1000000.0;

        return (long) this.current_millis;
    }

    public long currentTimeMillis() {
        return (long) this.current_millis;
    }

    synchronized long getDelay(long t) {
        long ct = currentTimeMillis1();

//            ct = t;
        if (t > ct) {
            //if ((long) diff > 0) {
            //              System.out.printf("Leave Event Queue in run events %d\n", Thread.currentThread().getId());
//                System.out.printf("Sleeping somewat %d\n", (long) (0.5 + (t - this.currentTimeMillis()) / this.acceleration));
            //  return (long) diff;
            return (long) (((double) t - this.current_millis) / this.acceleration);
        }

        if (t < ct) {
            //  System.out.printf("Time is overslipping: %d\n",ct-t);
            this.current_millis = t;
            this.current_nanos = this.current_millis * 1000000.0;

        }
        return 0;
    }

}
