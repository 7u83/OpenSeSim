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

    private final SortedMap<Long, SortedSet<TimerEvent>> event_queue = new TreeMap<>();
    private boolean stop = false;

    public void halt() {
        stop = true;
        synchronized (event_queue) {
            event_queue.notifyAll();
        }
    }

    public interface TimerEvent {

        long timerEvent();
    }

    private class ObjectComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
    }

    public long getCurrentTimeMillies() {
        return System.currentTimeMillis();

    }

    public void startEvent(TimerEvent e, long time) {
        long evtime = time + this.getCurrentTimeMillies();
        synchronized (event_queue) {
            SortedSet<TimerEvent> s = event_queue.get(evtime);
            if (s == null) {
                s = new TreeSet<>(new ObjectComparator());
                event_queue.put(evtime, s);
            }
            s.add(e);
        }
        synchronized (this) {
            notify();
        }
    }

    public long fireEvent(TimerEvent e) {
        return e.timerEvent();
    }

    private void addEvent(TimerEvent e, long time) {

        long evtime = time + this.getCurrentTimeMillies();

        SortedSet<TimerEvent> s = event_queue.get(evtime);
        if (s == null) {
            s = new TreeSet<>(new ObjectComparator());
            event_queue.put(evtime, s);
        }
        s.add(e);

    }

    public long runEvents() {
        synchronized (event_queue) {
            if (event_queue.isEmpty()) {
                return -1;
            }


            long t = event_queue.firstKey();
            if (t <= this.getCurrentTimeMillies()) {
                SortedSet s = event_queue.get(t);

                event_queue.remove(t);
                Iterator<TimerEvent> it = s.iterator();
                while (it.hasNext()) {

                    TimerEvent e = it.next();
                    long next_t = this.fireEvent(e);
                    this.addEvent(e, next_t);
                }
                return 0;

            } else {
                return t - this.getCurrentTimeMillies();
            }
        }

    }

    @Override
    public void run() {
        while (!stop) {
            long wtime = runEvents();
            if (wtime == 0) {
                continue;
            }

            synchronized (this) {
                try {
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
