/*
 * Copyright (c) 2016, 7u83 <7u83@mail.ru>
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
package Gui;

import SeSim.Exchange;
import java.util.ArrayList;
import java.util.Formatter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.SwingUtilities;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
/**
 * OderBook Class
 */
public abstract class OrderBook extends javax.swing.JPanel implements Exchange.BookReceiver {

    
    OrderBookListModel model;
    
    abstract ArrayList getOrderBook();

    private Color hdr_color = Color.LIGHT_GRAY;

    private class OrderBookCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            DefaultTableCellRenderer renderer
                    = (DefaultTableCellRenderer) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
            renderer.setBackground(hdr_color);
            return renderer;
        }
    }

    @Override
    public void UpdateOrderBook() {
        
        class Updater implements Runnable{
            OrderBookListModel model;
            ArrayList newlist;
            
            @Override
            public void run() {
                model.update(this.newlist);
            }

            Updater(OrderBookListModel model, ArrayList newlist){
                this.model = model;
                this.newlist = newlist;
            }
             
        }
        ArrayList newlist = getOrderBook();
        SwingUtilities.invokeLater(new Updater(this.model,newlist));

    }

    boolean getDesc() {
        return false;
    }

//    protected OrderBookListModel model;

    protected class OrderBookListModel extends AbstractTableModel {

        private ArrayList list;
        private boolean desc = false;

        public OrderBookListModel() {
            System.out.print("CREATING A NEW MODEL\n");
//            update();
            list = getOrderBook();
        }

        int update_calls = 0;
        int colcount_calls = 0;

        public void update(ArrayList newlist) {
            list = newlist; //getOrderBook();
            this.fireTableDataChanged();
            
            this.update_calls++;
            int hc = this.hashCode();
            System.out.print("Update/ColCalls = " + update_calls + "/" + colcount_calls + " HC: " + hc + "\n");
        }
 
        
        @Override
        public String getColumnName(int c) {
            switch (c) {
                case 0:
                    return "ID";
                case 1:
                    return "Price";
                case 2:
                    return "Vol.";
            }
            return "";
        }

        @Override
        public int getRowCount() {
            colcount_calls++;
            System.out.print("Update/ColCalls = " + update_calls + "/" + colcount_calls + "\n");
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int r, int c) {
            SeSim.Order o;

            int s = list.size();
            //System.out.print("Looking for Value at" + r + ":" + c + " w size:" + s + "\n");

            if (!getDesc()) {
                o = (SeSim.Order) list.get(r);
            } else {
                o = (SeSim.Order) list.get(list.size() - r - 1);
            }
            Formatter f = new Formatter();
            switch (c) {
                case 0:
                    return f.format("#%06x", o.id);

                case 1:
                    return o.limit;
                case 2:
                    return o.volume;
            }
            return "";
        }
    }

    /**
     * Creates new form OrderBook
     */
    public OrderBook() {
        //System.out.print("init Orderbook]\n");
        initComponents();

        this.setBorder(BorderFactory.createEmptyBorder());
        this.orderBookScroller.setBorder(BorderFactory.createBevelBorder(0));

        if (MainWin.se == null) {
            return;
        }

        this.model = new OrderBookListModel();
        this.orderBookList.setModel(model);
        
        orderBookList.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader h = this.orderBookList.getTableHeader();
        h.setBackground(hdr_color);
        h.setForeground(Color.green);
        h.setDefaultRenderer(new OrderBookCellRenderer());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orderBookScroller = new javax.swing.JScrollPane();
        orderBookList = new javax.swing.JTable();

        orderBookList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", null, null},
                {"2", null, null},
                {"3", null, null},
                {"4", null, null},
                {"5", null, null},
                {"7", null, null},
                {"4", null, null},
                {null, null, null},
                {"3", null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "id", "Title 2pri", "Title 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderBookList.setDoubleBuffered(true);
        orderBookList.setFocusable(false);
        orderBookScroller.setViewportView(orderBookList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderBookScroller, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderBookScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable orderBookList;
    private javax.swing.JScrollPane orderBookScroller;
    // End of variables declaration//GEN-END:variables
}
