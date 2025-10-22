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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.JDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.Account;
//import sesim.AccountData;

import sesim.AutoTraderBase;

import sesim.AutoTraderGui;
import sesim.Exchange;

//import sesim.Exchange.Order;
//import sesim.OrderData;
import sesim.Quote;
import sesim.Scheduler.Event;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderB extends AutoTraderBase {

    public Float[] initial_delay = {0f, 5.0f};

    public Float[] sell_volume = {100f, 100f};
    public Float[] sell_limit = {-2f, 2f};
    public Long[] sell_wait = {10000L, 50000L};
    public Long[] wait_after_sell = {1000L, 30000L};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-2f, 2f};
    public Long[] buy_wait = {10000L, 50000L};
    public Long[] wait_after_buy = {10L, 30L};

    final String INITIAL_DELAY = "initla_delay";
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
        long delay = (long) (getRandom(initial_delay[0], initial_delay[1]) * 1000);
      //  se.timer.createEvent(this, delay);
    }

    @Override
    public long processEvent(long time, Event e) {
  //      sesim.Account account = account_id;
        long rc = this.doTrade();
        return rc;

    }

    @Override
    public String getDisplayName() {
        return "Random Trader B";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;
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

    @Override
    public void setConfig(JSONObject cfg) {
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
        return true;
    }

    public long cancelOrders() {
        int n = se.getNumberOfOpenOrders(account_id);
        if (n > 0) {
            sesim.Account ad = account_id;
            

            Set <Long>keys = ad.getOrders().keySet();
                    
           Iterator<Long> it = keys.iterator();
           while (it.hasNext()) {
      //          Order od = it.next();
                boolean rc = se.cancelOrder(account_id, it.next());
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

    Action mode=Action.RANDOM;
    
    long doTrade() {
        cancelOrders();
        Action a = getAction();
        switch (a) {
            case BUY: {
                boolean rc = doBuy();
                if (rc) {
                    mode = Action.BUY;
                    return getRandom(buy_wait);
                }
                return 5000;
            }

            case SELL:
            {
                boolean rc = doSell();
                if (rc){
                    mode = Action.SELL;
                    return getRandom(sell_wait);
                    
                }
                return 5000;
                
            }

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
    protected float getRandom(float min, float max) {
        float r = (float)se.randNextDouble();

        // System.out.printf("RD: %f", r);
        // System.exit(0);
        return (max - min) * r + min;
    }

    protected int getRandom(Long[] minmax) {
        return (int) Math.round(getRandom(minmax[0], minmax[1]));
    }

    float getStart() {

        return this.se.fairValue;

    }

    /**
     *
     * @param val
     * @param minmax
     * @return
     */
    protected float getRandomAmmount(float val, Float[] minmax) {

        //System.out.printf("RandomAmmount: %f (%f,%f)\n",val, minmax[0], minmax[1]);
        float min = val * minmax[0] / 100.0f;
        float max = val * minmax[1] / 100.0f;
        return getRandom(min, max);
    }

    public boolean doBuy() {

//        AccountData ad = this.se.getAccountData(account_id);

        sesim.Account account = account_id;

        byte type = Exchange.Order.BUYLIMIT;

        if (account == null) {
            return false;
        }

        // how much money we ant to invest?
        float money = getRandomAmmount(account.getMoney(), buy_volume);
    
        Quote q = se.getBestPrice_0();
        //q=se.getLastQuoete();
        float lp = q == null ? getStart() : q.price;

        float limit;
        limit = lp + getRandomAmmount(lp, buy_limit);


        float volume = money / limit;

    //    System.out.printf("Volume : %f", volume);
        
        limit = se.roundMoney(limit);
        volume = se.roundShares(volume);
        
        if (volume <= 0 || money <= 0) {
            return false;
        }

        se.createOrder(account, type, volume, limit,0f);

        return true;

    }

    public boolean doSell() {
        //   RandomTraderConfig myoldconfig = (RandomTraderConfig) this.oldconfig;
        //AccountData ad = this.se.getAccountData(account_id);
        
        Account account = account_id;

       byte type = Exchange.Order.SELLLIMIT;

               
        // how much shares we ant to sell?
        float volume = getRandomAmmount(account.getShares(), sell_volume);
        volume = se.roundShares(volume);
        

        //    float lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getBestPrice_0();
          //      q=se.getLastQuoete();
        float lp = q == null ? getStart() : q.price;
        
        
        
        

        float limit;
        limit = lp + getRandomAmmount(lp, sell_limit);
        se.roundMoney(limit);


        if (volume <= 0 || limit <=0) {
            return false;
        }

        se.createOrder(account, type, volume, limit,0f);
        
        return true;



    }

}
