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
package traders;

import gui.Globals;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
//import sesim.Exchange.Account;
import sesim.Exchange.AccountListener;
import sesim.Exchange.Order;
import sesim.Exchange.OrderStatus;
import sesim.Exchange.OrderType;
import sesim.Quote;
import sesim.Scheduler.Event;
import sesim.Scheduler.EventProcessor;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderA1 extends AutoTraderBase 
        implements AccountListener {

    public Float[] initial_delay = {0f, 5.0f};

    public Float[] sell_volume = {100f, 100f};
    public Float[] sell_limit = {-2f, 2f};
    public Long[] sell_wait = {10000L, 50000L};
    public Long[] wait_after_sell = {0L, 0L};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-2f, 2f};
    public Long[] buy_wait = {10000L, 50000L};
    public Long[] wait_after_buy = {0L, 0L};

    final String INITIAL_DELAY = "initla_delay";
    final String SELL_VOLUME = "sell_volume";
    final String BUY_VOLUME = "buy_volume";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_WAIT = "sell_wait";
    final String BUY_WAIT = "buy_wait";
    final String WAIT_AFTER_SELL = "sell_wait_after";
    final String WAIT_AFTER_BUY = "buy_wait_after";
    
    private final Event TRADEEVENT = new Event(this);
 

    @Override
    public void start() {
        Account a = account_id;
        a.setListener(this);

        long delay = (long) (getRandom(initial_delay[0], initial_delay[1]) * 1000);
        setStatus("Inital delay: %d", delay);
     //   timerTask = se.timer.createEvent(this, delay);
        
//        se.timer.xAddEvent(TRADEEVENT, 0);
    }

    boolean intask = false;

    @Override
    public long processEvent(long time,Event e) {
        intask = true;
        owait = null;
        long rc = this.doTrade();
        setStatus("Sleeping for %d ms", rc);
        intask = false;
    //    setStatus("Return fromtask %d", rc);
        
        if (owait != null)
            rc = owait;
        
    //    se.timer.xAddEvent(TRADEEVENT, rc);
        return rc;

    }

    @Override
    public String getDisplayName() {
        return "Random Trader (A1)";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;
        //return new RandomTraderGuiA(this);
    }

    @Override
    public JSONObject getConfig() {
        JSONObject jo = new JSONObject();
        jo.put(INITIAL_DELAY, initial_delay);
        jo.put(SELL_VOLUME, sell_volume);
        jo.put(BUY_VOLUME, buy_volume);
        jo.put(SELL_LIMIT, sell_limit);
        jo.put(BUY_LIMIT, buy_limit);
        jo.put(SELL_WAIT, sell_wait);
        jo.put(BUY_WAIT, buy_wait);
        jo.put(WAIT_AFTER_SELL, wait_after_sell);
        jo.put(WAIT_AFTER_BUY, wait_after_buy);
        jo.put("base", this.getClass().getCanonicalName());

        return jo;
    }



    private Float[] to_float(JSONArray a) {
        Float[] ret = new Float[a.length()];
        for (int i = 0; i < a.length(); i++) {
         //   ret[i] = new Float(a.getDouble(i));

        }
        return ret;
    }

    private Long[] to_long(JSONArray a) {
        Long[] ret = new Long[a.length()];
        for (int i = 0; i < a.length(); i++) {
            ret[i] = a.getLong(i);

        }
        return ret;

    }

    Long owait = null;

    @Override
    public void putConfig(JSONObject cfg) {
        if (cfg == null) {
            return;
        }

        try {
            initial_delay = to_float(cfg.getJSONArray(INITIAL_DELAY));
            sell_volume = to_float(cfg.getJSONArray(SELL_VOLUME));
            buy_volume = to_float(cfg.getJSONArray(BUY_VOLUME));
            sell_limit = to_float(cfg.getJSONArray(SELL_LIMIT));
            buy_limit = to_float(cfg.getJSONArray(BUY_LIMIT));
            sell_wait = to_long(cfg.getJSONArray(SELL_WAIT));
            buy_wait = to_long(cfg.getJSONArray(BUY_WAIT));

            wait_after_sell = to_long(cfg.getJSONArray(WAIT_AFTER_SELL));
            wait_after_buy = to_long(cfg.getJSONArray(WAIT_AFTER_BUY));
        } catch (Exception e) {

        }

    }

    @Override
    public boolean getDevelStatus() {
        return false;
    }

    public long cancelOrders() {
        int n = se.getNumberOfOpenOrders(account_id);
        if (n > 0) {
            Account ad = account_id;

            Set<Long> keys = ad.getOrders().keySet();

            Iterator<Long> it = keys.iterator();
            while (it.hasNext()) {
                //          Order od = it.next();
                boolean rc = se.cancelOrder(account_id, it.next());
            }
        }
        return n;

    }

    @Override
    public JDialog getGuiConsole() {
        return null;
    }

    sesim.Scheduler.Event timerTask;

    @Override
    public void accountUpdated(Account a, Exchange.Order o) {
        setStatus("Account update -%s ", o.getStatus().toString());
        setStatus("In Task: %s", Boolean.toString(this.intask));
        //System.out.printf("Order updated %s %d\n", o.getStatus().toString(), Thread.currentThread().getId());
        if (o.getStatus() == OrderStatus.CLOSED) {

         //   if (!intask) {
                Long w = waitAfterOrder();
                
                setStatus("Reschedule %d", w);
                
                
                //se.timer.rescheduleTimerTask(timerTask, w);
//                se.timer.xDelEvent(TRADEEVENT);
  //              se.timer.xAddEvent(TRADEEVENT, w);
                
         //   } else {
              //  owait = waitAfterOrder();
           // }

        }
//        System.out.printf("Updatetd Account\n", "");

    }

    protected enum Action {
        BUY, SELL, RANDOM
    }

    protected Action getAction() {
        if (se.randNextInt(2) == 0) {
            return Action.BUY;
        } else {
            return Action.SELL;
        }

    }

    Action mode = Action.RANDOM;

    Integer doTrade1(Action a) {
        setStatus("doTrade1 with action %s", a.toString());
        switch (a) {
            case BUY: {
                boolean rc = doBuy();
                if (rc) {
            //        setStatus("dobuy");
                    mode = Action.BUY;
                    return getRandom(buy_wait);
                }
                //    System.out.printf("Buy failed\n");
                return null;
            }

            case SELL: {
                boolean rc = doSell();
                if (rc) {
                    setStatus("dosell");
                    mode = Action.SELL;
                    return getRandom(sell_wait);

                }
                //     System.out.printf("Sell failed\n");
                return null;

            }

        }
        return 0;

    }

    long waitAfterOrder() {
        if (mode == Action.BUY) {
            mode = Action.RANDOM;
            long r = getRandom(wait_after_buy);
            setStatus("Wait after buy: %d ms", r);
            return r;
        }

        if (mode == Action.SELL) {
            mode = Action.RANDOM;
            long r = getRandom(wait_after_sell);
            setStatus("Wait after sell: %d ms", r);
            return r;
        }

//        System.out.printf("Return action 0\n");
        return 0;

    }

    long doTrade() {

        long co = cancelOrders();
        setStatus("Orders cancled: %d", co);
        if (co > 0) {
            mode = Action.RANDOM;
        }

        Action a = getAction();

//        System.out.printf("Action is %s\n", a.toString());
        if (mode == Action.RANDOM) {

            setStatus("Mode is %s, next action is %s", mode.toString(), a.toString());

//            System.out.printf("Action: %s\n", a.toString());
            Integer rc = doTrade1(a);
            if (rc != null) {
                setStatus("Action %s successfull, ret %d", a.toString(), rc);
                return rc;
            }

            rc = doTrade1(Action.BUY);
            if (rc != null) {
                setStatus("BuyAction %s successfull, ret %d", a.toString(), rc);
                return rc;
            }

            rc = doTrade1(Action.SELL);
            if (rc != null) {
                setStatus("SellAction %s successfull, ret %d", a.toString(), rc);
                return rc;
            }

            setStatus("No trade possible, returning 5000");
            //System.out.printf("All ha s failed\n");
            return 5007;
        }

        setStatus("Current mode is %s", mode.toString());
        return waitAfterOrder();
    }

    /**
     * Get a (long) random number between min an max
     *
     * @param min minimum value
     * @param max maximeum value
     * @return the number
     */
    protected double getRandom(double min, double max) {
        double r = se.randNextDouble();

        // System.out.printf("RD: %f", r);
        // System.exit(0);
        return (max - min) * r + min;
    }

    protected int getRandom(Long[] minmax) {
        return (int) Math.round(getRandom(minmax[0], minmax[1]));
    }

    double getStart() {

        return this.se.fairValue;

    }

    /**
     *
     * @param val
     * @param minmax
     * @return
     */
    protected double getRandomAmmount(double val, Float[] minmax) {

        //System.out.printf("RandomAmmount: %f (%f,%f)\n",val, minmax[0], minmax[1]);
        double min = val * minmax[0] / 100.0;
        double max = val * minmax[1] / 100.0;
        return getRandom(min, max);
    }

    public boolean doBuy() {

//        AccountData ad = this.se.getAccountData(account_id);
        Account account = account_id;

        Exchange.OrderType type = Exchange.OrderType.BUYLIMIT;

        if (account == null) {
            return false;
        }

        // how much money we ant to invest?
        double money = getRandomAmmount(account.getMoney(), buy_volume);

        Quote q = se.getBestPrice_0();
        //q=se.getLastQuoete();
        double lp = q == null ? getStart() : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, buy_limit);

        //System.out.printf("Creating Buy Order from lp: %f, %f\n",lp,limit);
        double volume = money / limit;

        //    System.out.printf("Volume : %f", volume);
        limit = se.roundMoney(limit);
        volume = se.roundShares(volume);

        //    if (volume <= 0 || money <= 0) {
        //        System.out.printf("Buy failed %f, %f / %f (%f)\n", volume,money,limit,ad.getMoney());
