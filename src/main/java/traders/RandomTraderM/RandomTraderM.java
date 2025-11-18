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
package traders.RandomTraderM;

import traders.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Market.AccountListener;
import sesim.Order;
import sesim.Quote;
import sesim.Scheduler.Event;
import sesim.Scheduler.EventProcessor;
import sesim.Sim;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderM extends AutoTraderBase
        implements AccountListener {

    public long[] initialDelay = {0, 0};

    public long[] amountToBuy = {1000, 1000};
    public long[] amountToSell = {1000, 1000};

    public long minAmountToBuyDeviation = 0;
    public long minAmountToSellDeviation = 0;

    public long[] buyLimit = {-20, 20};
    public long[] sellLimit = {-20, 20};

    public long minBuyDeviation = 1;
    public long minSellDeviation = 1;

    public long[] buyOrderTimeout = {10000, 50000};
    public long[] sellOrderTimeout = {10000, 50000};

    public long[] sleepAfterBuy = {0, 0};
    public long[] sleepAfterSell = {0, 0};

    public float[] wait_after_fail = {1f, 5f};

    //  public float bankrupt_shares_cfg = 1f;
//    public float bankrupt_cash_cfg = 1f;
    public long bankrupt_shares = 0;
    public long bankrupt_cash = 0;
    
    
    public boolean moodEnable=false;
    public float moodiness=0.5f;
    public float moodFrequency=60.0f;
    
    int leverage=1;

    final String INITIAL_DELAY = "initial_delay";
    final String AMOUNT_TO_SELL = "amount_to_sell";
    final String AMOUNT_TO_BUY = "amount_to_buy";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_ORDER_TIMEOUT = "sell_order_timeout";
    final String BUY_ORDER_TIMEOUT = "buy_order_timeout";
    final String SLEEP_AFTER_SELL = "sleep_after_sell";
    final String SLEEP_AFTER_BUY = "sleep_after_buy";

    final String BANKRUPT_SHARES = "bankrupt_shares";
    final String BANKRUPT_CASH = "bankrupt_cash";

    final String MIN_AMOUNT_TO_SELL_DEVIATION = "min_amount_to_sell_deviation";
    final String MIN_AMOUNT_TO_BUY_DEVIATION = "min_amount_to_buy_deviation";
    final String MIN_BUY_DEVIATION = "min_buy_deviation";
    final String MIN_SELL_DEVIATION = "min_sell_deviation";
    
    final String MOODINESS = "modiness";
    final String MOOD_FREQUENCY = "mood_frequency";
    final String MOOD_ENABLE = "mood_enable";

    final String WAIT_AFTER_FAIL = "wait_after_fail";
    long wait_after_timeout = 1000;

    private final Event TRADEEVENT = new Event(this);
    private long tradeEventTime;

    private final Event ORDERFILLEDEVENT = new Event(this);

    private Order currentOrder = null;

    @Override
    public void start() {
        //bankrupt_shares = (long) (bankrupt_shares_cfg * se.shares_df);
        //bankrupt_cash = (long) (bankrupt_cash_cfg * se.money_df);
        //     this.TRADEEVENT.name = this.getName();
        //     this.ORDERFILLEDEVENT.name = this.getName();
        Account a = account;
        a.setListener(this);

        long delay = getRandom(initialDelay[0], initialDelay[1]);

        setStatus("Inital delay: %d", delay);

        tradeEventTime = delay + sim.getCurrentTimeMillis();
        sim.addEvent(tradeEventTime, TRADEEVENT);
    }

    // boolean intask = false;
    @Override
    public void processEvent(long time, Event e) {
        /*     if (getName().equals("Alice-0")) {
            System.out.printf("Alice is alive\n");
        }*/
        //System.out.printf("Process Event for %s %d\n",this.getName(),time);
        if (time != tradeEventTime) {
            //    System.out.printf("Wrong Event for %s: %d != %d\n", this.getName(), time, tradeEventTime);
          
        }
        if (e == this.TRADEEVENT) {

            long t = 0;

            if (account.getShares_Long() < this.bankrupt_shares && account.getMoney_Long() < this.bankrupt_cash) {
                setStatus("Ruined");
            
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

     

    }

    @Override
    public String getDisplayName() {
        return "Random Trader M";
    }

    @Override
    public AutoTraderGui getGui() {
        return new RandomTraderMGui(this);
    }

    @Override
    public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();
        double fields[];
        fields = new double[2];
        fields[0] = Math.round((initialDelay[0] / 1000.0) * 10) / 10.0;
        fields[1] = Math.round((initialDelay[1] / 1000.0) * 10) / 10.0;
        cfg.put(INITIAL_DELAY, fields);

//        cfg.put(AMOUNT_TO_BUY, amountToBuy);
//        cfg.put(AMOUNT_TO_SELL, amountToSell);
        fields = new double[2];
        fields[0] = Math.round((amountToBuy[0] / 10.0) * 10) / 10.0;
        fields[1] = Math.round((amountToBuy[1] / 10.0) * 10) / 10.0;
        cfg.put(AMOUNT_TO_BUY, fields);

        fields = new double[2];
        fields[0] = Math.round((amountToSell[0] / 10.0) * 10) / 10.0;
        fields[1] = Math.round((amountToSell[1] / 10.0) * 10) / 10.0;
        cfg.put(AMOUNT_TO_SELL, fields);

        fields = new double[2];
        fields[0] = Math.round((buyLimit[0] / 10.0) * 10) / 10.0;
        fields[1] = Math.round((buyLimit[1] / 10.0) * 10) / 10.0;
        cfg.put(BUY_LIMIT, fields);

        fields = new double[2];
        fields[0] = Math.round((sellLimit[0] / 10.0) * 10) / 10.0;
        fields[1] = Math.round((sellLimit[1] / 10.0) * 10) / 10.0;
        cfg.put(SELL_LIMIT, fields);

        fields = new double[2];
        fields[0] = Math.round((buyOrderTimeout[0] / 1000.0) * 10) / 10.0;
        fields[1] = Math.round((buyOrderTimeout[1] / 1000.0) * 10) / 10.0;
        cfg.put(BUY_ORDER_TIMEOUT, fields);

        fields = new double[2];
        fields[0] = Math.round((sellOrderTimeout[0] / 1000.0) * 10) / 10.0;
        fields[1] = Math.round((sellOrderTimeout[1] / 1000.0) * 10) / 10.0;
        cfg.put(SELL_ORDER_TIMEOUT, fields);

        fields = new double[2];
        fields[0] = Math.round((sleepAfterBuy[0] / 1000.0) * 10) / 10.0;
        fields[1] = Math.round((sleepAfterBuy[1] / 1000.0) * 10) / 10.0;
        cfg.put(SLEEP_AFTER_BUY, fields);

        fields = new double[2];
        fields[0] = Math.round((sleepAfterSell[0] / 1000.0) * 10) / 10.0;
        fields[1] = Math.round((sleepAfterSell[1] / 1000.0) * 10) / 10.0;
        cfg.put(SLEEP_AFTER_SELL, fields);

        cfg.put(BANKRUPT_SHARES, bankrupt_shares);
        cfg.put(BANKRUPT_CASH, bankrupt_cash);

        cfg.put(MIN_AMOUNT_TO_BUY_DEVIATION, this.minAmountToBuyDeviation);
        cfg.put(MIN_AMOUNT_TO_SELL_DEVIATION, this.minAmountToSellDeviation);
        cfg.put(MIN_BUY_DEVIATION, this.minBuyDeviation);
        cfg.put(MIN_SELL_DEVIATION, this.minSellDeviation);
        
        cfg.put(MOOD_ENABLE,moodEnable);
        cfg.put(MOOD_FREQUENCY,moodFrequency);
        cfg.put(MOODINESS,moodiness);


        /*     cfg.put(SELL_VOLUME, sell_volume);
        cfg.put(BUY_VOLUME, buy_volume);
        cfg.put(SELL_LIMIT, sell_limit);

        fields[0] = Math.round((buy_limit[0] / 10.0) * 10) / 10.0;
        fields[1] = Math.round((buy_limit[1] / 10.0) * 10) / 10.0;
        cfg.put(BUY_LIMIT, fields);

        cfg.put(SELL_WAIT, sell_wait);
        cfg.put(BUY_WAIT, buy_wait);
        cfg.put(WAIT_AFTER_SELL, wait_after_sell);
        cfg.put(WAIT_AFTER_BUY, wait_after_buy);
        cfg.put("base", this.getClass().getCanonicalName());
         */
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
            initialDelay[0] = (long) (1000 * cfg.getJSONArray(INITIAL_DELAY).getDouble(0));
            initialDelay[1] = (long) (1000 * cfg.getJSONArray(INITIAL_DELAY).getDouble(1));

            //amountToBuy = to_float(cfg.getJSONArray(AMOUNT_TO_BUY));
            //amountToSell = to_float(cfg.getJSONArray(AMOUNT_TO_SELL));
            amountToBuy[0] = (long) (10 * cfg.getJSONArray(AMOUNT_TO_BUY).getDouble(0));
            amountToBuy[1] = (long) (10 * cfg.getJSONArray(AMOUNT_TO_BUY).getDouble(1));

            amountToSell[0] = (long) (10 * cfg.getJSONArray(AMOUNT_TO_SELL).getDouble(0));
            amountToSell[1] = (long) (10 * cfg.getJSONArray(AMOUNT_TO_SELL).getDouble(1));

            buyLimit[0] = (long) (10 * cfg.getJSONArray(BUY_LIMIT).getDouble(0));
            buyLimit[1] = (long) (10 * cfg.getJSONArray(BUY_LIMIT).getDouble(1));

            sellLimit[0] = (long) (10 * cfg.getJSONArray(SELL_LIMIT).getDouble(0));
            sellLimit[1] = (long) (10 * cfg.getJSONArray(SELL_LIMIT).getDouble(1));

            buyOrderTimeout[0] = (long) (1000 * cfg.getJSONArray(BUY_ORDER_TIMEOUT).getDouble(0));
            buyOrderTimeout[1] = (long) (1000 * cfg.getJSONArray(BUY_ORDER_TIMEOUT).getDouble(1));

            sellOrderTimeout[0] = (long) (1000 * cfg.getJSONArray(SELL_ORDER_TIMEOUT).getDouble(0));
            sellOrderTimeout[1] = (long) (1000 * cfg.getJSONArray(SELL_ORDER_TIMEOUT).getDouble(1));

            sleepAfterBuy[0] = (long) (1000 * cfg.getJSONArray(SLEEP_AFTER_BUY).getDouble(0));
            sleepAfterBuy[1] = (long) (1000 * cfg.getJSONArray(SLEEP_AFTER_BUY).getDouble(1));

            sleepAfterSell[0] = (long) (1000 * cfg.getJSONArray(SLEEP_AFTER_SELL).getDouble(0));
            sleepAfterSell[1] = (long) (1000 * cfg.getJSONArray(SLEEP_AFTER_SELL).getDouble(1));

            bankrupt_shares = cfg.optLong(BANKRUPT_SHARES,0);
            bankrupt_cash = cfg.optLong(BANKRUPT_CASH,100);

//            if (se != null) {
//                bankrupt_shares = (long) (bankrupt_shares_cfg * se.shares_df);
//                bankrupt_cash = (long) (bankrupt_cash_cfg * se.money_df);
//            }
            minAmountToBuyDeviation = cfg.getLong(MIN_AMOUNT_TO_BUY_DEVIATION);
            minAmountToSellDeviation = cfg.getLong(MIN_AMOUNT_TO_SELL_DEVIATION);

            minBuyDeviation = cfg.getLong(MIN_BUY_DEVIATION);
            minSellDeviation = cfg.getLong(MIN_SELL_DEVIATION);
            
            moodEnable = cfg.optBoolean(MOOD_ENABLE, false);
            moodFrequency = (float)cfg.optDouble(MOOD_FREQUENCY,0.5f);
            moodiness = (float)cfg.optDouble(MOODINESS, 1.0);

            /*
            sell_wait[0] = (long) (1000 * cfg.getJSONArray(SELL_WAIT).getDouble(0));
            sell_wait[1] = (long) (1000 * cfg.getJSONArray(SELL_WAIT).getDouble(1));

            buy_wait[0] = (long) (1000 * cfg.getJSONArray(BUY_WAIT).getDouble(0));
            buy_wait[1] = (long) (1000 * cfg.getJSONArray(BUY_WAIT).getDouble(1));
            //buy_wait = to_float(cfg.getJSONArray(BUY_WAIT));

            wait_after_sell[0] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_SELL).getDouble(0));
            wait_after_sell[1] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_SELL).getDouble(1));

            wait_after_buy[0] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_BUY).getDouble(0));
            wait_after_buy[1] = (long) (1000 * cfg.getJSONArray(WAIT_AFTER_BUY).getDouble(1));
            //wait_after_buy = to_float(cfg.getJSONArray(WAIT_AFTER_BUY));*/
        } catch (JSONException e) {

            //        System.out.printf("Some exception has accoured\n", buy_wait);
            //throw (e);
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
            tradeEventTime = getRandom(sleepAfterBuy[0],           
                    (long)(sleepAfterBuy[1]*global.bmoodyness)
            );
        } else {
            tradeEventTime = getRandom(sleepAfterSell[0], 
            (long)(sleepAfterSell[1]*global.smoodyness)
            );
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
            se.cancelOrder(account, o.getID());

            currentOrder = null;
            setStatus("Sleep after timeout");
            return (long) (getRandom(wait_after_fail) * 1000f);
        }

        if (o.getType() == Order.BUYLIMIT) {
            setStatus("Sleep after buy");
            long r = (long) getRandom(sleepAfterBuy[0], 
                (long)(sleepAfterBuy[1]*global.bmoodyness)
            );
            return r;
        }
        setStatus("Sleep after sell");
        return getRandom(sleepAfterSell[0], 
        (long)(sleepAfterSell[1]*global.smoodyness)
        );
    }

    private long doBuyOrSell() {
        Action a = getAction();

        Order o;
        //long t = 0;

        switch (a) {
            case BUY:
                o = doBuy();
                if (o == null) {
                    setStatus("No trade possible");
                    //return getRandom(wait_after_fail) * 1000;
                    float r = getRandom(wait_after_fail) * 1000f;
                    //      return 5000;
                    return (long) r;

                }
                if (o.getStatus() == Order.CLOSED) {
                    setStatus("Holding");

                    return  getRandom(sleepAfterBuy[0], 
                            (long)(sleepAfterBuy[1]*global.bmoodyness)
                            );

                }
                this.currentOrder = o;
                setStatus("Waiting for buy order");
                return getRandom(buyOrderTimeout[0], buyOrderTimeout[1]);

            case SELL:
                o = doSell();
                if (o == null) {
                    setStatus("No trade possible");
                    float r = getRandom(wait_after_fail) * 1000f;
                    //   System.out.printf("R: %f",r);
                    return (long) r;
                    //  return 5000;
                }
                if (o.getStatus() == Order.CLOSED) {

                    setStatus("Cool down");
                    return getRandom(sleepAfterSell[0],
                    (long)(sleepAfterSell[1]*global.smoodyness)
                    );

                }
                this.currentOrder = o;
                setStatus("Waiting for sell order");
                return getRandom(sellOrderTimeout[0], sellOrderTimeout[1]);

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



    // static long minn = 10000000;
    //  static long maxn = -10;

    /*   double getRandomPrice(double lastPrice, double tickSize, double minPercent, double maxPercent) {

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
    }*/
    private Order doBuy() {
        long money_avail = account.getMoney_Long();
        // how much money we ant to invest?
        long money = getRandomPriceDelta_Long(money_avail, amountToBuy[0], amountToBuy[1], minAmountToBuyDeviation);
        if (money > money_avail) {
            money = money_avail;
        }

        Quote q = se.getBestPrice_0();
        long lp = q.getPrice_Long();

        long limit = this.getRandomPrice_Long(lp, this.buyLimit[0], this.buyLimit[1], minBuyDeviation);

        long volume = money / limit;

        return se.createOrder_Long(account, Order.BUYLIMIT, volume, limit, 0, leverage);

    }

    private Order doSell() {
        long shares = account.getShares_Long();
        // how many shares we want to sell?
        long volume = getRandomPriceDelta_Long(shares, amountToSell[0], amountToSell[1], minAmountToSellDeviation);
        if (volume > shares) {
            volume = shares;
        }

        //    float lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getBestPrice_0();
        long lp = q.getPrice_Long();

        long limit = this.getRandomPrice_Long(lp, this.sellLimit[0], this.sellLimit[1], minSellDeviation);
        //   limit = lp + getRandomAmmount(lp, sell_limit);
        //  limit = lp + se.random.nextLong(0, 4) - 2;

        return se.createOrder_Long(account, Order.SELLLIMIT, volume, limit, 0, leverage);

    }

    class Global implements EventProcessor {

        float currentMoodiness = 0.0f;
        float bmoodyness = 1.0f;
        float smoodyness = 1.0f;
        
        
   //     float moodiness = 0.5f;
        
        private final Event MYEVENT = new Event(this);

        Global(boolean enable) {
            if (!enable){
                return;
            }
            
            
            sim.addEvent(sim.getCurrentTimeMillis() + (long)(moodFrequency*1000), MYEVENT);
        }

        @Override
        public void processEvent(long time, Event e) {
            sim.addEvent(sim.getCurrentTimeMillis() + (long)(moodFrequency*1000), MYEVENT);
          //  long r = getRandom(0, 2);
          boolean r = Sim.random.nextBoolean();
       
            if (r) {
                currentMoodiness += moodiness;
            } else {
                currentMoodiness -= moodiness;
            }
            if (currentMoodiness>=0){
                bmoodyness=currentMoodiness+1.0f;
                smoodyness=1.0f;
            }else{
                smoodyness=Math.abs(currentMoodiness)+1.0f;
                bmoodyness=1.0f;
            }
            
    // System.out.printf("Hello %b, %f!\n", r, moodyness);
          
        }
    }

    private Global global;

    @Override
    public Object initGlobal(Sim sim, Object global, JSONObject cfg) {
        this.sim = sim;
        if (global != null) {
            this.global = (Global) global;
            return global;

        }
        
        

        this.global = new Global(cfg.optBoolean(MOOD_ENABLE));
        return this.global;

    }

}
