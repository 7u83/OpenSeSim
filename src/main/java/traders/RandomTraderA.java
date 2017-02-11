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
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.AccountData;
import sesim.AutoTrader;
import sesim.AutoTraderBase;
import sesim.AutoTraderConfig;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.OrderData;
import sesim.Quote;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderA extends AutoTraderBase  {

    public Float[] sell_volume = {100f, 100f};
    public Float[] sell_limit = {-2f, 2f};
    public Long[] sell_wait = {10000L, 50000L};
    public Long[] wait_after_sell = {1000L, 30000L};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-2f, 2f};
    public Long[] buy_wait = {10000L, 50000L};
    public Long[] wait_after_buy = {10L, 30L};

    final String SELL_VOLUME = "sell_volume";
    final String BUY_VOLUME = "buy_volume";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_WAIT = "sell_wait";
    final String BUY_WAIT = "buy_wait";
    final String WAIT_AFTER_SELL = "sell_wait_after";
    final String WAIT_AFTER_BUY = "buy_wait_after";

    @Override
    public void start() {
        se.timer.startTimerEvent(this, 0);
    }

    @Override
    public long timerTask() {
        sesim.Exchange.Account a = se.getAccount(account_id);
        long rc = this.doTrade();
        return rc;

    }

  /*  @Override
    public AutoTrader createTrader(Exchange se, JSONObject cfg, long id, String name, double money, double shares) {
            return null;
    }
*/
    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public AutoTraderGui getGui() {
        return new RandomTraderGuiA(this);
    }

    @Override
    public JSONObject getConfig() {
        JSONObject jo = new JSONObject();
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
            ret[i] = new Float(a.getDouble(i));

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

    @Override
    public void putConfig(JSONObject cfg) {
        if (cfg == null) {
            return;
        }

        String cname = cfg.get(SELL_VOLUME).getClass().getName();

        sell_volume = to_float(cfg.getJSONArray(SELL_VOLUME));
        buy_volume = to_float(cfg.getJSONArray(BUY_VOLUME));
        sell_limit = to_float(cfg.getJSONArray(SELL_LIMIT));
        buy_limit = to_float(cfg.getJSONArray(BUY_LIMIT));
        sell_wait = to_long(cfg.getJSONArray(SELL_WAIT));
        buy_wait = to_long(cfg.getJSONArray(BUY_WAIT));

        wait_after_sell = to_long(cfg.getJSONArray(WAIT_AFTER_SELL));
        wait_after_buy = to_long(cfg.getJSONArray(WAIT_AFTER_BUY));

    }

    @Override
    public boolean getDevelStatus() {
        return false;
    }

    public long cancelOrders() {
        int n = se.getNumberOfOpenOrders(account_id);
        if (n > 0) {
            AccountData ad = se.getAccountData(account_id);
            Iterator<OrderData> it = ad.orders.iterator();
            while (it.hasNext()) {
                OrderData od = it.next();
                boolean rc = se.cancelOrder(account_id, od.id);
            }
        }
        return n;
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

    long doTrade() {
        cancelOrders();
        Action a = getAction();
        switch (a) {
            case BUY:
                return doBuy() + 1;
            case SELL:
                return doSell() + 1;

        }
        return 0;

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
        return Globals.se.fairValue;

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

    public long doBuy() {

        //     RandomTraderConfig myoldconfig = (RandomTraderConfig) this.oldconfig;
        AccountData ad = this.se.getAccountData(account_id);

        Exchange.OrderType type = Exchange.OrderType.BUYLIMIT;

        if (ad == null) {
            //System.out.printf("%s: myconf = 0 \n", this.getName());
            return 0;

        }

        // how much money we ant to invest?
        double money = getRandomAmmount(ad.money, buy_volume);

        Quote q = se.getCurrentPrice();
        double lp = q == null ? getStart() : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, buy_limit);

        // 10 
        // 24   13 
//        System.out.printf("MyLimit: %f\n",limit);
        long volume = (long) (money / limit);

        if (volume <= 0) {

            volume = 1;
            limit = money;

        }

//       double volume = (money / (limit * 1));
//        if (volume <= 0) {
        //           return 0;
        //      }
        //System.out.printf("%s: create order %s %f\n", this.getName(),type.toString(),limit);
        se.createOrder(account_id, type, volume, limit);

        return getRandom(buy_wait);

    }

    public long doSell() {
        //   RandomTraderConfig myoldconfig = (RandomTraderConfig) this.oldconfig;
        AccountData ad = this.se.getAccountData(account_id);

        Exchange.OrderType type = Exchange.OrderType.SELLLIMIT;

        //System.out.printf("%s: calling rand for volume\n", this.getName());                
        // how much money we ant to envest?
        double volume = (long) getRandomAmmount(ad.shares, sell_volume);

        //    double lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getCurrentPrice();
        double lp = q == null ? getStart() : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, sell_limit);

//        long volume = (long) (money / (limit * 1));
        if (volume <= 0) {
//            System.out.print("SellVolume 0\n");
            return 5000;
        }

//        System.out.print("Volume is:"+volume+"\n");
        //               System.out.print("My Ammount is: "+volume+" My limit si:"+limit+ "\n");
        //System.out.printf("%s: create order %s %f\n", this.getName(),type.toString(),limit);
        se.createOrder(account_id, type, volume, limit);

        return getRandom(sell_wait);

    }

}
