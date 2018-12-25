/*
 * Copyright (c) 2018, 7u83
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;
import opensesim.world.scheduler.EventListener;

/**
 *
 * @author tohe
 */
public class Scheduler {

    private final SortedMap<Long, LinkedList<FiringEvent>> event_queue = new TreeMap<>();

    private class Worker extends Thread {

        boolean terminate = false;

        @Override
        public void run() {
            while (!terminate) {

                long delay = getDelay();
//System.out.printf("Worker %d has delay %d\n",Thread.currentThread().getId(), delay);                
                if (delay > 0  || delay==-1) {
                    //System.out.printf("Worker %d sleeps for %d\n",Thread.currentThread().getId(), delay);
                    synchronized (clock) {
                        try {
                            if (delay != -1 && !clock.isPause()) {
                                clock.wait(delay);
                            } else {
                                clock.wait();
                            }
                        } catch (InterruptedException e) {
//System.out.printf("Interrupted\n");
                        }
                    }
                    continue;
                }
                FiringEvent e = getNextEvent();
                
                if (e == null) {
                    continue;
                }
//System.out.printf("Worker %d got event %d\n",Thread.currentThread().getId(), e.t);
                e.listener.receive(e);
            }
        }
    }

    ArrayList<Worker> workers;
    final private Clock clock = new Clock();
 //   int next = 0;

    public Scheduler(int nthreads) {
        workers = new ArrayList<>();
        for (int i = 0; i < nthreads; i++) {
            workers.add(i, new Worker());
        }
    }

    public Scheduler() {
        this(10);
    }

    public void start() {
        for (Worker w : workers) {
            w.start();
        }
    }

    public FiringEvent startTimerTask(EventListener listener, long time) {
        FiringEvent e = new FiringEvent(listener);
        long t = time + clock.currentTimeMillis();
        synchronized (event_queue) {
            LinkedList<FiringEvent> s = event_queue.get(t);
            if (s == null) {
                s = new LinkedList<>();
                event_queue.put(t, s);
            }

            s.add(e);
        }
        synchronized (clock) {
            clock.notifyAll();
        }
        return e;

    }

    protected long getDelay() {
        synchronized (event_queue) {
            

            if (event_queue.isEmpty()) {
                return -1;
            }

            long t = event_queue.firstKey();
//System.out.printf("Worker: %d - queu is not empty: cur millis %d til %d\n", Thread.currentThread().getId(), clock.currentTimeMillis(),t);
            return clock.getDelay(t);
        }
    }

    protected FiringEvent getNextEvent() {

        //  System.out.printf("RunEvents in Thread %d\n",Thread.currentThread().getId());
        synchronized (event_queue) {
            if (event_queue.isEmpty()) {
                return null;
            }

            long t = event_queue.firstKey();
            LinkedList<FiringEvent> s = event_queue.get(t);

            FiringEvent e = s.pop();
            if (s.isEmpty()) {
                event_queue.remove(t);
            }

            return e;

        }

    }

    public long currentTimeMillis(){
        return clock.currentTimeMillis();
    }
    
    public void setAcceleration(double a){
        clock.setAcceleration(a);
        
    }
    
}
