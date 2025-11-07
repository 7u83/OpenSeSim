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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

/**
 *
 * @author tube
 */
public class TradingLogReader {

    private final FileChannel readChannel;
    private final RandomAccessFile raf;

    public TradingLogReader(String fileName) throws FileNotFoundException {
        this.raf = new RandomAccessFile(fileName, "r");
        this.readChannel = raf.getChannel();

    }

    /**
     * Ermittelt die aktuelle Anzahl der vollständig geschriebenen Datensätze in
     * der Datei.
     *
     * * @return Die Anzahl der Datensätze oder 0, wenn der Kanal nicht
     * geöffnet ist.
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
        return fileSize / TradingLogWriter.TradingLogRecord.BLOCK_SIZE;
    }

    /**
     * Holt den n-ten Datensatz aus der Datei (1-basiert) über den persistenten
     * Kanal.
     *
     * @param n Der Index des Datensatzes, der gelesen werden soll (z.B. 1 für
     * den ersten).
     * @return Der TradingLogEntry an der Position n, oder null bei Fehler/Ende.
     */
    public TradingLogWriter.TradingLogRecord get(long n) {
        // Prüfen, ob der Kanal überhaupt geöffnet ist
        if (n < 0 || readChannel == null || !readChannel.isOpen()) {
            return null;
        }

        // Keine try-with-resources oder finally Blöcke zum Schließen des Kanals nötig!
        try {
            // 1. Zielposition berechnen (n - 1 * feste BLOCK_SIZE)
            long position = (n - 1) * TradingLogWriter.TradingLogRecord.BLOCK_SIZE;

            // 2. Prüfen, ob die Datei überhaupt so lang ist (Dateigröße kann sich ändern, da geschrieben wird)
            if (position + TradingLogWriter.TradingLogRecord.BLOCK_SIZE > readChannel.size()) {
                System.out.printf("Datensatz %d existiert noch nicht oder ist unvollständig (Dateigröße: %d).%n",
                        n, readChannel.size());
                return null;
            }

            // 3. Lese-Position im Channel setzen (Seek)
            readChannel.position(position);

            // 4. Buffer für genau einen Datensatz erstellen
            ByteBuffer recordBuffer = ByteBuffer.allocate(TradingLogWriter.TradingLogRecord.BLOCK_SIZE);

            // 5. Lesen des Datensatzes
            int bytesRead = readChannel.read(recordBuffer);

            if (bytesRead != TradingLogWriter.TradingLogRecord.BLOCK_SIZE) {
                System.out.println("Fehler: Nur ein unvollständiger Datensatz gelesen.");
                return null;
            }

            // 6. Buffer zum Lesen umschalten und Entry erstellen
            recordBuffer.flip();
            return TradingLogWriter.TradingLogRecord.read(recordBuffer);

        } catch (IOException e) {
            java.util.logging.Logger.getLogger(TradingLogWriter.class.getName()).log(Level.SEVERE, "Fehler beim Lesen des Datensatzes " + n, e);
            return null;
        }
    }

}
