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
package traders.ManTrader;

import gui.OpenOrdersList;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sesim.Account;

import sesim.Exchange;
import sesim.Order;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class ManTraderConsolePanel extends javax.swing.JPanel {

    public ManTrader trader;
    Account account;
    Exchange se;

    public OpenOrdersList getOrderListPanel() {

        return this.ordersList;

    }
    
  

    /**
     * Creates new form ManTraderConsole
     */
    public ManTraderConsolePanel() {
        initComponents();

        //  this.ordersList1.account=trader.getAccount();
    }

    public ManTraderConsolePanel(Exchange e, Account a) {
        account = a;
        se = e;
        initComponents();

        // JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ordersList.table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed()) {
                    evt.consume();
                    System.out.println("Doppelklick auf Order!");
                    //    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ManTraderConsolePanel.this);

                    // 1. Übergeordnetes Window finden (korrigierter Cast)
                    Window parentWindow = SwingUtilities.getWindowAncestor(ManTraderConsolePanel.this);

                    // 2. Parent an den Dialog-Konstruktor übergeben
                    //    Wenn ModifyOrderDialog einen java.awt.Frame oder java.awt.Dialog erwartet:
                    Frame parentFrame = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
                    // ODER (falls der Konstruktor auch Dialoge akzeptiert, was üblich ist):
                    // Dialog parentDialog = (parentWindow instanceof Dialog) ? (Dialog) parentWindow : null;

                    // Da Sie Frame verwenden wollten, bleiben wir bei dieser Logik,
                    // setzen aber auf null, wenn es kein Frame ist, um den Fehler zu vermeiden.
                    Point point = evt.getPoint();
                    int currentRow = ordersList.table.rowAtPoint(point);
                    if (currentRow != -1) {

                        ordersList.table.setRowSelectionInterval(currentRow, currentRow);
                        Long oid = (Long) ordersList.table.getModel().getValueAt(currentRow, 0);
                        Order o = account.getOrderByID(oid);
                        ModifyOrderDialog d = new ModifyOrderDialog(parentFrame, true, se, o);
                        d.setLocationRelativeTo(parentWindow);
                        d.setVisible(true);
                    } else {

                    }

                }
            }
        });

        this.buyEditOrderPanel.addChangeListeners(
                new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBuyButton();
                updateSellButton();

            }

        }
        );

        this.sellEditOrderPanel.addChangeListeners(
                new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBuyButton();
                updateSellButton();

            }

        }
        );


    }

    private boolean updateBuyButton() {
        float vol = this.buyEditOrderPanel.getVolume();
        float limit = this.buyEditOrderPanel.getLimit();
        byte type = this.buyEditOrderPanel.getOrderType();
        boolean b = account.isOrderCovered(type, vol, limit);

     //   this.buyButton.setEnabled(b);
        return b;
    }

    private boolean updateSellButton() {
        float vol = this.sellEditOrderPanel.getVolume();
        float limit = this.sellEditOrderPanel.getLimit();
        byte type = this.sellEditOrderPanel.getOrderType();
        boolean b = account.isOrderCovered(type, vol, limit);
    //    this.sellButton.setEnabled(b);
        return b;
    }

    void init(Account a, ManTrader mt) {
        this.positionList.setPositionList(account.getPositions());
        this.ordersList.setOrderList(account.getOrders());
        this.closedOrderList.setOrderList(Collections.unmodifiableMap(mt.allOrders));

    }

    void doUpdate(Account a, ManTrader mt) {
        this.ordersList.updateModel();
        this.positionList.updateModel();
        this.closedOrderList.updateModel();
        this.accountBalance2.updateBalance(a);
        updateBuyButton();
        updateSellButton();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ctxMenu = new javax.swing.JPopupMenu();
        ctxMenuCreateBuyOrder = new javax.swing.JMenuItem();
        ctxMenuCreateSellOrder = new javax.swing.JMenuItem();
        ctxMenuCancelOrder = new javax.swing.JMenuItem();
        ctxMenuModifyOder = new javax.swing.JMenuItem();
        orderPanel = new javax.swing.JPanel();
        orderTabs = new javax.swing.JTabbedPane();
        positionList = new gui.PositionsListPanel();
        ordersList = new gui.OpenOrdersList();
        closedOrderList = new gui.OpenOrdersList();
        tradingPanel = new javax.swing.JPanel();
        sellButton = new javax.swing.JButton();
        sellEditOrderPanel = new EditOrderPanel(se,account,Order.SELL);
        buyEditOrderPanel = new EditOrderPanel(se,account,Order.BUY);
        buyButton = new javax.swing.JButton();
        accountBalance2 = new traders.ManTrader.AccountBalance();

        ctxMenuCreateBuyOrder.setText("Create Buy Order");
        ctxMenuCreateBuyOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctxMenuCreateBuyOrderActionPerformed(evt);
            }
        });
        ctxMenu.add(ctxMenuCreateBuyOrder);

        ctxMenuCreateSellOrder.setText("Create Sell Order");
        ctxMenuCreateSellOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctxMenuCreateSellOrderActionPerformed(evt);
            }
        });
        ctxMenu.add(ctxMenuCreateSellOrder);

        ctxMenuCancelOrder.setText("Cancel Order");
        ctxMenuCancelOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctxMenuCancelOrderActionPerformed(evt);
            }
        });
        ctxMenu.add(ctxMenuCancelOrder);

        ctxMenuModifyOder.setText("Modify Oder");
        ctxMenu.add(ctxMenuModifyOder);

        orderTabs.addTab("Positions", positionList);
        orderTabs.addTab("Open Orders", ordersList);
        orderTabs.addTab("All Orders", closedOrderList);

        javax.swing.GroupLayout orderPanelLayout = new javax.swing.GroupLayout(orderPanel);
        orderPanel.setLayout(orderPanelLayout);
        orderPanelLayout.setHorizontalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
                .addContainerGap())
        );
        orderPanelLayout.setVerticalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addContainerGap())
        );

        sellButton.setBackground(new java.awt.Color(255, 153, 153));
        sellButton.setText("Sell");
        sellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellButtonActionPerformed(evt);
            }
        });

        buyButton.setBackground(new java.awt.Color(153, 255, 153));
        buyButton.setText("Buy");
        buyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tradingPanelLayout = new javax.swing.GroupLayout(tradingPanel);
        tradingPanel.setLayout(tradingPanelLayout);
        tradingPanelLayout.setHorizontalGroup(
            tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tradingPanelLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buyEditOrderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addComponent(sellEditOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(sellButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tradingPanelLayout.setVerticalGroup(
            tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tradingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buyEditOrderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sellButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellEditOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(accountBalance2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(tradingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tradingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountBalance2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyButtonActionPerformed
        float vol = this.buyEditOrderPanel.getVolume();
        float limit = this.buyEditOrderPanel.getLimit();
        float stop = this.buyEditOrderPanel.getStop();
        byte type = this.buyEditOrderPanel.getOrderType();

        //    synchronized (se.timer) {
        Order o = se.createOrder(account, type, vol, limit, stop, 1);        
        o = se.createOrder(account, type, vol, limit, stop, 10);

        //    }
        this.updateBuyButton();


    }//GEN-LAST:event_buyButtonActionPerformed

    private void sellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellButtonActionPerformed
        float vol = this.sellEditOrderPanel.getVolume();
        float limit = this.sellEditOrderPanel.getLimit();
        float stop = this.sellEditOrderPanel.getStop();
        byte type = this.sellEditOrderPanel.getOrderType();

        //      synchronized (se.timer) {
        Order o = se.createOrder(account, type, vol, limit, stop);
 //Order o = se.createLeveragedOrder(account, type, (long)(limit*100));

        //    }
        this.updateSellButton();
    }//GEN-LAST:event_sellButtonActionPerformed

    private void ctxMenuCreateBuyOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCreateBuyOrderActionPerformed
        //  createOrder(Order.BUYLIMIT);
    }//GEN-LAST:event_ctxMenuCreateBuyOrderActionPerformed

    private void ctxMenuCreateSellOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCreateSellOrderActionPerformed
        // createOrder(Order.SELLLIMIT);
    }//GEN-LAST:event_ctxMenuCreateSellOrderActionPerformed

    private void ctxMenuCancelOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCancelOrderActionPerformed

    }//GEN-LAST:event_ctxMenuCancelOrderActionPerformed

    private void ordersListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersListMousePressed
     //   System.out.printf("Something hppened\n");
        /*        if (evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            ModifyOrderDialog d = new ModifyOrderDialog(null, true);
        }*/
    }//GEN-LAST:event_ordersListMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private traders.ManTrader.AccountBalance accountBalance2;
    private javax.swing.JButton buyButton;
    private traders.ManTrader.EditOrderPanel buyEditOrderPanel;
    private gui.OpenOrdersList closedOrderList;
    private javax.swing.JPopupMenu ctxMenu;
    private javax.swing.JMenuItem ctxMenuCancelOrder;
    private javax.swing.JMenuItem ctxMenuCreateBuyOrder;
    private javax.swing.JMenuItem ctxMenuCreateSellOrder;
    private javax.swing.JMenuItem ctxMenuModifyOder;
    private javax.swing.JPanel orderPanel;
    private javax.swing.JTabbedPane orderTabs;
    private gui.OpenOrdersList ordersList;
    private gui.PositionsListPanel positionList;
    private javax.swing.JButton sellButton;
    private traders.ManTrader.EditOrderPanel sellEditOrderPanel;
    private javax.swing.JPanel tradingPanel;
    // End of variables declaration//GEN-END:variables
}
