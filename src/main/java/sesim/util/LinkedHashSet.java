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

/**
 *
 * @author tube
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author 7u83
 */
public class LinkedHashSet<T> implements Iterable<T> {

    private static class Node<T> {

        T value;
        Node<T> prev, next;

        Node(T v) {
            value = v;
        }
    }

    private final Map<T, Node<T>> map = new HashMap<>();
    private final Node<T> dummy;  // Dummy-Node als Kopf
    private Node<T> tail;         // Letzte echte Node

    public LinkedHashSet() {
        dummy = new Node<>(null);
        tail = dummy; // Initial: tail = dummy, Liste ist leer
    }

    // Fügt Element hinzu, falls noch nicht vorhanden
    public void add(T value) {
        if (map.containsKey(value)) {
            return;
        }
        Node<T> node = new Node<>(value);
        map.put(value, node);
        tail.next = node;
        node.prev = tail;
        tail = node;
    }

    // Entfernt Element, falls vorhanden
    public boolean remove(T value) {
        Node<T> node = map.remove(value);
        if (node == null) {
            return false;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // update tail, falls letztes Element entfernt wurde
        }
        node.prev = null;
        node.next = null;
        node.value = null; 
        return true;
    }

    public int size() {
        return map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> current = dummy; // Start auf Dummy-Node

            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                current = current.next;    // springt auf die nächste Node
                return current.value;      // liefert Value
            }
        };
    }

    // Optional: Debug-Ausgabe
    public void printList() {
        Node<T> n = dummy.next;
        while (n != null) {
            System.out.print(n.value + " ");
            n = n.next;
        }
        System.out.println();
    }
}
