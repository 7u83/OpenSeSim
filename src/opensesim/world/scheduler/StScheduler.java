/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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

import java.util.Date;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class StScheduler extends Thread {

   // private double acceleration = 1.0;
    
    StScheduler(Clock clock){
        this.clock = clock;
    }


    private final SortedMap<Long, LinkedList<Event>> event_queue = new TreeMap<>();
    
    



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
            // thread is already running
            return;
        }
        initScheduler();
        super.start();
    }

/*    private class ObjectComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {

            return (((EventListener) o1).getID() - ((EventListener) o2).getID()) < 0 ? -1 : 1;
            //return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
    }
*/
    
    //long last_time_millis = System.currentTimeMillis();
    //private double current_millis = 0.0;

    //long last_nanos = System.nanoTime();
    //private double current_nanos = 0;

    /**
     *
     * @return
     */
 /*   private long currentTimeMillis1_old() {

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
*/
    Clock clock = new Clock();
    
  /*  public long currentTimeMillis_old() {
        return (long) this.current_millis;
    }
*/
    static public String formatTimeMillis(long t) {
        Date date = new Date(t);
        //    DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        //    String dateFormatted = formatter.format(date);
        //    return dateFormatted;
        long seconds = (t / 1000) % 60;
        long minutes = (t / 1000 / 60) % 60;
        long hours = (t / 1000) / (60 * 60);

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

//    AtomicInteger nextTimerTask = new AtomicInteger(0);

/*    public class TimerTaskDef  {

        EventListener listener;
        Event arg;
        
        long curevtime;
        long newevtime;
        int id;

        TimerTaskDef(EventListener listener, Event arg, long t) {
            this.listener = listener;
            this.arg=arg;
            newevtime = t;
            id = nextTimerTask.getAndAdd(1);

        }


    }
*/
    
    //LinkedList<TimerTaskDef> set_tasks = new LinkedList<>();
    ConcurrentLinkedQueue<Event> new_tasks = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param listener
     * @param time
     * @return The TimerTask created
     */
    public Event startTimerTask(EventListener listener, long time) {

        long evtime = time + clock.currentTimeMillis();

        Event task = new Event(listener, evtime);
        new_tasks.add(task);

        synchronized (this) {
            notify();
        }
        return task;
    }
    
    
    public Event scheduleEvent(Event e){
        new_tasks.add(e);
        synchronized (this) {
            notify();
        }        
        return e;
    }

    public void rescheduleTimerTask(Event task, long time) {
        long evtime = time + clock.currentTimeMillis();
        task.newevtime = evtime;
        new_tasks.add(task);

        synchronized (this) {
            notify();
        }
    }

    private boolean pause = false;

    public void pause() {
        setPause(!pause);

    }

    public void setPause(boolean val) {
        pause = val;
        synchronized (this) {
            this.notify();
        }

    }

    public boolean getPause() {
        return pause;
    }

    public long fireEvent(EventListener e, Event arg) {
        return e.receive(arg);//   .receive(e,arg);
    }

    //  HashMap<TimerTaskDef, Long> tasks = new HashMap<>();
    private boolean addTimerTask(Event e) {

        // System.out.printf("Add TimerTask %d %d\n",e.curevtime,e.newevtime);
        //   long evtime = time + currentTimeMillis();
        LinkedList<Event> s = event_queue.get(e.newevtime);
        if (s == null) {
            s = new LinkedList<>();
            event_queue.put(e.newevtime, s);
        }

        e.curevtime = e.newevtime;

        //      tasks.put(e, e.evtime);
        return s.add(e);
    }

    private final LinkedList<EventListener> cancel_queue = new LinkedList();

    public void cancelTimerTask(EventListener e) {
        cancel_queue.add(e);
    }

    private void cancelMy(Event e) {

//        Long evtime = tasks.get(e.curevtime);
//        if (evtime == null) {
//            return;
//        }
        LinkedList<Event> s = event_queue.get(e.curevtime);
        if (s == null) {
            //   System.out.printf("My not found\n");    
            return;
        }

        Boolean rc = s.remove(e);
             

        if (s.isEmpty()) {

            event_queue.remove(e.curevtime);
        }

    }

    public long runEvents() {
        
      //  System.out.printf("RunEvents in Thread %d\n",Thread.currentThread().getId());
                
        synchronized (event_queue) {

            if (event_queue.isEmpty()) {
                return -1;
            }

            long t = event_queue.firstKey();
            
            long delay = clock.getDelay(t);
            System.out.printf("Delay is %d %s\n", delay,StScheduler.formatTimeMillis(clock.currentTimeMillis()));
            if (delay>0)
                return delay*2;
            

            //  if (t <= ct) {
            LinkedList<Event> s = event_queue.get(t);
            Object rc;
            rc = event_queue.remove(t);

            while (s.size() > 0) {
                Event def = s.pop();
               long next_t = this.fireEvent(def.listener,def);
                if (next_t == -1)
                    continue;   

                def.newevtime = next_t + t;
                this.addTimerTask(def);                
                //System.out.printf("Events in a row: %d\n", s.size());
            }
/*
            Iterator<TimerTaskDef> it = s.iterator();
            while (it.hasNext()) {
                TimerTaskDef def = it.next();


                long next_t = this.fireEvent(def.listener,def.arg);
                if (next_t == -1)
                    continue;
                
                def.newevtime = next_t + t;
                this.addTimerTask(def);
            }
  */          return 0;

        }

    }

    /*
    class EmptyCtr implements EventListener {

        @Override
        public long receive() {
            //   System.out.printf("Current best brice %f\n", Globals.se.getBestPrice());
            return 1000;
        }

        @Override
        public long getID() {
            return 999999999999999999L;
            //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
*/
    
    void initScheduler() {
//        current_millis = 0.0;
   //     this.startTimerTask(new EmptyCtr(), 0);
        terminate = false;

    }

    @Override
    public void run() {

        while (!terminate) {

            while (!new_tasks.isEmpty()) {
                Event td = new_tasks.poll();
                //   System.out.printf("There is a set task %d %d\n",td.curevtime,td.newevtime);

                this.cancelMy(td);
                this.addTimerTask(td);

            }

            long wtime = runEvents();

            if (wtime == 0) {
                continue;
            }

            synchronized (this) {
                try {
//                    System.out.printf("My WTIME %d\n", wtime);
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