//            System.out.printf("Buy Order wont work\n");
        //        return false;
        //    }
        
//        System.out.printf("I am: %s create BuyOrder. Money: %f, Shares: %f",
//                this.getName(),
//                this.account_id.getMoney(), this.account_id.getShares());
        
        Order rc = se.createOrder(account, type, volume, limit);
        

        if (rc == null) {

//            System.out.printf("Buy failed %f, %f / %f (%f)\n", volume, money, limit, ad.getMoney());
            return false;
        }
        //System.out.printf("Creating Sell Order from lp: %f, %f\n",lp,limit);

        return true;

    }

    public boolean doSell() {
        //   RandomTraderConfig myoldconfig = (RandomTraderConfig) this.oldconfig;
        //AccountData ad = this.se.getAccountData(account_id);

        Account account = account_id;

        Exchange.OrderType type = Exchange.OrderType.SELLLIMIT;

        // how much shares we ant to sell?
        double volume = getRandomAmmount(account.getShares(), sell_volume);
        volume = se.roundShares(volume);

        //    double lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getBestPrice_0();
        //      q=se.getLastQuoete();
        double lp = q == null ? getStart() : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, sell_limit);
        se.roundMoney(limit);

        //    if (volume <= 0 || limit <= 0) {
        //                    System.out.printf("Sell failed %f, %f (%f)\n", volume,limit,ad.getMoney());
//            System.out.printf("Sell wont work\n");
        //        return false;
        //    }
//        System.out.printf("Create a Sell Order %f %f!!!!\n", volume, limit);
        Order rc = se.createOrder(account, type, volume, limit);
        return rc != null;

    }

}
