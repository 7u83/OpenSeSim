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
import java.util.*;

/**
 *
 * @author tube
 */


public final class FastEventSet<T> implements Iterable<T> {

    private final ArrayList<T> order = new ArrayList<>();           // behält Reihenfolge
    private final IdentityHashMap<T, Boolean> alive = new IdentityHashMap<>(); // nur Anwesenheit

    public void add(T event) {
        if (alive.put(event, Boolean.TRUE) == null) {   // war noch nicht drin
            order.add(event);
        }
    }

    public boolean remove(T event) {
        return alive.remove(event) != null;
    }

    public int size() {
        return alive.size();
    }

    public boolean isEmpty() {
        return alive.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                while (pos < order.size() && !alive.containsKey(order.get(pos))) {
                    pos++;
                }
                return pos < order.size();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T event = order.get(pos);
                pos++;
                return event;
            }
        };
    }

    // Nur nötig, falls du die Instanz wiederverwenden willst
    public void clear() {
        order.clear();
        alive.clear();
    }
}