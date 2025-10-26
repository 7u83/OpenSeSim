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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange.AccountListener;
import sesim.Order;
import sesim.Quote;
import sesim.Scheduler.Event;
import sesim.Sim;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderLong extends AutoTraderBase
        implements AccountListener {

    public long[] initial_delay = {0, 7000};

    public float[] sell_volume = {100f, 100f};
    public long[] sell_limit = {-20, 20};
    public long[] sell_wait = {10000, 50000};
    public long[] wait_after_sell = {0, 0};

    public float[] buy_volume = {100f, 100f};
    public long[] buy_limit = {-20, 20};
    public long[] buy_wait = {0, 50000};
    public long[] wait_after_buy = {0, 0};

    public float[] wait_after_fail = {1f, 15f};

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

        long delay = getRandom(initial_delay[0], initial_delay[1]);

        setStatus("Inital delay: %d", delay);

        tradeEventTime = delay + sim.getCurrentTimeMillis();
        sim.addEvent(tradeEventTime, TRADEEVENT);
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

            if (account_id.getShares() == 0 && account_id.getMoney() < 0.3) {
                setStatus("Ruined");
                return 0;
            }

            if (currentOrder == null) {
                t = doBuyOrSell();
            } else {
                t = handleCurrentOrder();

            }
            //       System.out.printf("Add evebt %d\n", t);
            tradeEventTime = t + sim.getCurrentTimeMillis();
            sim.addEvent(tradeEventTime, e);
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
        return "Random Trader (Long)";
    }

    @Override
    public AutoTraderGui getGui() {
        return null; //new RandomTraderGuiA(this);
    }

    @Override
    public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();
        cfg.put(INITIAL_DELAY, initial_delay);
        cfg.put(SELL_VOLUME, sell_volume);
        cfg.put(BUY_VOLUME, buy_volume);
        cfg.put(SELL_LIMIT, sell_limit);
        cfg.put(BUY_LIMIT, buy_limit);
        cfg.put(SELL_WAIT, sell_wait);
        cfg.put(BUY_WAIT, buy_wait);
        cfg.put(WAIT_AFTER_SELL, wait_after_sell);
        cfg.put(WAIT_AFTER_BUY, wait_after_buy);
        cfg.put("base", this.getClass().getCanonicalName());

        return cfg;
    }

    private float[] to_float(JSONArray a) {
        float[] ret = new float[a.length()];
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
        //System.out.printf("put config sr: %s\n", cfg.toString(9));

        if (cfg == null) {
            return;
        }

        try {
            initial_delay[0] = (long) (1000 * cfg.getJSONArray(INITIAL_DELAY).getDouble(0));
            initial_delay[1] = (long) (1000 * cfg.getJSONArray(INITIAL_DELAY).getDouble(1));

            sell_volume = to_float(cfg.getJSONArray(SELL_VOLUME));
            buy_volume = to_float(cfg.getJSONArray(BUY_VOLUME));

            sell_limit[0] = (long) (10 * cfg.getJSONArray(SELL_LIMIT).getDouble(0));
            sell_limit[1] = (long) (10 * cfg.getJSONArray(SELL_LIMIT).getDouble(1));

            buy_limit[0] = (long) (10 * cfg.getJSONArray(BUY_LIMIT).getDouble(0));
            buy_limit[1] = (long) (10 * cfg.getJSONArray(BUY_LIMIT).getDouble(1));

      
            sell_wait[0] = (long) (1000 * cfg.getJSONArray(SELL_WAIT).getDouble(0));
            sell_wait[1] = (long) (1000 * cfg.getJSONArray(SELL_WAIT).getDouble(1));

            buy_wait[0] = (long) (1000 * cfg.getJSONArray(BUY_WAIT).getDouble(0));
            buy_wait[1] = (long) (1000 * cfg.getJSONArray(BUY_WAIT).getDouble(1));
            //buy_wait = to_float(cfg.getJSONArray(BUY_WAIT));

            wait_after_sell[0] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_SELL).getDouble(0));
            wait_after_sell[1] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_SELL).getDouble(1));

            wait_after_buy[0] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_BUY).getDouble(0));
            wait_after_buy[1] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_BUY).getDouble(1));
            //wait_after_buy = to_float(cfg.getJSONArray(WAIT_AFTER_BUY));

        } catch (JSONException e) {

            //        System.out.printf("Some exception has accoured\n", buy_wait);
            throw (e);
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
    public void accountUpdated(Account a, Order o) {

        if (currentOrder == null) {
            return;
        }

        if (currentOrder.getStatus() != Order.CLOSED) {
            return;
        }

        //System.out.printf("ORDER Closed: %s\n", this.getName());
        boolean rc = sim.delEvent(tradeEventTime, TRADEEVENT);

        //     System.out.printf("Cancel %s rc for %d = %b\n",getName(),tradeEventTime,rc);
        if (currentOrder.getType() == Order.BUYLIMIT) {
            tradeEventTime = getRandom(wait_after_buy[0], wait_after_buy[1]);
        } else {
            tradeEventTime = getRandom(wait_after_sell[0], wait_after_sell[1]);
        }

        tradeEventTime += sim.getCurrentTimeMillis();
        currentOrder = null;
        sim.addEvent(tradeEventTime, TRADEEVENT);

        // System.out.printf("Closed Scheduled for %s - %d\n",getName(),tradeEventTime);
    }

    private enum Action {
        BUY, SELL
    }

    private Action getAction() {
        if (sim.randNextInt(2) == 0) {
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
        currentOrder = null;

        byte s = o.getStatus();
        if (s == Order.OPEN || s == Order.PARTIALLY_EXECUTED) {
            se.cancelOrder(account_id, o.getID());
            currentOrder = null;
            setStatus("Sleep after timeout");
            return (long) (getRandom(wait_after_fail) * 1000f);
        }

        if (o.getType() == Order.BUYLIMIT) {
            setStatus("Sleep after buy");
            long r = getRandom(wait_after_buy[0], wait_after_buy[1]);
            return r;
        }
        setStatus("Sleep after sell");
        return getRandom(wait_after_sell[0], wait_after_sell[1]);
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
                    //return getRandom(wait_after_fail) * 1000;
                    float r = getRandom(wait_after_fail) * 1000f;
                    //      return 5000;
                    return (long) r;

                }
                if (o.getStatus() == Order.CLOSED) {
                    setStatus("Sleep after buy");
                    return getRandom(wait_after_buy[0], wait_after_buy[1]);

                }
                this.currentOrder = o;
                setStatus("Waiting for buy order");
                return getRandom(buy_wait[0], buy_wait[1]);

            case SELL:
                o = doSell();
                if (o == null) {
                    setStatus("Sell failed");
                    float r = getRandom(wait_after_fail) * 1000f;
                    //   System.out.printf("R: %f",r);
                    return (long) r;
                    //  return 5000;
                }
                if (o.getStatus() == Order.CLOSED) {
                    setStatus("Sleep after sell");
                    return getRandom(wait_after_sell[0], wait_after_sell[1]);

                }
                this.currentOrder = o;
                setStatus("Waiting for sell order");
                return getRandom(sell_wait[0], sell_wait[1]);

        }

        return (long) (getRandom(wait_after_fail) * 1000f);

//       return 5000;
    }

    /**
     * Get a (long) random number between min an max
     *
     * @param min minimum value
     * @param max maximeum value
     * @return the number
     */
    protected float getRandom(float min, float max) {
        double r = sesim.Sim.randNextDouble();

        // System.out.printf("RD: %f", r);
        // System.exit(0);
        return (float) ((max - min) * r + min);
    }

    protected long getRandom(long min, long max) {
        double r = sesim.Sim.randNextDouble();
        return (long) ((max - min) * r + min);
    }

    protected float getRandom(float[] minmax) {
        return getRandom(minmax[0], minmax[1]);
//        return  Math.round(getRandom(minmax[0], minmax[1]));
    }

    /*    private float getStart() {

        return this.se.fairValue;

    }*/
    /**
     *
     * @param val
     * @param minmax
     * @return
     */
    private float getRandomAmmount(float val, float[] minmax) {

        float min = val * minmax[0] / 100.0f;
        float max = val * minmax[1] / 100.0f;
        return getRandom(min, max);
    }

    long getRandomPrice_Long(long lastPrice, long minPercent, long maxPercent) {
        // minPercent/maxPercent sind Fixkommawerte mit 1 Nachkommastelle
        // Beispiel: -50 = -5.0%, +25 = +2.5%

        // 1. Preisänderungsspanne berechnen (in Cent)
        long minDelta = (lastPrice * minPercent) / 1000;
        long maxDelta = (lastPrice * maxPercent) / 1000;

        // 2. Sicherheitskorrektur, falls Rundung zu 0 führt
        if (minDelta == 0 && minPercent != 0) {
            minDelta = (minPercent < 0) ? -1 : 1;
        }
        if (maxDelta == 0 && maxPercent != 0) {
            maxDelta = (maxPercent < 0) ? -1 : 1;
        }

        // 3. Zufällige Preisänderung zwischen minDelta und maxDelta
        long delta = Sim.random.nextLong(maxDelta - minDelta + 1) + minDelta;

        // 4. Neuer Preis in Cent
        long newPrice = lastPrice + delta;

        // Optional: Absicherung gegen negative Preise
        if (newPrice < 1) {
            newPrice = 1;
        }

        return newPrice;
    }

    double getRandomPrice(double lastPrice, double tickSize, double minPercent, double maxPercent) {

        // 1. Prozentbereich in Ticks umrechnen
        int minTicks = (int) Math.round(lastPrice * minPercent / 100 / tickSize);
        int maxTicks = (int) Math.round(lastPrice * maxPercent / 100 / tickSize);

        if (minTicks > -1 && minTicks < 1) {
            minTicks = (minPercent < 0) ? -1 : 1;
        }
        if (maxTicks > -1 && maxTicks < 1) {
            maxTicks = (maxPercent < 0) ? -1 : 1;
        }

        // 3. Zufällig eine Tickzahl auswählen
        int tickChange = sim.random.nextInt(maxTicks - minTicks + 1) + minTicks;

        // 4. Neue Order berechnen
        double newPrice = lastPrice + tickChange * tickSize;

        // Optional: auf Tickgröße runden (um Rundungsfehler zu vermeiden)
        newPrice = Math.round(newPrice / tickSize) * tickSize;

        return newPrice;
    }

    private Order doBuy() {

        // how much money we ant to invest?
        long money = (long)getRandomAmmount(account_id.getMoney_Long(), buy_volume);

        Quote q = se.getBestPrice_0();
        long lp = q.getPrice_Long();

        long limit = this.getRandomPrice_Long(lp,  this.buy_limit[0], this.buy_limit[1]);

        long volume = money / limit;

 

        return se.createOrder_Long(account_id, Order.BUYLIMIT, volume, limit, 0);

    }

    private Order doSell() {

        // how many shares we want to sell?
        long volume = (long)getRandomAmmount(account_id.getShares_Long(), sell_volume);
      //  volume = se.roundShares(volume);

        //    float lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getBestPrice_0();
        long lp = q.getPrice_Long();

        long limit =  this.getRandomPrice_Long(lp, this.sell_limit[0], this.sell_limit[1]);
        //   limit = lp + getRandomAmmount(lp, sell_limit);
        //  limit = lp + se.random.nextLong(0, 4) - 2;

 
        return se.createOrder_Long(account_id, Order.SELLLIMIT, volume, limit, 0);

    }

}
