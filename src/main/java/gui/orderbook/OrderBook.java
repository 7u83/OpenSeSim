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
package gui.orderbook;

import gui.Globals;
import gui.Globals.CfgListener;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sesim.Exchange;
import sesim.Exchange.Order;
import sesim.Exchange.OrderType;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class OrderBook extends javax.swing.JPanel implements Exchange.BookReceiver, CfgListener {

    DefaultTableModel model;
    TableColumn trader_column = null;

    class Renderer extends DefaultTableCellRenderer {

        private final DecimalFormat formatter = new DecimalFormat("#.0000");

        Renderer() {
            super();
            this.setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            // First format the cell value as required
            value = formatter.format((Number) value);

            // And pass it on to parent class
            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
        }
    }

    OrderType type = OrderType.BUYLIMIT;
    int depth = 40;

    public void setGodMode(boolean on) {
        TableColumnModel tcm = list.getColumnModel();
        if (on) {
            if (list.getColumnCount() == 3) {
                return;
            }
            tcm.addColumn(trader_column);
            tcm.moveColumn(2, 0);

        } else {
            if (list.getColumnCount() == 2) {
                return;
            }
            tcm.removeColumn(tcm.getColumn(0));
        }
    }

    /**
     * Bla
     */
    @Override
    public final void cfgChanged() {
        boolean gm = Globals.prefs.get(Globals.CfgStrings.GODMODE, "false").equals("true");
        setGodMode(gm);
        list.invalidate();
        list.repaint();
    }

    public void setType(OrderType type) {
        this.type = type;
        Globals.se.addBookReceiver(type, this);
    }

    /**
     * Creates new form OrderBookNew
     */
    public OrderBook() {
        initComponents();

        if (Globals.se == null) {
            return;
        }
        model = (DefaultTableModel) this.list.getModel();
        trader_column = list.getColumnModel().getColumn(0);
        list.getColumnModel().getColumn(1).setCellRenderer(new Renderer());
        cfgChanged();
//        Globals.se.addBookReceiver(Exchange.OrderType.BUYLIMIT, this);
        Globals.addCfgListener(this);
    }

    boolean oupdate = false;
    boolean new_oupdate = false;

    void oupdater() {
        ArrayList<Order> ob = Globals.se.getOrderBook(type, depth);
        model.setRowCount(ob.size());
        int row = 0;
        for (Order ob1 : ob) {
            model.setValueAt(ob1.getAccount().getOwner().getName(), row, 0);
            model.setValueAt(ob1.getLimit(), row, 1);
            model.setValueAt(ob1.getVolume(), row, 2);
            row++;
        }

        oupdate = false;

    }

    @Override
    public synchronized void UpdateOrderBook() {

        if (oupdate) {
            new_oupdate=true;
            return;
        }

        oupdate = true;

        SwingUtilities.invokeLater(() -> {
            oupdater();
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
        list = new javax.swing.JTable();

        list.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trader", "Price", "Volume"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(list);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable list;
    // End of variables declaration//GEN-END:variables

}
