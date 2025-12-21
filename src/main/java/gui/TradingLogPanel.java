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
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import sesim.Order;
import sesim.TradingLogReader;
import sesim.TradingLogWriter;
import sesim.TradingLogWriter.TradingLogRecord;

/**
 *
 * @author tube
 */
public class TradingLogPanel extends javax.swing.JPanel {

    private Timer updateTimer; // Der Timer für die regelmäßige Aktualisierung
    private long lastLogSize = 0; // Speichert die letzte bekannte Größe
    private TradingLogModel logModel; // Referenz auf das Tabellenmodell
    
    /**
     * Creates new form TradingLogPanel
     */
    public TradingLogPanel() {
        initComponents();
        if (Globals.sim == null) {
            return;
        }
        
        // 1. Initialisiere das Modell und setze es für die Tabelle
        logModel = new TradingLogModel();
        table.setModel(logModel);

        // 2. Initialisiere und starte den Timer (3 Mal pro Sekunde = 333 Millisekunden)
        int delay = 1000 / 6; // Verzögerung in Millisekunden (ca. 333 ms)
        updateTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAndUpdateLog();
            }
        });
        updateTimer.start();
        
    }
    
    /**
     * Überprüft die aktuelle Größe des TradingLogs und aktualisiert das
     * Tabellenmodell präzise (Einfügungen oder Löschungen), um die Selektion
     * beizubehalten, wenn Zeilen nur am Ende hinzugefügt oder entfernt werden.
     */
    private void checkAndUpdateLog() {
        if (Globals.sim == null || Globals.sim.getDefaultMarket() == null) {
            return;
        }
        
        try {
            TradingLogReader logReader = Globals.sim.getDefaultMarket().getTradingLog();
            if (logReader != null) {
                long currentSize = logReader.size();
                
                if (currentSize != lastLogSize) {
                    
                    if (currentSize > lastLogSize) {
                        // FALL 1: ZEILEN HINZUGEFÜGT
                        // Verwenden von fireTableRowsInserted, um die Selektion zu erhalten
                        int firstRow = (int) lastLogSize;
                        int lastRow = (int) (currentSize - 1);
                        logModel.fireTableRowsInserted(firstRow, lastRow);
                        
                    } else if (currentSize < lastLogSize) {
                        // FALL 2: ZEILEN GELÖSCHT (z.B. Log zurückgesetzt auf 0)
                        // Verwenden von fireTableRowsDeleted, um die Selektion der verbleibenden Zeilen zu erhalten
                        int firstRow = (int) currentSize; // Die erste Zeile, die nicht mehr existiert
                        int lastRow = (int) (lastLogSize - 1); // Die letzte Zeile, die vorher existierte
                        
                        logModel.fireTableRowsDeleted(firstRow, lastRow);
                    }
                    
                    // Aktualisiere die zuletzt gelesene Größe
                    lastLogSize = currentSize;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TradingLogPanel.class.getName()).log(Level.WARNING, "TradingLogReader file not found.", ex);
        } catch (IOException ex) {
            Logger.getLogger(TradingLogPanel.class.getName()).log(Level.SEVERE, "IO Error when calling TradingLogReader.size()", ex);
        }
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
        table = new javax.swing.JTable();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Time", "Action", "Order ID", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Long.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    class TradingLogModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            TradingLogReader l = null;
            try {
                l = Globals.sim.getDefaultMarket().getTradingLog();
            } catch (FileNotFoundException ex) {
                return 0;
            }
            if (l==null)
                return 0;
            
            int rows = 0;
            try {
                rows = (int) l.size();
            } catch (IOException ex) {
               return 0;
            }
            return rows;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TradingLogReader l = null;
            try {
                l = Globals.sim.getDefaultMarket().getTradingLog();
            } catch (FileNotFoundException ex) {
                return null;
            }
            TradingLogRecord e = l.get(rowIndex + 1);
            switch (columnIndex) {
                case 0:
                    return e.time;
                case 1:
                    return e.action.toString();
                case 2:
                    return e.owner;
                case 3:
                    return e.orderID;
                case 4:
                    return Order.getTypeAsString(e.orderType);
                case 5:
                    return e.volume;
                case 6:
                    return e.limit;
                case 7:
                    return e.stop;
                case 8:
                    return e.trasaction_volume;
                case 9:
                    return e.transaction_price;

                default:
                    return null;
            }

            // }
        }

        /*     @Override
        public void fireTableRowsUpdated(int firstRow, int lastRow) {
        }

        @Override
        public void fireTableCellUpdated(int row, int column) {
        }
         */
        @Override
        public int getColumnCount() {
            return 10;
        }

        final String colNames[] = {
            "Time",
            "Action",
            "Trader",
            "OID",
            "Type",
            "Volume",
            "Limit",
            "Stop",
            "Trans Vol",
            "Trans Price"

        };
        final Class[] colTypes = new Class[]{
            java.lang.Long.class,
            java.lang.String.class,
            java.lang.String.class,
            java.lang.Long.class, //oid
            java.lang.String.class,
            java.lang.Float.class, // vol
            java.lang.Float.class, // limit
            java.lang.Float.class, // stop
            java.lang.Float.class, // limit
            java.lang.Float.class // stop                
        };

        @Override
        public String getColumnName(int column) {
            return colNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return colTypes[columnIndex];

        }

    }

}
