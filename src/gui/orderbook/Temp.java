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
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Temp extends javax.swing.JPanel implements Exchange.BookReceiver, CfgListener {

    private final Object lock = new Object();
    AbstractTableModel model;
    TableColumn trader_column = null;

// Dieses Objekt wird die einzige Quelle der Wahrheit für die Liste sein
    private final AtomicReference<ArrayList<Order>> listReference
            = new AtomicReference<>(new ArrayList<>());

    ArrayList<Order> orderBook;

    class uRunner extends Thread {

        @Override
        public void run() {
            if (model == null) {
                return;
            }
            ArrayList<Order> ob = Globals.sim.se.getOrderBook(type, depth);
            synchronized (lock) {
                listReference.set(ob);

                // 3. Informiere den EDT, dass er neu zeichnen soll
                SwingUtilities.invokeLater(() -> {
                    // Dies triggert den JList-Mechanismus, der unser Model aufruft
                    model.fireTableDataChanged(); 
                //    repaint();
// oder besser: meinListModel.fire...
                });
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
                Logger.getLogger(OrderBook.class.getName()).log(Level.SEVERE, null, ex);
            }
            theThread = null;
        }

    }

    volatile uRunner theThread;


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
        boolean gm = Globals.prefs_new.get(Globals.CfgStrings.GODMODE, "false").equals("true");
        setGodMode(gm);
        list.invalidate();
        list.repaint();
    }

    public void setType(OrderType type) {
        this.type = type;
        Globals.sim.se.addBookReceiver(type, this);
    }

    /**
     * Creates new form OrderBookNew
     */
    public Temp() {
        initComponents();

        for (int i = 0; i < 1000; i++) {

        }

        if (Globals.sim == null) {
            return;
        }
        model = (AbstractTableModel) this.list.getModel();
        trader_column = list.getColumnModel().getColumn(0);
        list.getColumnModel().getColumn(1).setCellRenderer(new NummericCellRenderer(3));
        list.getColumnModel().getColumn(2).setCellRenderer(new NummericCellRenderer(0));
        cfgChanged();
//        Globals.sim.se.addBookReceiver(Exchange.OrderType.BUYLIMIT, this);
        Globals.addCfgListener(this);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // System.out.printf("Update order book\n");
                // UpdateOrderBook();
            }
        }, 1000, 1000);

    }

    boolean oupdate = false;
    boolean new_oupdate = false;

    //   long ouctr = 0;
    @Override
    public void UpdateOrderBook() {
        synchronized (this) {
            if (theThread == null) {
                theThread = new uRunner();
                theThread.start();
            }
        }
        return;
        /*  synchronized (this) {
            if (oupdate) {
                new_oupdate = true;
                return;
            }
            oupdate = true;
        }

        SwingUtilities.invokeLater(() -> {
            oupdater();
        });
         */
    }

    class MyModel extends AbstractTableModel {

        private final AtomicReference<ArrayList<Order>> dataReference;

        ArrayList<Order> myOb;

        MyModel(AtomicReference<ArrayList<Order>> ref) {
            this.dataReference = ref;
        }

        @Override
        public int getRowCount() {
            synchronized (lock) {
                myOb = this.dataReference.get();
                // alle 3 Mio Zeilen, aber nicht gespeichert im Model
            }
            if (myOb == null) {
                return 0;
            }

            //       sesim.Logger.debug("GET ROWCOUNT %d\n", myOb.size());
            return myOb.size();

        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            synchronized (lock) {

                if (rowIndex >= myOb.size()) {
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
                }

                //sesim.Logger.debug("GET VALUE AT ROWCOUNT %d\n", myOb.size());
                //     Order o = orders.get(rowIndex); // Zugriff auf das Originalarray
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

            }
        }
        //  @Override

        /*     public void fireTableDataChanged() {
            super.fireTableDataChanged();
        }*/
        @Override
        public void fireTableStructureChanged() {

        }

        @Override
        public void fireTableRowsUpdated(int firstRow, int lastRow
        ) {
        }

        @Override
        public void fireTableCellUpdated(int row, int column
        ) {

        }

        @Override
        public int getColumnCount() {
            return 3;
            //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JTable();

        list.setModel(new MyModel(
            listReference
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable list;
    // End of variables declaration                   

}
