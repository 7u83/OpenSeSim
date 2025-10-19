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
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sesim.Account;

import sesim.Exchange;
import sesim.Exchange.Order;
import sesim.Exchange.OrderType;

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
                                        ModifyOrderDialog d = new ModifyOrderDialog(parentFrame, true,se, o);
                    d.setLocationRelativeTo(parentWindow);
                    d.setVisible(true);
        } else {

        }

            
            
            

                }
            }
        });

        this.buyEditOrderPanel.limitSpinner.addChangeListener(
                new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBuyButton();
                updateSellButton();

            }

        }
        );

        this.buyEditOrderPanel.volumeSpinner.addChangeListener(
                new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBuyButton();
                updateSellButton();

            }

        }
        );

        this.sellEditOrderPanel.limitSpinner.addChangeListener(
                new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBuyButton();
                updateSellButton();

            }

        }
        );

        this.sellEditOrderPanel.volumeSpinner.addChangeListener(
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

        boolean b = account.couldBuy(vol, limit);
        this.buyButton.setEnabled(b);
        return b;
    }

    private boolean updateSellButton() {
        float vol = this.sellEditOrderPanel.getVolume();
        boolean b = account.coulSell(vol);
        this.sellButton.setEnabled(b);
        return b;
    }

    void init(Account a) {
        this.ordersList.setOrderList(account.getOrders()); //  .initOrderList(account);
    }

    void doUpdate(Account a) {
        this.ordersList.updateModel();
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
        tradingPanel = new javax.swing.JPanel();
        sellButton = new javax.swing.JButton();
        sellEditOrderPanel = new EditOrderPanel(se,account,OrderType.SELLLIMIT);
        buyEditOrderPanel = new EditOrderPanel(se,account,OrderType.BUYLIMIT);
        buyButton = new javax.swing.JButton();
        orderPanel = new javax.swing.JPanel();
        orderTabs = new javax.swing.JTabbedPane();
        ordersList = new gui.OpenOrdersList();
        closedOrderList = new gui.OpenOrdersList();
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
                .addContainerGap()
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sellEditOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyEditOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sellButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tradingPanelLayout.setVerticalGroup(
            tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tradingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buyEditOrderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(tradingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sellEditOrderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sellButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ordersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ordersListMousePressed(evt);
            }
        });
        orderTabs.addTab("Open Orders", ordersList);
        orderTabs.addTab("Closed Orders", closedOrderList);

        javax.swing.GroupLayout orderPanelLayout = new javax.swing.GroupLayout(orderPanel);
        orderPanel.setLayout(orderPanelLayout);
        orderPanelLayout.setHorizontalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(orderPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(orderTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        orderPanelLayout.setVerticalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
            .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(orderTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                    .addContainerGap()))
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
                        .addComponent(accountBalance2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tradingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tradingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(accountBalance2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyButtonActionPerformed
        float vol = this.buyEditOrderPanel.getVolume();
        float limit = this.buyEditOrderPanel.getLimit();

        synchronized (se.timer) {
            Order o = se.createOrder(account, OrderType.BUYLIMIT, vol, limit);

        }
        this.updateBuyButton();


    }//GEN-LAST:event_buyButtonActionPerformed

    private void sellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellButtonActionPerformed
        float vol = this.sellEditOrderPanel.getVolume();
        float limit = this.sellEditOrderPanel.getLimit();

        synchronized (se.timer) {
            Order o = se.createOrder(account, OrderType.SELLLIMIT, vol, limit);

        }
        this.updateSellButton();
    }//GEN-LAST:event_sellButtonActionPerformed

    private void ctxMenuCreateBuyOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCreateBuyOrderActionPerformed
        //  createOrder(OrderType.BUYLIMIT);
    }//GEN-LAST:event_ctxMenuCreateBuyOrderActionPerformed

    private void ctxMenuCreateSellOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCreateSellOrderActionPerformed
        // createOrder(OrderType.SELLLIMIT);
    }//GEN-LAST:event_ctxMenuCreateSellOrderActionPerformed

    private void ctxMenuCancelOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctxMenuCancelOrderActionPerformed

    }//GEN-LAST:event_ctxMenuCancelOrderActionPerformed

    private void ordersListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersListMousePressed
        System.out.printf("Something hppened\n");
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
    private javax.swing.JButton sellButton;
    private traders.ManTrader.EditOrderPanel sellEditOrderPanel;
    private javax.swing.JPanel tradingPanel;
    // End of variables declaration//GEN-END:variables
}
