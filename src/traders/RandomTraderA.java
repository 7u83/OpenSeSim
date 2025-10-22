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

import javax.swing.JDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Exchange.AccountListener;
import sesim.Exchange.Order;
import sesim.Exchange.OrderStatus;
import sesim.Exchange.Order;
import sesim.Quote;
import sesim.Scheduler.Event;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderA extends AutoTraderBase
        implements AccountListener {

    public Float[] initial_delay = {0f, 7.0f};

    public Float[] sell_volume = {100f, 100f};
    public Float[] sell_limit = {-2f, 2f};
    public Float[] sell_wait = {10.0f, 50.0f};
    public Float[] wait_after_sell = {0f, 0f};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-2f, 2f};
    public Float[] buy_wait = {10f, 50f};
    public Float[] wait_after_buy = {0f, 0f};

    public Float[] wait_after_fail = {10f, 20f};

    final String INITIAL_DELAY = "initial_delay";
    final String SELL_VOLUME = "sell_volume";
    final String BUY_VOLUME = "buy_volume";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_WAIT = "sell_wait";
    final String BUY_WAIT = "buy_wait";
    final String WAIT_AFTER_SELL = "sell_wait_after";
    final String WAIT_AFTER_BUY = "buy_wait_after";
    final String WAIT_AFTER_FAIL = "wait_after_fail";

    long wait_after_timeout = 1000;

    private final Event TRADEEVENT = new Event(this);
    private long tradeEventTime;

    private final Event ORDERFILLEDEVENT = new Event(this);

    private Order currentOrder = null;

    @Override
    public void start() {
        this.TRADEEVENT.name = this.getName();
        this.ORDERFILLEDEVENT.name = this.getName();
        Account a = account_id;
        a.setListener(this);

        long delay = (long) (getRandom(initial_delay[0], initial_delay[1]) * 1000);
        setStatus("Inital delay: %d", delay);

        tradeEventTime = delay + se.timer.getCurrentTimeMillis();
        se.timer.addEvent(tradeEventTime, TRADEEVENT);
    }

    // boolean intask = false;
    @Override
    public long processEvent(long time, Event e) {

        //System.out.printf("Process Event for %s %d\n",this.getName(),time);
        if (time != tradeEventTime) {
        //    System.out.printf("Wrong Event for %s: %d != %d\n", this.getName(), time, tradeEventTime);
            return 0;
        }
        if (e == this.TRADEEVENT) {

            long t = 0;

            if (currentOrder == null) {
                t = doBuyOrSell();
            } else {
                t = handleCurrentOrder();

            }
            tradeEventTime = t + se.timer.getCurrentTimeMillis();
            se.timer.addEvent(tradeEventTime, e);
//System.out.printf("Scheduled for %s - %d\n",getName(),tradeEventTime);
        }
        /*       if (e == this.ORDERFILLEDEVENT) {
            //          System.out.printf("Order filled: KILL %s, %d\n", TRADEEVENT.name, TRADEEVENT.time);
            //          long w = getSleepTimeAfterOrder();

        }*/

        return 0;

    }

    @Override
    public String getDisplayName() {
        return "Random Trader (A)";
    }

    @Override
    public AutoTraderGui getGui() {
        return new RandomTraderGuiA(this);
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
            //ret[i] = new Float(a.getDouble(i));
            ret[i] = (float) a.getDouble(i);

        }
        return ret;
    }

    /*    private Long[] to_long(JSONArray a) {
        Long[] ret = new Long[a.length()];
        for (int i = 0; i < a.length(); i++) {
            ret[i] = a.getLong(i);

        }
        return ret;

    }*/
    //   Long owait = null;
    @Override
    public void setConfig(JSONObject cfg) {
        System.out.printf("put config sr: %s\n", cfg.toString(9));
        
        if (cfg == null) {
            return;
        }
        
         

        try {
            initial_delay = to_float(cfg.getJSONArray(INITIAL_DELAY));
            sell_volume = to_float(cfg.getJSONArray(SELL_VOLUME));
            buy_volume = to_float(cfg.getJSONArray(BUY_VOLUME));
            sell_limit = to_float(cfg.getJSONArray(SELL_LIMIT));
            buy_limit = to_float(cfg.getJSONArray(BUY_LIMIT));
            sell_wait = to_float(cfg.getJSONArray(SELL_WAIT));
            buy_wait = to_float(cfg.getJSONArray(BUY_WAIT));

            wait_after_sell = to_float(cfg.getJSONArray(WAIT_AFTER_SELL));
            wait_after_buy = to_float(cfg.getJSONArray(WAIT_AFTER_BUY));
        } catch (JSONException e) {

    //        System.out.printf("Some exception has accoured\n", buy_wait);
            throw(e);
        }

    }

    @Override
    public boolean getDevelStatus() {
        return false;
    }

 /*   @Override
    public JDialog getGuiConsole() {
        return null;
    }
*/
    @Override
    public void accountUpdated(Account a, Exchange.Order o) {

        if (currentOrder == null) {
            return;
        }

        if (currentOrder.getStatus() != OrderStatus.CLOSED) {
            return;
        }

        //System.out.printf("ORDER Closed: %s\n", this.getName());
        boolean rc = se.timer.delEvent(tradeEventTime, TRADEEVENT);

   //     System.out.printf("Cancel %s rc for %d = %b\n",getName(),tradeEventTime,rc);
        if (currentOrder.getType() == Order.BUYLIMIT) {
            tradeEventTime = getRandom(wait_after_buy) * 1000;
        } else {
            tradeEventTime = getRandom(wait_after_sell) * 1000;
        }
        
        tradeEventTime += se.timer.getCurrentTimeMillis();
        currentOrder = null;
        se.timer.addEvent(tradeEventTime, TRADEEVENT);
          
        // System.out.printf("Closed Scheduled for %s - %d\n",getName(),tradeEventTime);

    }

    private enum Action {
        BUY, SELL
    }

    private Action getAction() {
        if (se.randNextInt(2) == 0) {
            return Action.BUY;
        } else {
            return Action.SELL;
        }

    }

    // private Action mode = Action.RANDOM;

    /* private long getSleepTimeAfterOrder(Order o) {

        if (o.getType() == Order.BUYLIMIT) {
            long r = getRandom(wait_after_buy) * 1000;
            return r;
        }

        return getRandom(wait_after_sell) * 1000;

    }*/
    private long handleCurrentOrder() {
        Order o = currentOrder;
        currentOrder=null;
        
        OrderStatus s = o.getStatus();
        if (s == OrderStatus.OPEN || s == OrderStatus.PARTIALLY_EXECUTED) {
            se.cancelOrder(account_id, o.getID());
            currentOrder = null;
            setStatus("Sleep after timeout");
            return wait_after_timeout;
        }

        if (o.getType() == Order.BUYLIMIT) {
            setStatus("Sleep after buy");
            long r = getRandom(wait_after_buy) * 1000;
            return r;
        }
        setStatus("Sleep after sell");
        return getRandom(wait_after_sell) * 1000;
    }

    private long doBuyOrSell() {
        Action a = getAction();

        Order o;
        //long t = 0;

        switch (a) {
            case BUY:
                o = doBuy();
                if (o == null) {
                    setStatus("Buy failed");
                    return 5000;
                }
                if (o.getStatus() == OrderStatus.CLOSED) {
                    setStatus("Sleep after buy");
                    return getRandom(wait_after_buy) * 1000;

                }
                this.currentOrder = o;
                setStatus("Waiting for buy order");
                return getRandom(buy_wait) * 1000;

            case SELL:
                o = doSell();
                if (o == null) {
                    setStatus("Sell failed");
                    return 5000;
                }
                if (o.getStatus() == OrderStatus.CLOSED) {
                setStatus("Sleep after sell");
                    return getRandom(wait_after_sell) * 1000;

                }
                this.currentOrder = o;
                setStatus("Waiting for sell order");
                return getRandom(sell_wait) * 1000;

        }

        
        return 5000;
    }

    /**
     * Get a (long) random number between min an max
     *
     * @param min minimum value
     * @param max maximeum value
     * @return the number
     */
    protected float getRandom(float min, float max) {
        float r = (float)se.randNextDouble();

        // System.out.printf("RD: %f", r);
        // System.exit(0);
        return (max - min) * r + min;
    }

    protected int getRandom(Float[] minmax) {
        return (int) Math.round(getRandom(minmax[0], minmax[1]));
    }

    private float getStart() {

        return this.se.fairValue;

    }

    /**
     *
     * @param val
     * @param minmax
     * @return
     */
    private float getRandomAmmount(float val, Float[] minmax) {

        float min = val * minmax[0] / 100.0f;
        float max = val * minmax[1] / 100.0f;
        return getRandom(min, max);
    }

    private Order doBuy() {

        byte type = Exchange.Order.BUYLIMIT;

        // how much money we ant to invest?
        float money = getRandomAmmount(account_id.getMoney(), buy_volume);

        Quote q = se.getBestPrice_0();
        //q=se.getLastQuoete();
        float lp = q == null ? getStart() : q.price;

        float limit;
        limit = lp + getRandomAmmount(lp, buy_limit);

        float volume = money / limit;

        limit = se.roundMoney(limit);
        volume = se.roundShares(volume);

        return se.createOrder(account_id, type, volume, limit,0f);

    }

    private Order doSell() {

        byte type = Exchange.Order.SELLLIMIT;

        // how many shares we want to sell?
        float volume = getRandomAmmount(account_id.getShares(), sell_volume);
        volume = se.roundShares(volume);

        //    float lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getBestPrice_0();

        float lp = q == null ? getStart() : q.price;

        float limit;
        limit = lp + getRandomAmmount(lp, sell_limit);
        se.roundMoney(limit);

        return se.createOrder(account_id, type, volume, limit,0f);

    }

}
