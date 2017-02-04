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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import sesim.AutoTrader;
import sesim.Exchange;
import sesim.Scheduler;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class TraderListPanel extends javax.swing.JPanel
        implements Scheduler.TimerTask {

    Exchange se;
    TraderListModel model;

    /**
     * Creates new form TraderListPanel
     */
    public TraderListPanel() {
        initComponents();

        this.setBorder(BorderFactory.createEmptyBorder());
//        this.orderBookScroller.setBorder(BorderFactory.createBevelBorder(0));

        if (Globals.se == null) {
            return;
        }

        this.model = new TraderListModel();
        this.traderList.setModel(this.model);

        traderList.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader h = this.traderList.getTableHeader();
//        h.setBackground(Color.BLUE);
//        h.setForeground(Color.green);

        if (Globals.se != null) {
            this.se = Globals.se;
            this.list = this.getTraderList();
            se.timer.startTimerEvent(this, 1000);
        }

    }

    final ArrayList<TraderListItem> getTraderList() {
        if (se.traders==null)
            return new ArrayList<>();
        
        sesim.Quote q = se.getLastQuoete();
        double price = q == null ? 0 : q.price;
        Iterator<AutoTrader> it = se.traders.iterator();
        ArrayList<TraderListItem> tlist = new ArrayList<>();
        while (it.hasNext()) {
            AutoTrader at = it.next();
            Exchange.Account a = at.getAccount();

            TraderListItem ti = new TraderListItem();
            ti.name = at.getName();
            ti.shares = a.getShares();
            ti.money = a.getMoney();
            ti.welth = price==0 ? 0 : ti.shares * price + ti.money;
            tlist.add(ti);

        }
        return tlist;
    }

    @Override
    public long timerTask() {
        class Updater implements Runnable {

            TraderListModel model;
            ArrayList<TraderListItem> newlist;

            @Override
            public void run() {
                System.out.print("TTrunner\n");
                model.update(this.newlist);
            }

            Updater(TraderListModel model, ArrayList newlist) {
                this.model = model;
                this.newlist = newlist;
            }

        }
        
        System.out.print("TimerTaskUpdater\n");
        
        ArrayList <TraderListItem> newlist = getTraderList();
        SwingUtilities.invokeLater(new Updater(this.model, newlist));

        return 2000;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class TraderListItem {

        public String name;
        public double shares;
        public double money;
        public double welth;
    }

    private ArrayList<TraderListItem> list = new ArrayList<>();

    protected class TraderListModel extends AbstractTableModel {

        //private final boolean desc = false;
        public TraderListModel() {

        }

        public void update(ArrayList newlist) {
            
         
            list = newlist; //getOrderBook();
            this.fireTableDataChanged();
        }

        @Override
        public String getColumnName(int c) {
            switch (c) {
                case 0:
                    return "ID";
                case 1:
                    return "Name";
                case 2:
                    return "Money";
                case 3:
                    return "Shares";
                case 4:
                    return "Wealth";
            }
            return "";
        }

        @Override
        public int getRowCount() {
            int rc = list.size();
            //System.out.print("Size" + rc + "\n");
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }
        
        
        

        @Override
        public Object getValueAt(int r, int c) {
            TraderListItem ti;
            ti = list.get(r);

            int s = list.size();
            Formatter f = new Formatter();
            switch (c) {
                case 0:
                    return String.format("#%06x", 0);

                case 1:
                    return String.format("%s", ti.name);
                case 2:
                    return String.format("%.2f", ti.money);
                case 3:
                    return String.format("%.2f", ti.shares);
                case 4:
                    return ti.welth; //String.format("%.2f", ti.welth);
            }

            return "x";
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

        traderListScroller = new javax.swing.JScrollPane();
        traderList = new javax.swing.JTable();

        traderList.setAutoCreateRowSorter(true);
        traderList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        traderListScroller.setViewportView(traderList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(traderListScroller, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(traderListScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable traderList;
    private javax.swing.JScrollPane traderListScroller;
    // End of variables declaration//GEN-END:variables
}
