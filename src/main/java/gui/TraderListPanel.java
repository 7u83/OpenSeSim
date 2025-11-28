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

import gui.util.NummericCellRenderer;
import gui.util.PercentageCellRenderer;
import gui.util.PercentageValue;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
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
    // NEU: Map zum Speichern ausgeblendeter Spalten
    // Key: Column Enum, Value: TableColumn Objekt
    private final Map<Column, TableColumn> hiddenColumns = new HashMap<>();

    // Definition of columns
    public static enum Column {
        ID("ID", null, Long.class),
        NAME("Name", null, Object.class),
        STATUS("Status", null, String.class),
        SHARES("Shares", null, Float.class),
        MARGIN("Margin", null, Float.class),
        EQUITY("Equtiy", null, Float.class),
        FREEMARGIN("Free Margin", null, Float.class),
        CASH("Cash", null, Float.class),
        PNL("PnL", null, PercentageValue.class);

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
                    case NAME:
                        renderer = new NameCellRenderer();
                        break;
                    case SHARES:
                        renderer = new NummericCellRenderer(Globals.sim.getExchange().getSharesDecimals());
                        break;
                    case MARGIN:
                    case EQUITY:
                    case FREEMARGIN:
                    case CASH:
                        renderer = new NummericCellRenderer(Globals.sim.getExchange().getMoneyDecimals());
                        break;
                    case PNL:
                        renderer = new PercentageCellRenderer();
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

    public TraderListPanel(Frame parent, Column[] columnList) {
        initComponents();
        if (Globals.sim == null) {
            return;
        }
        this.columnList = columnList;

        setupTable();
        addContextMenu();

        /*      TableColumnModel colModel = list.getColumnModel();
        for (int i = colModel.getColumnCount() - 1; i >= 0; i--) {
            TableColumn col = colModel.getColumn(i);
            Column columnEnum = (Column) col.getIdentifier(); // besser Identifier setzen
            if (!Arrays.asList(columnList).contains(columnEnum)) {
                this.hideColumn(columnEnum);

            }
        }*/
    }

    /**
     * Creates new form TraderListPanel
     */
    public TraderListPanel() {

        initComponents();
        if (Globals.sim == null) {
            return;
        }
        setupTable();
// NEU: Kontextmenü nach der Tabellen-Einrichtung hinzufügen
        addContextMenu();
    }

    private void setupTable() {
        model = new MyModel(Column.values());
        list.setModel(model);

        MyRowSorter sorter = new MyRowSorter((MyModel) model);
        list.setRowSorter(sorter);

        for (Column a : Column.values()) {
            list.getColumnModel().getColumn(a.ordinal()).setHeaderValue(a.header);
            list.getColumnModel().getColumn(a.ordinal()).setIdentifier(a);
            list.getColumnModel().getColumn(a.ordinal()).setCellRenderer(a.getRenderer());
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.getColumnModel().getColumn(Column.ID.ordinal()).setCellRenderer(centerRenderer);

        TableColumnModel colModel = list.getColumnModel();

        for (int i = colModel.getColumnCount() - 1; i >= 0; i--) {
            TableColumn col = colModel.getColumn(i);
            Column columnEnum = (Column) col.getIdentifier(); // besser Identifier setzen
            if (!Arrays.asList(columnList).contains(columnEnum)) {

                this.hideColumn(columnEnum);

                // hiddenColumns.put(columnEnum, col);
                // colModel.removeColumn(col);
            }
        }

        /*     DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        list.getColumnModel().getColumn(Column.ID.ordinal()).setCellRenderer(centerRenderer);
         */
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

        ((MyModel) (model)).sortTraders();

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
                //   model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                model.fireTableDataChanged();

                list.getRowSorter().allRowsChanged();

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

    JPopupMenu popupMenu;

    /**
     * NEU: Erstellt und fügt das Kontextmenü zur Tabelle hinzu.
     */
    private void addContextMenu() {
        popupMenu = new JPopupMenu("Spalten verwalten");

        for (final Column colEnum : Column.values()) {
            final String header = colEnum.header;
            // Bestimme den Anfangszustand: Ist die Spalte aktuell sichtbar?
            boolean isVisible = false;
            try {
                isVisible = list.getColumnModel().getColumnIndex(colEnum) != -1;
            } catch (Exception e) {

            }

            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(header, isVisible);
            menuItem.setName(header); // Kann optional zur Identifizierung verwendet werden

            // ID-Spalte sollte immer sichtbar sein und nicht abwählbar
            if (colEnum == Column.ID) {
                //     menuItem.setEnabled(false);
            }

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (menuItem.isSelected()) {
                        // Spalte anzeigen (hinzufügen)
                        showColumn(colEnum);
                    } else {
                        // Spalte ausblenden (entfernen)
                        hideColumn(colEnum);
                    }
                }
            });
            popupMenu.add(menuItem);
        }

        // Fügen Sie den MouseListener hinzu, um das Menü anzuzeigen
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * NEU: Blendet eine Spalte aus und speichert sie.
     *
     * * @param colEnum Die auszublendende Spalte.
     */
    private void hideColumn(Column colEnum) {
        TableColumnModel colModel = list.getColumnModel();
        try {
            int viewIndex = colModel.getColumnIndex(colEnum);
            TableColumn col = colModel.getColumn(viewIndex);
            colModel.removeColumn(col);
            hiddenColumns.put(colEnum, col);

            /*        for (int i = colModel.getColumnCount() - 1; i >= 0; i--) {
            col = colModel.getColumn(i);
            Column columnEnum = (Column) col.getIdentifier();
            col.setCellRenderer(columnEnum.getRenderer());
            
            System.out.printf("Setting CellRenderer %s for %d\n",columnEnum.toString(),i);
          }*/
        } catch (IllegalArgumentException ex) {
            // Spalte ist bereits ausgeblendet, nichts tun.
        }
    }

    /**
     * NEU: Zeigt eine ausgeblendete Spalte wieder an.
     *
     * * @param colEnum Die anzuzeigende Spalte.
     */
    private void showColumn(Column colEnum) {
        TableColumn col = hiddenColumns.get(colEnum);
        if (col != null) {
            TableColumnModel colModel = list.getColumnModel();

            // Finde die korrekte Einfügeposition, basierend auf der ursprünglichen
            // Reihenfolge der Enum-Werte, die sichtbar sind.
            int insertIndex = 0;
            Column[] allColumns = Column.values();
            for (int i = 0; i < allColumns.length; i++) {
                Column current = allColumns[i];
                if (current == colEnum) {
                    // Wir haben die Spalte gefunden, die wir einfügen wollen. 
                    // Der Einfügeindex ist die Anzahl der *sichtbaren* Spalten, die *vor* dieser Spalte im Enum stehen.
                    // Das ist bereits der Wert von insertIndex.
                    break;
                }

                // Überprüfe, ob die aktuelle Spalte (im Enum vor der einzufügenden) sichtbar ist
                // Wenn sie nicht in hiddenColumns ist, dann ist sie sichtbar.
                if (!hiddenColumns.containsKey(current)) {
                    insertIndex++;
                }
            }

            // Füge die Spalte an der berechneten Position ein
            colModel.addColumn(col);
            colModel.moveColumn(colModel.getColumnCount() - 1, insertIndex);

            hiddenColumns.remove(colEnum);
            /*    for (int i = colModel.getColumnCount() - 1; i >= 0; i--) {
                col = colModel.getColumn(i);
                Column columnEnum = (Column) col.getIdentifier();
                col.setCellRenderer(columnEnum.getRenderer());

                System.out.printf("Setting CellRenderer %s for %d\n", columnEnum.toString(), i);
            }*/
        }
    }

    private class MyModel extends DefaultTableModel {

        public MyModel(Object arg0[][], Object arg1[]) {
            super(arg0, arg1);
        }

        Column[] def;

        public MyModel(Column[] d) {
            def = d;

        }

        @Override
        public int getColumnCount() {
            if (def == null) {
                return 0;
            }
            return def.length;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            if (def == null) {
                return null;
            }
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
            super.fireTableRowsUpdated(firstRow, lastRow);
        }

        @Override
        public void fireTableCellUpdated(int row, int column) {

        }

        //public ArrayList<AutoTraderInterface> traders = null;
        public ArrayList<ArrayList<Object>> sortedTraders = null;

        byte sortCol = 0;
        boolean sortAsc = false;

        void sortTraders() {
            //ArrayList<AutoTraderInterface> t = new ArrayList<>();

            ArrayList<ArrayList<Object>> t = new ArrayList<>();
            for (AutoTraderInterface a : Globals.sim.traders) {
                //t.add(a);
                ArrayList<Object> objects = new ArrayList<>();
                for (Column c : Column.values()) {
                    objects.add(getValue(a, c.ordinal()));
                }
                t.add(objects);
            }
            t.sort(new TraderComparator(sortCol, !sortAsc));
            sortedTraders = t;
            //traders = t;
        }

        class TraderComparator implements Comparator<ArrayList<Object>> {

            byte col;
            boolean asc;

            TraderComparator(byte col, boolean asc) {
                this.col = col;
                this.asc = asc;
            }

            @Override
            public int compare(ArrayList<Object> left, ArrayList<Object> right) {
                Object l, r;
                l = left.get(col); //getValue(left, col);
                r = right.get(col); //getValue(right, col);

                if (l == null && r == null) {
                    return 0;
                }

                if (asc) {
                    if (l == null) {
                        return 1;
                    }
                    if (r == null) {
                        return -1;
                    }
                    return ((Comparable<Object>) r).compareTo(l);
                }

                if (l == null) {
                    return -1;
                }
                if (r == null) {
                    return 1;
                }
                return ((Comparable<Object>) l).compareTo(r);

            }

        }

        public Object getValue(AutoTraderInterface at, int column) {

            Account a = at.getAccount();
            float price = Globals.sim.getExchange().getLastPrice();

            if (column == Column.ID.ordinal()) {
                return 1;
            }

            if (column == Column.NAME.ordinal()) {

                int[] atc = at.getColor();
                Color c = null;
                if (atc != null) {
                    c = new Color(atc[0], atc[1], atc[2]);
                }
                return new NameValue(at.getName(), c);
            }

            if (column == Column.STATUS.ordinal()) {
                return at.getStatus();
            }

            if (column == Column.CASH.ordinal()) {
                return a.getMoney();
            }

            if (column == Column.SHARES.ordinal()) {
                return a.getShares();
            }
            if (column == Column.EQUITY.ordinal()) {
                return a.getEquity();
            }

            if (column == Column.MARGIN.ordinal()) {
                return a.getMarginUsed();
            }

            if (column == Column.FREEMARGIN.ordinal()) {
                return a.getFreeMargin();
            }

            if (column == Column.PNL.ordinal()) {
                return new PercentageValue(a.getPerformance(price));
            }

            return null;
            //  return super.getValueAt(row, column);

        }

        @Override
        public Object getValueAt(int row, int column) {

            AutoTraderInterface at;
            if (sortedTraders == null) {
                at = Globals.sim.traders.get(row);
                return this.getValue(at, column);
            } else {
                return sortedTraders.get(row).get(column);

                //at = traders.get(row);
            }
            //Account a = at.getAccount();
            //float price = Globals.sim.getExchange().getLastPrice();

            //return this.getValue(at, column);
        }

        @Override
        public int getRowCount() {
            return Globals.sim.traders.size();
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
                /*                Font defaultFod) {
                Color color = n.getColor();
                if (color != null) {
                    this.setBackground(n.getColor());
                } else {
                    setBackground(nt = table.getFont();
                if (Math.abs(performance) > 10) {

                    setFont(defaultFont.deriveFont(Font.BOLD));
                } else {
                    setFont(defaultFont);
                }*/
            }

            return c;
        }
    }

    class MyRowSorter extends RowSorter<MyModel> {

        private final MyModel model;
        private List<SortKey> sortKeys = Collections.emptyList();

        public MyRowSorter(MyModel model) {
            this.model = model;
        }

        @Override
        public MyModel getModel() {
            return model;
        }

        @Override
        public void setSortKeys(List<? extends SortKey> keys) {
            // In Java 8 besser in eine modifizierbare Liste kopieren
            this.sortKeys = new ArrayList<SortKey>(keys);

            if (!keys.isEmpty()) {
                SortKey k = keys.get(0);
                int col = k.getColumn();
                boolean asc = k.getSortOrder() == SortOrder.ASCENDING;
                //    model.reloadSorted(col, asc);
            }

            fireSortOrderChanged();
        }

        @Override
        public List<SortKey> getSortKeys() {
            return sortKeys;
        }

        // Keine Abbildung nötig, 1:1 Durchreichung
        @Override
        public int convertRowIndexToModel(int row) {
            return row;
        }

        @Override
        public int convertRowIndexToView(int row) {
            return row;
        }

        @Override
        public void modelStructureChanged() {
            fireSortOrderChanged();
        }

        @Override
        public void allRowsChanged() {

            fireSortOrderChanged();
            //model.fireTableDataChanged();
        }

        @Override
        public void rowsInserted(int firstRow, int endRow) {
        }

        @Override
        public void rowsDeleted(int firstRow, int endRow) {
        }

        @Override
        public void rowsUpdated(int firstRow, int endRow) {
        }

        @Override
        public void rowsUpdated(int firstRow, int endRow, int col) {
        }

        @Override
        public void toggleSortOrder(int column) {

            boolean asc = true;

            if (!sortKeys.isEmpty()) {
                SortKey current = sortKeys.get(0);

                if (current.getColumn() == column) {
                    // Richtung umdrehen
                    asc = current.getSortOrder() != SortOrder.ASCENDING;
                }
            }

            model.sortCol = (byte) column;
            model.sortAsc = asc;

            // Neue SortKeys-Liste setzen
            List<SortKey> newKeys = new ArrayList<>();
            newKeys.add(new SortKey(column, asc ? SortOrder.ASCENDING : SortOrder.DESCENDING));

            setSortKeys(newKeys);
        }

        @Override
        public int getViewRowCount() {
            return model.getRowCount();
        }

        @Override
        public int getModelRowCount() {
            return model.getRowCount();
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
