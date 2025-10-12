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
import gui.tools.NummericCellRenderer;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
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
public class RawOrderBook extends javax.swing.JPanel implements Exchange.BookReceiver {

    MyModel model;
    TableColumn trader_column = null;
    TableColumn price_column = null;
    TableColumn vol_column = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private OrderType type = OrderType.BUYLIMIT;
    int depth = 40;

    /**
     * Creates new form OrderBookNew
     */
    public RawOrderBook() {
        initComponents();

        if (Globals.sim == null) {
            return;
        }

        model = (MyModel) this.list.getModel();

        trader_column = list.getColumnModel().getColumn(0);
        price_column = list.getColumnModel().getColumn(1);
        vol_column = list.getColumnModel().getColumn(2);

        list.getColumnModel().getColumn(1).setCellRenderer(new NummericCellRenderer(
                2//  Globals.sim.se.getSharesFormatter()
        ));

        list.getColumnModel().getColumn(2).setCellRenderer(new NummericCellRenderer(
                2 //  Globals.sim.se.getMoneyFormatter()
        ));

    }

    public void setType(OrderType type) {
        this.type = type;
        Globals.sim.se.addBookReceiver(type, this);
    }

    volatile boolean busy;

    @Override
    public void UpdateOrderBook() {

        if (busy) {
            return;
        }
        busy = true;

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Order> newOb = Globals.sim.se.getRawOrderBook(type, depth);

                    // GUI-Update on EDT
                    SwingUtilities.invokeLater(new Runnable() {
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

                        @Override
                        public void run() {
                            setGodMode(Globals.prefs_new.get(Globals.CfgStrings.GODMODE, "false").equals("true"));
                            vol_column.setCellRenderer(new NummericCellRenderer(Globals.sim.se.getSharesFormatter()));
                            price_column.setCellRenderer(new NummericCellRenderer(Globals.sim.se.getMoneyFormatter()));
                            model.setData(newOb);
                            model.fireTableDataChanged();
                        }
                    });

                    try {
                        Thread.sleep(20);   // update rate is limited 50 Hz
                    } catch (InterruptedException e) {
                    }
                } finally {
                    busy = false;
                }
            }
        });

    }

    class MyModel extends AbstractTableModel {

        ArrayList<Order> myOb = null;
        final String colNames[] = {"Trader", "Price", "Volume"};
        final Class[] colTypes = new Class[]{
            java.lang.String.class, java.lang.Double.class, java.lang.Double.class
        };

        /*   private int getOffset() {
            if (Globals.prefs_new.get(Globals.CfgStrings.GODMODE, "false").equals("true")) {
                return 0;
            }
            return 1;

        }
         */
        void setData(ArrayList<Order> d) {
            myOb = d;
        }

        @Override
        public int getRowCount() {

            if (myOb == null) {
                return 0;
            }
            return myOb.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
//            int o = getOffset();

            /*          if (rowIndex >= myOb.size()) {
                sesim.Logger.debug("ERROR  %d >= %d\n", rowIndex, myOb.size());

                switch (columnIndex) {
                    case 0:
                        return "a";
                    case 1:
                        return (double) rowIndex;
                    case 2:
                        return (double) myOb.size();
                    default:
                        return null;
                }
            }*/
            switch (columnIndex) {
                case 0:
                    return myOb.get(rowIndex).getAccount().getOwner().getName();
                case 1:
                    return myOb.get(rowIndex).getLimit();
                case 2:
                    return myOb.get(rowIndex).getVolume();
                default:
                    return null;
            }

            // }
        }

        //      list.getColumnModel().getColumn(1).setCellRenderer(new NummericCellRenderer(3));
        //list.getColumnModel().getColumn(2).setCellRenderer(new NummericCellRenderer(0));
        @Override
        public void fireTableRowsUpdated(int firstRow, int lastRow) {
        }

        @Override
        public void fireTableCellUpdated(int row, int column) {
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return colNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return colTypes[columnIndex];

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

        list.setModel(new MyModel());
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
