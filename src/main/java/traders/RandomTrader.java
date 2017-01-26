/*
 * Copyright (c) 2017, 7u83
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

import java.util.*;
import java.util.Random;
//import java.util.TimerTask;
/*import sesim.AccountData;*/

import sesim.AutoTrader;
import sesim.Exchange;
import sesim.Exchange.OrderType;

import sesim.*;

/**
 *
 * @author 7u83
 */
public class RandomTrader extends AutoTrader {

    long event() {

        sesim.Exchange.Account a = se.getAccount(account_id);
        long rc = this.doTrade();
        return rc/80 ;

    }

    public RandomTrader(Exchange se, double money, double shares, RandomTraderConfig config) {
        super(se, money, shares, config);
        if (this.config == null) {
            this.config = new RandomTraderConfig();
        }

    }

    /**
     *
     * @return
     */
    @Override
    public long timerTask() {
        sesim.Exchange.Account a = se.getAccount(account_id);
        long rc = this.doTrade();
        return rc / 100;

//        return this.event();
    }

    protected enum Action {
        BUY, SELL, RANDOM
    }

    protected Action getAction() {
        if (rand.nextInt(2) == 0) {
            return Action.BUY;
        } else {
            return Action.SELL;
        }

    }

    double start = 1.1;
    //Timer timer = new Timer();

    @Override
    public void start() {

        //    timer.schedule(new TimerTaskImpl(this, timer), 0);
        se.timer.startTimerEvent(this, 0);

        //  timer.schedule(new TimerTaskImpl, date);
    }

    // config for this trader
    //final private RandomTraderConfig_old myconfig;
    // object to generate random numbers
    final private Random rand = new Random();

    /**
     * Get a (long) random number between min an max
     *
     * @param min minimum value
     * @param max maximeum value
     * @return the number
     */
    protected double getRandom(double min, double max) {
        double r = rand.nextDouble();
        return (max - min) * r + min;
    }

    protected int getRandom(int[] minmax) {
        return (int) Math.round(getRandom(minmax[0], minmax[1]));
    }

    /**
     *
     * @param val
     * @param minmax
     * @return
     */
    protected double getRandomAmmount(double val, float[] minmax) {
        double min = val * minmax[0] / 100.0;
        double max = val * minmax[1] / 100.0;
        return getRandom(min, max);
    }

    public long cancelOrders() {
        int n = se.getNumberOfOpenOrders(account_id);
//        System.out.print("Open Orders: "+n+"\n");
        if (n > 0) {
//            System.out.print("Want to killń\n");
            AccountData ad = se.getAccountData(account_id);
            Iterator<OrderData> it = ad.orders.iterator();
            while (it.hasNext()) {
                OrderData od = it.next();
                boolean rc = se.cancelOrder(account_id, od.id);
//                System.out.print("killer rc "+rc+"\n");
                //               System.out.print("Killing: "+od.id+"\n");
            }
        }

        return n;

    }

    public long doBuy() {
        RandomTraderConfig myconfig = (RandomTraderConfig) this.config;
        AccountData ad = this.se.getAccountData(account_id);

        OrderType type = OrderType.BID;

        if (ad == null || myconfig == null) {
//            System.out.print(ad + "\n");
            return 0;

        }
        // how much money we ant to envest?
        double money = getRandomAmmount(ad.money, myconfig.buy_volume);

        Quote q = se.getCurrentPrice();
        double lp = q == null ? start : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, myconfig.buy_limit);
        

        long volume = (long) (money / (limit * 1));
        if (volume <= 0) {
            return 0;
        }

//       double volume = (money / (limit * 1));
               if (volume <= 0) {
            return 0;
        }
       
        se.createOrder(account_id, type, volume, limit);

        return getRandom(myconfig.buy_order_wait);

    }

    public long doSell() {
        RandomTraderConfig myconfig = (RandomTraderConfig) this.config;
        AccountData ad = this.se.getAccountData(account_id);

        OrderType type = OrderType.ASK;

        // how much money we ant to envest?
        double volume =  (long)getRandomAmmount(ad.shares, myconfig.sell_volume);

        //    double lp = 100.0; //se.getBestLimit(type);
        Quote q = se.getCurrentPrice();
        double lp = q == null ? start : q.price;

        double limit;
        limit = lp + getRandomAmmount(lp, myconfig.sell_limit);

//        long volume = (long) (money / (limit * 1));
        if (volume <= 0) {
//            System.out.print("SellVolume 0\n");
            return 0;
        }

//        System.out.print("Volume is:"+volume+"\n");
        //               System.out.print("My Ammount is: "+volume+" My limit si:"+limit+ "\n");
        se.createOrder(account_id, type, volume, limit);

        return getRandom(myconfig.sell_order_wait);

    }

    long doTrade() {
        cancelOrders();
        Action a = getAction();
        switch (a) {
            case BUY:
                return doBuy();
            case SELL:
                return doSell();

        }
        return 0;

    }

    /*
    private static class TimerTaskImpl extends TimerTask {

        RandomTrader trader;
        Timer timer;

        public TimerTaskImpl(RandomTrader trader, Timer timer) {
            this.trader = trader;
            this.timer = timer;

        }

        @Override
        public void run() {

            long time = trader.event();
            time /= 100;

            this.cancel();
            timer.schedule(new TimerTaskImpl(trader, timer), time);

        }
    }
     */
}
