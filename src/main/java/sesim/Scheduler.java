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
import java.util.Comparator;
import java.util.Date;
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

    private double acceleration = 0.0;

    public void setAcceleration(double val) {

        this.acceleration = val;
        synchronized (this) {
            this.notify();
        }
    }

    public double getAcceleration() {

        return this.acceleration;
    }

    private final SortedMap<Long, SortedSet<TimerTask>> event_queue = new TreeMap<>();

    public interface TimerTask {

        long timerTask();

        long getID();
    }

    private boolean terminate = false;

    /**
     * Terminate the scheduler
     */
    public void terminate() {
        terminate = true;
        synchronized (event_queue) {
            event_queue.notifyAll();
        }

    }

    @Override
    public void start() {
        if (this.isAlive()) {
            return;
        }
        this.initScheduler();
        super.start();

    }

    private class ObjectComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {

            return (((TimerTask) o1).getID() - ((TimerTask) o2).getID()) < 0 ? -1 : 1;
            //return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
    }

    long last_time_millis = System.currentTimeMillis();
    double current_time_millis = 0.0;

    /**
     *
     * @return
     */
    public long currentTimeMillis1() {

        long diff = System.currentTimeMillis() - last_time_millis;
        last_time_millis += diff;
        if (diff==0)
            diff++;     
        if (pause) {
            return (long) this.current_time_millis;
        }
        this.current_time_millis += ((double)diff) * this.acceleration;
        return (long) this.current_time_millis;
    }

    public long currentTimeMillis() {
        return (long) this.current_time_millis;
    }

    static public String formatTimeMillis(long t) {
        Date date = new Date(t);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);

        long seconds = (t / 1000) % 60;
        long minutes = (t / 1000 / 60) % 60;
        long hours = (t / 1000) / (60 * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    /**
     *
     * @param e
     * @param time
     */
    public void startTimerEvent(TimerTask e, long time) {
        long evtime = time + currentTimeMillis();
        synchronized (event_queue) {
            this.addEvent(e, evtime);
        }
        synchronized (this) {
            notify();
        }
    }

    private boolean pause = false;

    public void pause() {
        setPause(!pause);

    }
    
    public void setPause(boolean val){
        pause=val;
        synchronized (this) {
            this.notify();
        }

    }
    
    public boolean getPause(){
        return pause;
    }

    public long fireEvent(TimerTask e) {
        return e.timerTask();
    }

    private boolean addEvent(TimerTask e, long evtime) {

        //   long evtime = time + currentTimeMillis();
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
            long ct = currentTimeMillis1();

            if (t <= ct) {
                this.current_time_millis = t;
                SortedSet s = event_queue.get(t);
                Object rc = event_queue.remove(t);
                Iterator<TimerTask> it = s.iterator();
                while (it.hasNext()) {
                    TimerTask e = it.next();
                    long next_t = this.fireEvent(e);
                    if (next_t == 0) {
                        next_t++;
                    }

                    this.addEvent(e, next_t + t);
                }
                return 0;

            } else {
                return(t - currentTimeMillis())/(long)this.acceleration;
            }
        }

    }

    void initScheduler() {
        current_time_millis = 0.0;
        terminate = false;

    }

    @Override
    public void run() {

        while (!terminate) {

            long wtime = runEvents();

            if (wtime == 0) {
                continue;
            }

            synchronized (this) {
                try {

                    if (wtime != -1 && !pause) {
                        wait(wtime);
                    } else {
                        wait();
                    }
                } catch (Exception e) {

                }
            }
        }

        this.event_queue.clear();

    }

}
