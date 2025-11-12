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
package gui;

import gui.tools.NummericCellRenderer;
import gui.tools.PercentageCellRenderer;
import gui.tools.PercentageValue;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.RIGHT;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;
import sesim.Account;

import sesim.AutoTraderInterface;
import sesim.Quote;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class TraderListPanel extends javax.swing.JPanel {

    DefaultTableModel model;
    Frame parentFrame = null;

    public TraderListPanel(Frame parent) {
        this();
        parentFrame = parent;
    }

    final void updateModel() {
        if (Globals.sim == null) {
            return;
        }

        if (Globals.sim.se == null) {
            return;
        }

        if (Globals.sim.traders == null) {
            return;
        }

        //   Quote q = Globals.sim.se.getLastQuoete();
        //   float price = q == null ? 0 : q.getPrice();
        int size = Globals.sim.traders.size();
        float price = Globals.sim.se.getLastPrice();
        model.setRowCount(size);
        for (int i = 0; i < size; i++) {
            AutoTraderInterface at = Globals.sim.traders.get(i);
            Account a = at.getAccount();
            model.setValueAt(i, i, 0);

            //model.setValueAt(at.getName(), i, 1);
            int[] atc = at.getColor();
            Color c = null;
            if (atc != null) {
                c = new Color(atc[0], atc[1], atc[2]);
            }

            model.setValueAt(new NameValue(at.getName(), c), i, 1);

            model.setValueAt(at.getStatus(), i, 2);
            model.setValueAt(a.getMoney(), i, 3);
            model.setValueAt(a.getShares(), i, 4);

            float wealth = a.getShares() * price + a.getMoney();
            model.setValueAt(wealth, i, 5);

            model.setValueAt(new PercentageValue(a.getPerformance(price)), i, 6);

        }
        List l = list.getRowSorter().getSortKeys();
        if (!l.isEmpty()) {
            list.getRowSorter().allRowsChanged();
        } else {
            model.fireTableDataChanged();
        }

    }

    TimerTask updater;

    /**
     * Creates new form TraderListPanel2
     */
    public TraderListPanel() {

        initComponents();
        if (Globals.sim == null) {
            return;
        }

        model = (DefaultTableModel) list.getModel();
//       updateModel();
        AtomicBoolean running = new AtomicBoolean(false);
        Timer timer = new Timer();
        updater = new TimerTask() {

            @Override
            public void run() {
                if (running.get()) {
                    return;
                }
                running.set(true);
                try {
                    updateModel();
                } catch (Exception e) {
                }
                running.set(false);

            }
        };

        list.getColumnModel().getColumn(3).setCellRenderer(
                new NummericCellRenderer(Globals.sim.se.getMoneyDecimals())
        );    // Cash
        list.getColumnModel().getColumn(4).setCellRenderer(
                new NummericCellRenderer(Globals.sim.se.getSharesDecimals())
        );    // Shares
        list.getColumnModel().getColumn(5).setCellRenderer(
                new NummericCellRenderer(Globals.sim.se.getMoneyDecimals())
        );    // Total

        list.getColumnModel().getColumn(6).setCellRenderer(
                new PercentageCellRenderer()
        );
        
                list.getColumnModel().getColumn(1).setCellRenderer(
                new NameCellRenderer()
        );


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // set width for id
        list.getColumnModel().getColumn(0).setPreferredWidth(50);
        list.getColumnModel().getColumn(0).setMinWidth(30);
        list.getColumnModel().getColumn(0).setMaxWidth(70);

        // --- Berechne Breite für "-100.0%" ---
        String widestText = "-100.0%"; // Der breiteste mögliche Wert
        FontMetrics fm = list.getFontMetrics(list.getFont()); // Font der Tabelle
        int textWidth = fm.stringWidth(widestText);

        int padding = 12; // ca. 6px links + 6px rechts (anpassen nach Look&Feel)
        int preferredWidth = textWidth + padding;
        int maxWidth = 100 * preferredWidth;
        // set width for id
        list.getColumnModel().getColumn(6).setPreferredWidth(preferredWidth);
        list.getColumnModel().getColumn(6).setMinWidth(preferredWidth);
        list.getColumnModel().getColumn(6).setMaxWidth(maxWidth);
        timer.schedule(updater, 0, 1000);

    }

    class MyModel extends DefaultTableModel {

        MyModel(Object arg0[][], Object arg1[]) {
            super(arg0, arg1);
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
        }

        @Override
        public void fireTableStructureChanged() {

        }

        @Override
        public void fireTableRowsUpdated(int firstRow, int lastRow) {
        }

        @Override
        public void fireTableCellUpdated(int row, int column) {

        }

    }



    public class NameValue implements Comparable<NameValue> {

        private final String value;
        private final Color color;

        public NameValue(String value, Color color) {
            this.value = value;
            this.color = color;

        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int compareTo(NameValue other) {
            return this.value.compareTo(other.value);
        }

        public String getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }

    public class NameCellRenderer extends DefaultTableCellRenderer {

        public NameCellRenderer() {
            //setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // value ist jetzt PerfValue!
            NameValue n = (NameValue) value;

            Component c = super.getTableCellRendererComponent(
                    table, n.toString(), isSelected, hasFocus, row, column);

            if (!isSelected) {
                Color color = n.getColor();
                if (color != null) {
                    this.setBackground(n.getColor());
                }
                else{
                    setBackground(table.getBackground());
                }
                /*                Font defaultFont = table.getFont();
                if (Math.abs(performance) > 10) {

                    setFont(defaultFont.deriveFont(Font.BOLD));
                } else {
                    setFont(defaultFont);
                }*/
            }

            return c;
        }
    }

    public class PerfCellRenderer_old extends DefaultTableCellRenderer {

        /**
         *
         * @param formatter
         */
        public PerfCellRenderer_old() {
            this.setHorizontalAlignment(RIGHT);

        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            /*            if (!(value instanceof Float)) {
                return super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
            }*/
            int modelRow = table.convertRowIndexToModel(row);

            Account a = Globals.sim.traders.get(modelRow).getAccount();
            float price = Globals.sim.se.getLastPrice();
            float perf = a.getPerformance(price);
            String performance = String.format("%.1f", perf);

            // Format the cell value as required
            value = performance + "%"; //Globals.sim.traders.get(modelRow).getName(); 
            //"Hallo"; //formatter.format((Number) value);

            // And pass it on to parent class
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (!isSelected) { // Nur wenn nicht selektiert, eigene Farben setzen
                if (perf > 0) {
                    setForeground(new Color(0, 100, 0)); // Dunkelgrün für bessere Lesbarkeit
                } else if (perf < 0) {
                    setForeground(Color.RED);
                } else {
                    setForeground(table.getForeground()); // Standardfarbe bei 0
                }
            }
            return this;
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
        list = new javax.swing.JTable();

        list.setAutoCreateRowSorter(true);
        list.setModel(new MyModel(
            new Object [][] {
            },
            new String [] {
                "ID", "Name","Status", "Cash", "Shares", "Total","Perf."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class,
                NameValue.class,
                java.lang.String.class,
                java.lang.Double.class,
                java.lang.Double.class,
                java.lang.Double.class,
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        list.setDoubleBuffered(true);
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(list);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        if (evt.getClickCount() == 2) {
            int index = list.rowAtPoint(evt.getPoint());

            index = list.getRowSorter().convertRowIndexToModel(index);
            Integer tid = (Integer) model.getValueAt(index, 0);
            // System.out.printf("Trader ID %d\n", tid);

            //  JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JDialog console = Globals.sim.traders.get(tid).getGuiConsole(parentFrame);
            if (console == null) {
                return;
            }
            console.setVisible(true);

        }

    }//GEN-LAST:event_listMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable list;
    // End of variables declaration//GEN-END:variables
}
