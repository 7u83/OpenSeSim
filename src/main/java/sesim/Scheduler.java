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

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Scheduler extends Thread {

    long multiply = 1;

    private final SortedMap<Long, SortedSet<TimerTask>> event_queue = new TreeMap<>();
    private boolean halt = false;

    public interface TimerTask {

        long timerTask();
    }

    /**\
     * 
     */
    public void halt() {
        halt = true;
        synchronized (event_queue) {
            event_queue.notifyAll();
        }
    }

    /**
     * 
     */
    private class ObjectComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
    }

    /**
     *
     * @return
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();

    }
    
    public static long timeStart=Scheduler.currentTimeMillis();
        
    

    /**
     * 
     * @param e
     * @param time 
     */
    public void startTimerEvent(TimerTask e, long time) {
        long evtime = time + currentTimeMillis();
        synchronized (event_queue) {
            this.addEvent(e, time);
        }
        synchronized (this) {
            notify();
        }
    }
    
    private boolean pause=false;
    
    public void pause(){
        pause=!pause;
        synchronized(this){
            this.notify();            
        }

    }

    public long fireEvent(TimerTask e) {
        return e.timerTask();
    }

    private boolean addEvent(TimerTask e, long time) {

        long evtime = time + currentTimeMillis();

        SortedSet<TimerTask> s = event_queue.get(evtime);
        if (s == null) {
            s = new TreeSet<>(new ObjectComparator());
            event_queue.put(evtime, s);
        }
        return s.add(e);
    }

    public long runEvents() {
        synchronized (event_queue) {
            if (event_queue.isEmpty()) {
                return -1;
            }

            long t = event_queue.firstKey();
            if (t <= currentTimeMillis()) {
                SortedSet s = event_queue.get(t);
                event_queue.remove(t);
                Iterator<TimerTask> it = s.iterator();
                while (it.hasNext()) {
                    TimerTask e = it.next();
                    long next_t = this.fireEvent(e);
                    this.addEvent(e, next_t);
                }
                return 0;

            } else {
                return t - currentTimeMillis();
            }
        }

    }

    @Override
    public void run() {
        while (!halt) {

            
            
            long wtime = runEvents();
            if (wtime == 0) {
                continue;
            }

            synchronized (this) {
                try {
                    if (pause){
                        wtime=-1;
                    }
                    
                    if (wtime != -1) {
                        wait(wtime);
                    } else {
                        wait();
                    }
                } catch (Exception e) {

                }
            }
        }

    }

}
