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
import gui.tools.NummericCellRenderer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sesim.Market;
import sesim.OrderBookEntry;
import sesim.Order;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RawOrderBook extends javax.swing.JPanel implements Market.BookListener {

    RawOrderBookModel model;
    TableColumn trader_column = null;
    TableColumn price_column = null;
    TableColumn vol_column = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    volatile boolean busy;
    volatile boolean update = true;

    protected byte type = Order.BUYLIMIT;
    protected int depth = 40;
    
    Market se;

    /**
     * Creates new form OrderBookNew
     */
    public RawOrderBook() {
        initComponents();

        if (Globals.sim == null) {
            return;
        }

        model = (RawOrderBookModel) this.list.getModel();

        trader_column = list.getColumnModel().getColumn(0);
        price_column = list.getColumnModel().getColumn(1);
        vol_column = list.getColumnModel().getColumn(2);
    }

    public void start(Market se, byte type) {
        this.se=se;
        this.type = type;
        stop();
        se.addBookListener(type, this);
        UpdateOrderBook();
    }
    
    public void stop(){
        se.removeBookListener(this);
    }
    

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

    protected ArrayList<? extends OrderBookEntry> getOrderBook() {
        return Globals.sim.getExchange().getRawOrderBook(type, depth);
        // return Globals.sim.se.getCompressedOrderBook(type);
    }
    
    private byte priceColumn;
    public void setPriceColumn(byte t){
        priceColumn=t;
        
    }

    @Override
    public void UpdateOrderBook() {

        if (busy) {
            update=true;
            return;
        }
        busy = true;
        update=true;

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    while (update) {
                        update=false;
                        ArrayList<? extends OrderBookEntry> newOb = getOrderBook(); // Globals.sim.se.getRawOrderBook(type, depth);

                        // GUI update on EDT
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                setGodMode(Globals.prefs_new.get(Globals.CfgStrings.GODMODE, "false").equals("true"));
                                vol_column.setCellRenderer(new NummericCellRenderer(Globals.sim.getExchange().getSharesFormatter()));
                                price_column.setCellRenderer(new NummericCellRenderer(Globals.sim.getExchange().getMoneyFormatter()));
                                model.setData(newOb);
                                model.fireTableDataChanged();
                            }
                        });

                        try {
                            Thread.sleep(50);   // update rate is limited 50 Hz
                        } catch (InterruptedException e) {
                        }
                    }
                    
                } finally {
                    busy = false;
                }
            }
        });

    }

    class RawOrderBookModel extends AbstractTableModel {

        ArrayList<? extends OrderBookEntry> myOb = null;
        final String colNames[] = {"Trader", "Price", "Volume"};
        final Class[] colTypes = new Class[]{
            java.lang.String.class, java.lang.Float.class, java.lang.Float.class
        };

        void setData(ArrayList<? extends OrderBookEntry> d) {
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

            switch (columnIndex) {
                case 0:
                    return myOb.get(rowIndex).getOwnerName();
                case 1:
                    switch (priceColumn){
                        case Order.STOP:
                            return myOb.get(rowIndex).getStop();
                        default:
                            return myOb.get(rowIndex).getLimit();
                    }
                case 2:
                    return myOb.get(rowIndex).getVolume();
                default:
                    return null;
            }

            // }
        }

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

    protected AbstractTableModel createModel() {
        return new RawOrderBookModel();
    }

    protected javax.swing.JTable createList() {
        return new javax.swing.JTable();
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
        list = createList();

        list.setModel(createModel());
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
    protected javax.swing.JTable list;
    // End of variables declaration//GEN-END:variables

}
