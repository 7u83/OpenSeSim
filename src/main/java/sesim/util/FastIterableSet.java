/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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
package sesim.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author tube
 */
public class FastIterableSet<T extends FastIterableSet.Node<T>> {

    // Das Event selbst trägt seinen eigenen next-Zeiger
    public static interface Node<T> {
        T getNext();
        void setNext(T next);
    }

    private T head = null;      // erstes Element
    private T tail = null;      // letztes Element (für schnelles anhängen)

    private final Map<Object, T> map;   // key → Node (meist das Event selbst als Key)

    public FastIterableSet() {
        this.map = new HashMap<>();
    }

    public FastIterableSet(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    public void add(T event) {
        if (map.putIfAbsent(event, event) == null) {   // nur wenn noch nicht drin
            if (tail == null) {
                head = tail = event;
            } else {
                tail.setNext(event);
                tail = event;
            }
            event.setNext(null);
        }
    }

    public boolean remove(Object key) {
        T node = map.remove(key);
        return node != null;
        // ← Wir klinken ihn NICHT aus der Kette aus! Das passiert später beim Durchlauf
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private T current = head;
            private T nextValid = null;

            private void advance() {
                while (current != null && !map.containsKey(current)) {
                    current = current.getNext();
                }
                nextValid = current;
            }

            @Override public boolean hasNext() {
                if (nextValid == null) advance();
                return nextValid != null;
            }

            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T result = nextValid;
                current = result.getNext();   // einen weiter in der Originalkette
                nextValid = null;             // zwingt advance() beim nächsten Aufruf
                return result;
            }
        };
    }

    // Optional: nach der Iteration wieder sauber machen (kostet O(n), aber nur 1× pro Frame
    public void compact() {
        if (head == null) return;
        T prev = null;
        T curr = head;
        while (curr != null) {
            if (map.containsKey(curr)) {
                if (prev != null) prev.setNext(curr);
                prev = curr;
            }
            curr = curr.getNext();
        }
        head = map.isEmpty() ? null : (T)map.values().iterator().next(); // oder prev behalten
        tail = prev;
        if (tail != null) tail.setNext(null);
    }
}