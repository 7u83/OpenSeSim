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
package sesim;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tube
 */
public class TradingLogWriter extends Thread {

    private Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<>();
    private BufferedOutputStream outStream;
    private boolean terminate = false;
    private ByteBuffer buffer = ByteBuffer.allocate(TradingLogRecord.BLOCK_SIZE * 32768);

    public TradingLogWriter(String fileName) throws FileNotFoundException {

        outStream = new BufferedOutputStream(new FileOutputStream(fileName));
        start();

    }

    @Override
    public void run() {

        while (!terminate) {
            ByteBuffer e = queue.poll();

            int i = 0;
            while (e != null) {

                try {
                    e.flip();
                    int n = e.array().length;
                  
                    outStream.write(e.array(), 0, e.limit());
                    i++;
                } catch (IOException ex) {
                   
                    Logger.getLogger(TradingLogWriter.class.getName()).log(Level.SEVERE, null, ex);
                }

                e = queue.poll();

            }
            try {
                this.outStream.flush();
            } catch (IOException ex) {
                Logger.getLogger(TradingLogWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (i == 0) {
                synchronized (this) {
                    if (queue.isEmpty()) {
                        ByteBuffer b = newBuffer();
                        int p = b.position();
                        if (p > 0) {
                            queue.add(b);
                        }
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }


    private ByteBuffer newBuffer() {

        ByteBuffer b = buffer;
        buffer = ByteBuffer.allocate(TradingLogRecord.BLOCK_SIZE * 32768);
        return b;
    }

    /**
     *
     * @param e
     */
    public void add(TradingLogRecord e) {
        long r = buffer.remaining();

        synchronized (this) {
            if (r < TradingLogRecord.BLOCK_SIZE) {
                queue.add(buffer);
                buffer = ByteBuffer.allocate(TradingLogRecord.BLOCK_SIZE * 32768);

            } else {

                e.write(buffer);

            }
        }

        if (queue.size() > 1024) {
            while (queue.size() > 512) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {

                }
            }
        }
    }

    public static class TradingLogRecord implements Serializable {

        private static final int OWNER_SIZE = 8;
        private static final int PARTNER_NAME_SIZE = 8;
        public static final int BLOCK_SIZE = 72;

        public static enum Action {
            CREATE_ORDER,
            CANCEL_ORDER,
            CLOSE_ORDER,
            SELL,
            BUY
        }

        public byte orderType;
        public long time;
        public Action action;
        public long orderID;

        public String owner;
        public float volume;
        public float limit;
        public float stop;

        public float trasaction_volume;
        public float transaction_price;
        public long transaction_partner;
        public String transaction_partner_name;
        float currentPrice;

        public TradingLogRecord() {
        }

        public TradingLogRecord(long time, Action a, Order o) {
            this.time = time;
            this.action = a;
            orderID = o.id;
            orderType = o.getType();
            owner = o.getOwnerName();

            volume = o.getVolume();
            limit = o.getLimit();
            stop = o.getStop();

        }

        public void write(ByteBuffer buffer) {
            long p0 = buffer.position();
            buffer.putLong(time);                  
            buffer.put((byte) action.ordinal()); 
            buffer.put(orderType);              
            buffer.putLong(orderID);            
            buffer.putFloat(limit);             
            buffer.putFloat(stop);             
            buffer.putFloat(volume);            
            buffer.putFloat(trasaction_volume); 
            buffer.putFloat(transaction_price); 
            buffer.putLong(transaction_partner); 
            buffer.putFloat(currentPrice);       
            writePaddedString(buffer, owner, TradingLogRecord.OWNER_SIZE);
            writePaddedString(buffer, transaction_partner_name, TradingLogRecord.PARTNER_NAME_SIZE);
            long p1 = buffer.position();
            writePaddedString(buffer, "", (int) (TradingLogRecord.BLOCK_SIZE - (p1 - p0)));
        }

        private void writePaddedString(ByteBuffer buffer, String s, int size) {

            if (s == null) {
                s = "";
            }
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8); // UTF-8 ist gut für internationale Zeichen

            // Kürzen, falls der String zu lang ist
            int length = Math.min(bytes.length, size);

            // String-Bytes schreiben
            buffer.put(bytes, 0, length);

            // Restliche Bytes mit Nullen (Padding) auffüllen
            for (int i = length; i < size; i++) {
                buffer.put((byte) 0);
            }
        }

        /**
         * Liest ein TradingLogEntry-Objekt aus dem gegebenen ByteBuffer. Muss
         * die umgekehrte Reihenfolge von write() verwenden.
         *
         * @param buffer Der ByteBuffer, der die 72 Bytes eines Eintrags
         * enthält.
         * @return Das neu erstellte TradingLogEntry-Objekt.
         */
        public static TradingLogRecord read(ByteBuffer buffer) {
            TradingLogRecord entry = new TradingLogRecord();

            // 1. Lesen in exakt der Reihenfolge der write()-Methode
            entry.time = buffer.getLong();           // 8

            // Enum: Als byte gespeichert und konvertiert
            byte actionOrdinal = buffer.get();       // 1
            // Fehlerbehandlung: Sicherstellen, dass der Ordinalwert gültig ist
            if (actionOrdinal >= 0 && actionOrdinal < Action.values().length) {
                entry.action = Action.values()[actionOrdinal];
            } else {
                // Optional: Behandlung ungültiger Werte, z.B. Standardwert setzen
                entry.action = Action.CREATE_ORDER;
            }

            entry.orderType = buffer.get();
            entry.orderID = buffer.getLong();
            entry.limit = buffer.getFloat();
            entry.stop = buffer.getFloat();
            entry.volume = buffer.getFloat();
            entry.trasaction_volume = buffer.getFloat();
            entry.transaction_price = buffer.getFloat();
            entry.transaction_partner = buffer.getLong();
            entry.currentPrice = buffer.getFloat();

            // 2. Strings mit fester Länge lesen (muss 8 Bytes lesen)
            entry.owner = readPaddedString(buffer, OWNER_SIZE);
            entry.transaction_partner_name = readPaddedString(buffer, PARTNER_NAME_SIZE);

            // 3. Padding überspringen (falls die BLOCK_SIZE größer ist als die gelesenen Bytes)
            // Hier: 72 (BLOCK_SIZE) - 66 (gelesene Bytes) = 6 Bytes Padding.
            // Der get-Vorgang liest automatisch weiter, bis der Buffer-Abschnitt erschöpft ist.
            return entry;
        }

        /**
         * Liest einen String fester Länge und entfernt das Null-Padding.
         */
        private static String readPaddedString(ByteBuffer buffer, int size) {
            byte[] bytes = new byte[size];
            buffer.get(bytes);

            int length = 0;
            for (int i = 0; i < size; i++) {
                if (bytes[i] == 0) {
                    break;
                }
                length++;
            }

            return new String(bytes, 0, length, StandardCharsets.UTF_8);
        }

    }
    
    public void close(){
        terminate=true;
        try {
            outStream.close();
        } catch (IOException ex) {
            Logger.getLogger(TradingLogWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
