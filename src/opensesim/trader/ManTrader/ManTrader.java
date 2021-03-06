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
package opensesim.trader.ManTrader;

import opensesim.gui.Globals;
import opensesim.gui.OpenOrdersList;
import javax.swing.JDialog;
import org.json.JSONObject;

import opensesim.old_sesim.AutoTraderBase;
import opensesim.old_sesim.AutoTraderGui;
import opensesim.old_sesim.AutoTraderInterface;
import opensesim.old_sesim.Exchange;
import opensesim.old_sesim.Exchange.AccountListener;
import opensesim.old_sesim.Order.OrderStatus;
import opensesim.old_sesim.Order;
import opensesim.old_sesim.Account;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class ManTrader extends AutoTraderBase implements AccountListener, AutoTraderInterface{

//    public ManTrader(Exchange se, long id, String name, double money, double shares, AutoTraderConfig config) {
//        //  super(se, id, name, money, shares, null);
//        super();
//    }

    public ManTrader() {
        super();

    }

    @Override
    public void init(Exchange se, long id, String name, double money, double shares, JSONObject cfg) {
        super.init(se, id, name, money, shares, cfg);
        getAccount().setListener(this);
    }
    ManTraderConsoleDialog consoleDialog;

    @Override
    public void start() {
        se.timer.startTimerTask(this, 0);
        consoleDialog = new ManTraderConsoleDialog(Globals.frame, false, this.getAccount());
        this.consoleDialog.getBalancePanel().updateBalance(this.getAccount());
        // consoleDialog.     rdersList1.account=trader.getAccount();

//        consoleDialog.getConsole().trader=this;
        consoleDialog.setVisible(true);

    }

    @Override
    public long timerTask() {

//        OpenOrdersList ol = this.consoleDialog.getConsole().getOrderListPanel();
//        ol.updateModel();
        return 1000;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public AutoTraderGui getGui() {
        return null;
    }

    @Override
    public JSONObject getConfig() {
        System.out.printf("return new json object\n");
        return new JSONObject();
    }

    @Override
    public void putConfig(JSONObject cfg) {
        return;
    }

    @Override
    public boolean getDevelStatus() {
        return true;
    }

    @Override
    public JDialog getGuiConsole() {
        return this.consoleDialog;
    }

    @Override
    public void accountUpdated(Account a, Order o) {
        //this.consoleDialog.cons
        //System.out.printf("AccountListener called\n");

        //System.out.printf("%d %s\n", o.getID(), o.getOrderStatus().toString());

        if (o.getOrderStatus()==OrderStatus.CLOSED){
            o.getAccount().getOrders().put(o.getID(), o);
        }
        this.consoleDialog.getOrderList().updateModel();
        this.consoleDialog.getBalancePanel().updateBalance(o.getAccount());
    }

}
