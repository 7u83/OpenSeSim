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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tube
 */
public class TradingLog extends Thread {

    Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<>();
  private final String fileName; // Speichern des Dateinamens
    private BufferedOutputStream outStream;
        private RandomAccessFile raf;
    private FileChannel readChannel; 

    //  private final ExecutorService executor = Executors.newSingleThreadExecutor();
    boolean terminate = false;

    public TradingLog(String fileName) throws FileNotFoundException {
   
this.fileName = fileName; // Dateinamen speichern
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();  // alte DB entfernen
        }

        this.outStream = new BufferedOutputStream(new FileOutputStream(fileName));
        
                // 2. Lese-Ressourcen dauerhaft öffnen (Random Access)
        this.raf = new RandomAccessFile(fileName, "r");
        this.readChannel = raf.getChannel();
        
             start();

    }
    
     /**
     * Ermittelt die aktuelle Anzahl der vollständig geschriebenen Datensätze in der Datei.
     * * @return Die Anzahl der Datensätze oder 0, wenn der Kanal nicht geöffnet ist.
     * @throws IOException Wenn beim Abrufen der Dateigröße ein Fehler auftritt.
     */
    public long size() throws IOException {
        if (readChannel == null || !readChannel.isOpen()) {
            return 0; 
        }
        
        // Die aktuelle Größe der Datei in Bytes
        long fileSize = readChannel.size();
        
        // Die Anzahl der Einträge ergibt sich aus der Gesamtgröße geteilt durch die feste Blockgröße.
        // Die Ganzzahl-Division ignoriert Bytes aus einem potenziell unvollständigen letzten Eintrag.
        return fileSize / TradingLogEntry.BLOCK_SIZE;
    }


    @Override
    public void run() {

        while (!terminate) {
            //TradingLogEntry e = queue.poll();

            ByteBuffer e = queue.poll();
            //  ByteBuffer buffer = ByteBuffer.allocate(TradingLogEntry.BLOCK_SIZE);
            int i = 0;
            while (e != null) {

                try {
                    //              buffer.clear();
                    //            e.write(buffer);
                    e.flip();
                 //   outStream.write(e.array());
                    int n = e.array().length;
                    System.out.printf("Writing length %d\n", n);
                    outStream.write(e.array(),0,e.limit());
                    
                    i++;
                    // System.out.printf("Wrote an entry\n");
                } catch (IOException ex) {
                    System.out.printf("Bad exception\n");
                    Logger.getLogger(TradingLog.class.getName()).log(Level.SEVERE, null, ex);
                }

                e = queue.poll();

            }
            try {
                this.outStream.flush();
            } catch (IOException ex) {
                Logger.getLogger(TradingLog.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (i == 0) {
                synchronized (this) {
                    if (queue.isEmpty()) {
                        ByteBuffer b = newBuffer();
                        int p = b.position();
                      //  System.out.printf("POSITIN: %d\n", p);
                        if (p > 0) {
                            queue.add(b);
                        }
                    }
                }
            }

          //  System.out.printf("Queue ist Empty after %d\n", i);
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
            //  LockSupport.park();
        }
    }

    ByteBuffer buffer = ByteBuffer.allocate(TradingLogEntry.BLOCK_SIZE * 32768);

    private ByteBuffer newBuffer() {

        ByteBuffer b = buffer;
        buffer = ByteBuffer.allocate(TradingLogEntry.BLOCK_SIZE * 32768);
        return b;
    }

    /**
     *
     * @param e
     */
    public void add(TradingLogEntry e) {
        long r = buffer.remaining();

        synchronized (this) {
            if (r < TradingLogEntry.BLOCK_SIZE) {
                queue.add(buffer);
                buffer = ByteBuffer.allocate(TradingLogEntry.BLOCK_SIZE * 32768);

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
    
    
    /**
     * Holt den n-ten Datensatz aus der Datei (1-basiert) über den persistenten Kanal.
     * @param n Der Index des Datensatzes, der gelesen werden soll (z.B. 1 für den ersten).
     * @return Der TradingLogEntry an der Position n, oder null bei Fehler/Ende.
     */
    public TradingLogEntry get(long n) {
        // Prüfen, ob der Kanal überhaupt geöffnet ist
        if (n < 0 || readChannel == null || !readChannel.isOpen()) {
            return null;
        }
        
        // Keine try-with-resources oder finally Blöcke zum Schließen des Kanals nötig!
        try {
            // 1. Zielposition berechnen (n - 1 * feste BLOCK_SIZE)
            long position = (n - 1) * TradingLogEntry.BLOCK_SIZE;
            
            // 2. Prüfen, ob die Datei überhaupt so lang ist (Dateigröße kann sich ändern, da geschrieben wird)
            if (position + TradingLogEntry.BLOCK_SIZE > readChannel.size()) {
                System.out.printf("Datensatz %d existiert noch nicht oder ist unvollständig (Dateigröße: %d).%n", 
                                   n, readChannel.size());
                return null;
            }

            // 3. Lese-Position im Channel setzen (Seek)
            readChannel.position(position);

            // 4. Buffer für genau einen Datensatz erstellen
            ByteBuffer recordBuffer = ByteBuffer.allocate(TradingLogEntry.BLOCK_SIZE);

            // 5. Lesen des Datensatzes
            int bytesRead = readChannel.read(recordBuffer);

            if (bytesRead != TradingLogEntry.BLOCK_SIZE) {
                 System.out.println("Fehler: Nur ein unvollständiger Datensatz gelesen.");
                 return null;
            }

            // 6. Buffer zum Lesen umschalten und Entry erstellen
            recordBuffer.flip();
            return TradingLogEntry.read(recordBuffer);

        } catch (IOException e) {
            Logger.getLogger(TradingLog.class.getName()).log(Level.SEVERE, "Fehler beim Lesen des Datensatzes " + n, e);
            return null;
        }
    }

    
  


    public static class TradingLogEntry implements Serializable {

        private static final int OWNER_SIZE = 8;
        private static final int PARTNER_NAME_SIZE = 8;
        private static final int BLOCK_SIZE = 72;

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

        public TradingLogEntry() {
        }

        public TradingLogEntry(long time, Action a, Order o) {
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
            buffer.putLong(time);               // 8    
            buffer.put((byte) action.ordinal()); // 1
            buffer.put(orderType);              // 1
            buffer.putLong(orderID);            // 8
            buffer.putFloat(limit);             // 4
            buffer.putFloat(stop);              // 4
            buffer.putFloat(volume);            // 4
            buffer.putFloat(trasaction_volume); // 4
            buffer.putFloat(transaction_price); // 4
            buffer.putLong(transaction_partner); // 8
            buffer.putFloat(currentPrice);       // 4
            writePaddedString(buffer, owner, TradingLogEntry.OWNER_SIZE);
            writePaddedString(buffer, transaction_partner_name, TradingLogEntry.PARTNER_NAME_SIZE);
            long p1 = buffer.position();
            writePaddedString(buffer,"", (int) (TradingLogEntry.BLOCK_SIZE-(p1-p0)));
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
        public static TradingLogEntry read(ByteBuffer buffer) {
            TradingLogEntry entry = new TradingLogEntry();

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

            entry.orderType = buffer.get();          // 1
            entry.orderID = buffer.getLong();        // 8
            entry.limit = buffer.getFloat();         // 4
            entry.stop = buffer.getFloat();          // 4
            entry.volume = buffer.getFloat();        // 4
            entry.trasaction_volume = buffer.getFloat(); // 4
            entry.transaction_price = buffer.getFloat(); // 4
            entry.transaction_partner = buffer.getLong(); // 8
            entry.currentPrice = buffer.getFloat();  // 4

            // 2. Strings mit fester Länge lesen (muss 8 Bytes lesen)
            entry.owner = readPaddedString(buffer, OWNER_SIZE);
            entry.transaction_partner_name = readPaddedString(buffer, PARTNER_NAME_SIZE);

            // 3. Padding überspringen (falls die BLOCK_SIZE größer ist als die gelesenen Bytes)
            // Hier: 72 (BLOCK_SIZE) - 66 (gelesene Bytes) = 6 Bytes Padding.
            // Der get-Vorgang liest automatisch weiter, bis der Buffer-Abschnitt erschöpft ist.
            return entry;
        }
        
           /** Liest einen String fester Länge und entfernt das Null-Padding. */
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
}
