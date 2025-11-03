/*
 * Copyright (c) 2025, tube
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
package gui;

//import static com.sun.tools.javac.util.Constants.format;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author tube
 */
public class LogPanel extends javax.swing.JPanel {

    public class LogPanelHandler extends Handler {

        private final LogPanel logPanel;

        public LogPanelHandler(LogPanel l) {
            this.logPanel = l;
        }

        @Override
        public void publish(LogRecord record) {
           // System.out.printf("Publish Record %s\n", record.getMessage());
            if (!isLoggable(record)) {
                return;
            }

            String msg = getFormatter().format(record);
            logPanel.appendLog(record.getLevel().getName(), msg /*.stripTrailing()*/);

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }

    }

    public class MessageOnlyFormatter extends Formatter {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private String formatTime(long millis) {
            return dateFormat.format(new Date(millis));
        }

        @Override
        public String format(LogRecord record) {
            String timeStr = formatTime(record.getMillis());
            String levelStr;
            switch (record.getLevel().getName()) {
                case "SEVERE":
                    levelStr = "ERROR";
                    break;
                case "WARNING":
                    levelStr = "WARN";
                    break;
                default:
                    levelStr = record.getLevel().getName(); // INFO, FINE, etc.
            }
            //return "[" + levelStr + "] " + record.getMessage() + "\n";
            return String.format("[%s] [%s] %s\n", timeStr, levelStr, record.getMessage());
        }

    }

    private final Logger logger = Logger.getLogger(sesim.Logger.NAME);

    /**
     * Creates new form LogPanel
     */
    public LogPanel() {
        initComponents();

        /*logArea = new javax.swing.JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false; // Kein Wordwrap → horizontaler Scrollbalken
            }

           @Override
            public boolean getScrollableTracksViewportHeight() {
                return true;
            }

            @Override
            public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
                return 16;
            }

            @Override
            public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
                return 64;
            }


        };*/

        logArea.setEditable(true);
        logArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        // ScrollPane neu konfigurieren
 /*       jScrollPane1.setViewportView(logArea);
        jScrollPane1.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Nachdem du logArea neu erstellt hast:
        logArea.setBackground(Color.WHITE);  // Weißer Text-Hintergrund*/

// WICHTIG: Viewport-Hintergrund auch weiß machen
        //jScrollPane1.getViewport().setBackground(Color.WHITE);

        // Optional: ScrollPane-Rahmen entfernen (falls gewünscht)
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        logger.setLevel(Level.INFO);
        logger.setFilter(record -> {
            Level level = record.getLevel();
            return level == Level.INFO || level == Level.SEVERE;
        });
        LogPanelHandler handler = new LogPanelHandler(this);
        handler.setFormatter(new MessageOnlyFormatter());
        logger.addHandler(handler);

    }

    public void appendLog(String level, String text) {

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = logArea.getStyledDocument();
            javax.swing.text.Style style = logArea.addStyle("Style", null);

            // Farben nach Level
            switch (level) {
                case "SEVERE":
                    StyleConstants.setForeground(style, Color.RED);
                    break;
                case "WARN":
                    StyleConstants.setForeground(style, Color.ORANGE.darker());
                    break;
                default:
                    StyleConstants.setForeground(style, Color.BLACK);
            }

            try {
                doc.insertString(doc.getLength(), text, style);
            } catch (BadLocationException e) {
            }

                      if (true) {
                logArea.setCaretPosition(doc.getLength());
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextPane();

        logArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(logArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane logArea;
    // End of variables declaration//GEN-END:variables
}
