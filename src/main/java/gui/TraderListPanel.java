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

import static gui.TraderListPanel.Column.ID;
import gui.tools.NummericCellRenderer;
import gui.tools.PercentageCellRenderer;
import gui.tools.PercentageValue;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.RIGHT;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sesim.Account;

import sesim.AutoTraderInterface;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class TraderListPanel extends javax.swing.JPanel {

    DefaultTableModel model;
    Frame parentFrame = null;
    TimerTask updater;

    // Definition of columns
    public static enum Column {
        ID("ID", null, Long.class),
        NAME("Name", new NameCellRenderer(), Object.class),
        STATUS("Status", null, String.class),
        SHARES("Shares", null, Float.class),
        MARGIN("Margin", null, Float.class),
        EQUITY("Equtiy", null, Float.class),
        FREEMARGIN("Free Margin",null, Float.class),
        CASH("Cash", null, Float.class),
        PNL("PnL", new PercentageCellRenderer(), PercentageValue.class); // Letzte Spalte

        public final String header;
        private TableCellRenderer renderer;
        public final Class cls;

        Column(String header, TableCellRenderer r, Class cls) {
            this.header = header;
            renderer = r;
            this.cls = cls;
        }
        
        public TableCellRenderer getRenderer() {
        if (renderer == null) {
            switch (this) {
                case SHARES:
                    renderer = new NummericCellRenderer(Globals.sim.getExchange().getSharesDecimals());
                    break;
                case MARGIN:
                case EQUITY:
                case FREEMARGIN:
                case CASH:
                    renderer = new NummericCellRenderer(Globals.sim.getExchange().getMoneyDecimals());
                    break;
                default:
                    // renderer bleibt null oder schon gesetzt
            }
        }
        return renderer;
    }

    }

    public TraderListPanel(Frame parent) {

        this();
        parentFrame = parent;

    }

    Column[] columnList = Column.values();
    
    /*{
        Column.ID, Column.NAME,Column.PNL
    };*/

    public TraderListPanel(Frame parent, Column[] columnList) {
        initComponents();
        if (Globals.sim == null) {
            return;
        }

        this.columnList = columnList;
        setupTable();
    }

    /**
     * Creates new form TraderListPanel2
     */
    public TraderListPanel() {

        initComponents();
        if (Globals.sim == null) {
            return;
        }
        setupTable();

    }

    private void setupTable() {
        model = new MyModel(Column.values());
        list.setModel(model);

        for (Column a : Column.values()) {
            list.getColumnModel().getColumn(a.ordinal()).setHeaderValue(a.header);
            list.getColumnModel().getColumn(a.ordinal()).setIdentifier(a);
            
     //       if (a.getRenderer() != null) {
                list.getColumnModel().getColumn(a.ordinal()).setCellRenderer(a.getRenderer());
     //      }

        }

        TableColumnModel colModel = list.getColumnModel();

        for (int i = colModel.getColumnCount() - 1; i >= 0; i--) {
            TableColumn col = colModel.getColumn(i);
            Column columnEnum = (Column) col.getIdentifier(); // besser Identifier setzen
            if (!Arrays.asList(columnList).contains(columnEnum)) {
                colModel.removeColumn(col);
            }
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.getColumnModel().getColumn(Column.ID.ordinal()).setCellRenderer(centerRenderer);

        String widestText = "-100.0%"; // Der breiteste mögliche Wert
        FontMetrics fm = list.getFontMetrics(list.getFont()); // Font der Tabelle
        int textWidth = fm.stringWidth(widestText);

        int padding = 12; // ca. 6px links + 6px rechts (anpassen nach Look&Feel)
        int preferredWidth = textWidth + padding;
        int maxWidth = 100 * preferredWidth;
        // set width for id
/*        list.getColumnModel().getColumn(Column.PNL.ordinal()).setPreferredWidth(preferredWidth);
        list.getColumnModel().getColumn(Column.PNL.ordinal()).setMinWidth(preferredWidth);
        list.getColumnModel().getColumn(Column.PNL.ordinal()).setMaxWidth(maxWidth);
*/
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
        timer.schedule(updater, 0, 1000);
    }

    final void updateModel() {
        if (Globals.sim == null) {
            return;
        }

        if (Globals.sim.getExchange() == null) {
            return;
        }

        if (Globals.sim.traders == null) {
            return;
        }

        //   Quote q = Globals.sim.getExchange().getLastQuoete();
        //   float price = q == null ? 0 : q.getPrice();
        int size = Globals.sim.traders.size();
        float price = Globals.sim.getExchange().getLastPrice();
        model.setRowCount(size);
        for (int i = 0; i < size; i++) {
            AutoTraderInterface at = Globals.sim.traders.get(i);
            Account a = at.getAccount();

            model.setValueAt(i, i, Column.ID.ordinal());

            int[] atc = at.getColor();
            Color c = null;
            if (atc != null) {
                c = new Color(atc[0], atc[1], atc[2]);
            }
            model.setValueAt(new NameValue(at.getName(), c), i, Column.NAME.ordinal());

            model.setValueAt(at.getStatus(), i, Column.STATUS.ordinal());
            model.setValueAt(a.getMoney(), i, Column.CASH.ordinal());
            model.setValueAt(a.getShares(), i, Column.SHARES.ordinal());

            //  float wealth = a.getShares() * price + a.getMoney();
            model.setValueAt(a.getEquity(), i, Column.EQUITY.ordinal());
            model.setValueAt(a.getMarginUsed(), i, Column.MARGIN.ordinal());
            model.setValueAt(a.getFreeMargin(), i, Column.FREEMARGIN.ordinal());
            model.setValueAt(new PercentageValue(a.getPerformance(price)), i, Column.PNL.ordinal());

        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Long selectedTraderId = null;
                int selectedViewRow = list.getSelectedRow();

                // Überprüfe, ob überhaupt eine Zeile markiert ist
                if (selectedViewRow != -1) {
                    // Konvertiere die Ansichts-Zeilennummer in die Model-Zeilennummer
                    int selectedModelRow = list.convertRowIndexToModel(selectedViewRow);

                    // Hole die ID aus dem Model (Spalte Column.ID hat Index 0)
                    Object value = model.getValueAt(selectedModelRow, Column.ID.ordinal());
                    if (value instanceof Long) {
                        selectedTraderId = (Long) value;
                    } else if (value instanceof Integer) {
                        // Da Sie Long.class im Enum verwenden, aber Integer bekommen könnten:
                        selectedTraderId = ((Integer) value).longValue();
                    }
                }

                List l = list.getRowSorter().getSortKeys();

                if (!l.isEmpty()) {
                    list.getRowSorter().allRowsChanged();
                } else {
                    model.fireTableDataChanged();
                }

                if (selectedTraderId != null) {
                    // Durchlaufe das Model, um die Zeilennummer der ID zu finden
                    for (int i = 0; i < model.getRowCount(); i++) {
                        Object value = model.getValueAt(i, Column.ID.ordinal());
                        Long currentId = null;
                        if (value instanceof Long) {
                            currentId = (Long) value;
                        } else if (value instanceof Integer) {
                            currentId = ((Integer) value).longValue();
                        }

                        if (selectedTraderId.equals(currentId)) {

                            // Konvertiere die Model-Zeilennummer in die Ansichts-Zeilennummer
                            int viewRow = list.convertRowIndexToView(i);

                            // Setze die Markierung
                            list.getSelectionModel().setSelectionInterval(viewRow, viewRow);

                            // Optional: Scrolle zur markierten Zeile
                  //          list.scrollRectToVisible(list.getCellRect(viewRow, 0, true));

                            break; // Gefunden, Schleife verlassen
                        }
                    }
                }

            }

        });

    }

    public static class MyModel extends DefaultTableModel {

        public MyModel(Object arg0[][], Object arg1[]) {
            super(arg0, arg1);
        }

        Column[] def;

        public MyModel(Column[] d) {
            def = d;

        }

        @Override
        public int getColumnCount() {
            return def.length;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return def[columnIndex].cls;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
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

    public static class NameCellRenderer extends DefaultTableCellRenderer {

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
                } else {
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
        list.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Status", "Shares", "Cash", "Free Margin", "Total", "PnL%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        list.setDoubleBuffered(true);
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(list);
        if (list.getColumnModel().getColumnCount() > 0) {
            list.getColumnModel().getColumn(0).setPreferredWidth(20);
            list.getColumnModel().getColumn(0).setMaxWidth(70);
            list.getColumnModel().getColumn(7).setPreferredWidth(50);
            list.getColumnModel().getColumn(7).setMaxWidth(80);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
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
