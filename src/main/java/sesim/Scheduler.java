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

//import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

/**
 * Scheduler class that manages and executes time-based simulation events.
 * Supports acceleration, pause/resume, and termination.
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Scheduler extends Thread {

    /**
     * Whether the scheduler is currently paused.
     */
    private boolean pause = false;

    /**
     * Acceleration factor for simulated time (1.0 = real time).
     */
    private double acceleration = 1.0;

    /**
     * Flag to terminate the main loop.
     */
    private boolean terminate = false;

    /**
     * Ordered map of events, keyed by their scheduled time (milliseconds).
     */
    private final SortedMap<Long, LinkedHashSet<Event>> eventQueue
            = new TreeMap<>();

    /**
     * Current simulated time in milliseconds.
     */
    private double currentTimeMillis = 0.0;

    /**
     * Last recorded real system nanosecond time.
     */
    private long last_nanos = System.nanoTime();

    /**
     * Accumulated simulated nanoseconds (scaled by acceleration).
     */
    private double current_nanos = 0;

    private boolean maxAcceleartion = false;

    /**
     * Sets the time acceleration factor.
     *
     * @param val acceleration multiplier (>1.0 = faster, <1.0 = slower)
     */
    public void setAcceleration(double val) {
        this.acceleration = val;
        LockSupport.unpark(this);
    }

    /**
     * Returns the current acceleration factor.
     *
     * @return acceleration multiplier
     */
    public double getAcceleration() {
        return this.acceleration;
    }

    public void setMaxAcceleration(boolean val) {
        maxAcceleartion = val;
    }

    public boolean getMaxAcceleration() {
        return maxAcceleartion;
    }

    /**
     * Interface defining an event processor. Implementations handle event logic
     * when triggered.
     */
    public interface EventProcessor {

        long processEvent(long time, Event e);
        //       long getID();
    }

    /**
     * Requests the scheduler to stop and exit the main loop.
     */
    public void terminate() {
        terminate = true;
        /*        synchronized (eventQueue) {
            eventQueue.notifyAll();
        }*/
        pause = false;
        LockSupport.unpark(this);
    }

    long startTime;

    /**
     * Starts the scheduler thread, reinitializing it if not already running.
     */
    @Override
    public void start() {
        if (this.isAlive()) {
            return;
        }
        this.initScheduler();
        super.start();
        startTime = System.currentTimeMillis();

    }

    /**
     * Calculates the current simulated time, advancing it based on elapsed real
     * time and the acceleration factor.
     *
     * @return simulated time in milliseconds
     */
    private long calculateCurrentTimeMillis() {

        if (pause) {
            return (long) this.currentTimeMillis;
        }
        long cur = System.nanoTime();
        long diff = cur - last_nanos;
        last_nanos = cur;
        this.current_nanos += (double) diff * (double) this.acceleration;
        this.currentTimeMillis = this.current_nanos / 1000000.0;
        return (long) this.currentTimeMillis;
    }

    /**
     * Returns the current simulated time in milliseconds.
     *
     * @return
     */
    public long getCurrentTimeMillis() {

        return (long) this.currentTimeMillis;
    }

    /**
     * Formats a millisecond timestamp as HH:mm:ss.
     *
     * @param t time in milliseconds
     * @return formatted string
     */
    static public String formatTimeMillis(long t) {
        Date date = new Date(t);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        long seconds = (t / 1000) % 60;
        long minutes = (t / 1000 / 60) % 60;
        long hours = (t / 1000) / (60 * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }
    ExecutorService executor = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()  // Optimale Thread-Anzahl
);
    
    
    public Scheduler(){
        this.executor = ForkJoinPool.commonPool();  // Oder newFixedThreadPool(cores)


    }

    //private static AtomicInteger nextTimerTask = new AtomicInteger(0);
    public static class Event {

        protected EventProcessor eventProcessor;

     //   public String name;

     //   public long time;
        //   private final int id;

  /*      public Event(EventProcessor e, long t) {
            eventProcessor = e;
            //     id = nextTimerTask.getAndAdd(1);
        //    time = t;

        }*/

        public Event(EventProcessor p) {
            this.eventProcessor = p;
            //id = nextTimerTask.getAndAdd(1);
        }

       public Event() {
            eventProcessor = null;
        }

    }

    public void addEvent(long t, Event e) {
        LinkedHashSet<Event> s = eventQueue.get(t);
        if (s == null) {
            s = new LinkedHashSet<>();
            eventQueue.put(t, s);
        }
        s.add(e);
        LockSupport.unpark(this);
    }

    public boolean delEvent(long t, Event e) {
        LinkedHashSet<Event> s = eventQueue.get(t);
        if (s == null) {
            return false;
        }
        boolean rc = s.remove(e);
        if (rc) {
            LockSupport.unpark(this);
        }
        return rc;
    }

    public void pause() {
        setPause(!pause);
    }

    long pauseTime;

    public void setPause(boolean val) {
        pause = val;
        LockSupport.unpark(this);

        if (pause) {
            pauseTime = System.currentTimeMillis();
        } else {
            startTime += System.currentTimeMillis() + pauseTime;
        }

    }

    public long getCurrentTime() {
        if (pause) {
            return pauseTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    public boolean getPause() {
        return pause;
    }

    private long runEvents() {

        if (pause)
            return -1;
        
        if (eventQueue.isEmpty()) {
            return -1;
        }

        long t = eventQueue.firstKey();

        long ct;

        if (this.maxAcceleartion) {
            ct = t;
        } else {
            ct = calculateCurrentTimeMillis();
        }
        //long ct = t+1; //calculateCurrentTimeMillis();

        if (t > ct) {
            return (long) (((double) t - this.getCurrentTimeMillis()) / this.acceleration);
        }

        if (t <= ct) {
            // System.out.printf("Time is overslipping: %d\n",ct-t);
            this.currentTimeMillis = t;
            this.current_nanos = this.currentTimeMillis * 1000000.0;
        }

        LinkedHashSet<Event> s = eventQueue.remove(t);

        //  System.out.printf("TIME: %d %d \n",t, s.size());
        for (Event e : s) {
            e.eventProcessor.processEvent(t, e);
        }
        

        
        return 0;

    }
    
    

    
    
    
    
    
    
    

    class EmptyCtr implements EventProcessor {

        @Override
        public long processEvent(long t, Event e) {
            //    System.out.printf("EventProcessor 1000\n");
            addEvent(1000 + getCurrentTimeMillis(), e);
            return 0;
        }

//        @Override
        public long getID() {
            return 999999999999999999L;

        }
    }

    void initScheduler() {
        //      nextTimerTask = new AtomicInteger(0);
        currentTimeMillis = 0.0;
        last_nanos = System.nanoTime();
        this.addEvent(1000, new Event(new EmptyCtr()));
        terminate = false;

    }

    @Override
    public void run() {

        while (!terminate) {
            long wMillis; // = 0;

            synchronized (this) {
                wMillis = runEvents();
            }

            if (wMillis != -1 && !pause) {
                LockSupport.parkNanos(wMillis * 1000 * 1000);
            } else {
                LockSupport.park();
            }
        }
        this.eventQueue.clear();

    }

}
