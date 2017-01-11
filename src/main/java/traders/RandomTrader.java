/*
 * Copyright (c) 2017, tobias
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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import sesim.AccountData;

import sesim.AutoTrader;
import sesim.Exchange;
import sesim.Exchange.OrderType;

/**
 *
 * @author tobias
 */
public class RandomTrader extends AutoTrader {

    static Timer timer = new Timer();

    enum Event {
        CANCEL,
        CREATE
    }

    class NextEvent {

        public Event event;
        public long time;

        NextEvent(Event e, long time) {
            this.event = e;
            this.time = time;
        }
    }

    public RandomTrader(Exchange se, double money, double shares, RandomTraderConfig config) {
        super(se, money, shares, config);
        if (this.config == null) {
            this.config = new RandomTraderConfig();
        }

    }

    @Override
    public void start() {

        timer.schedule(new TimerTaskImpl(this), 1000);

        //  timer.schedule(new TimerTaskImpl, date);
    }

    // config for this trader
    //final private RandomTraderConfig_old myconfig;
    // object to generate random numbers
    final private Random rand = new Random();

    /*public RandomTrader(Exchange se, double money,shares,) {
        //super(account, config);
        if (config == null) {
            config = new RandomTraderConfig_old();
        }
        myconfig = (RandomTraderConfig_old) config;
    }*/
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

    public boolean doBuy() {
        RandomTraderConfig myconfig = (RandomTraderConfig)this.config;
        AccountData ad = this.se.getAccountData(account_id);
        
        OrderType type=OrderType.BID;

        double money = getRandomAmmount(ad.money, myconfig.sell_volume);

        double lp = se.getBestLimit(type);
        
        double limit;
        limit = lp + getRandomAmmount(lp, myconfig.buy_limit);

        long volume = (int) (money / (limit * 1));
        if (volume <= 0) {
            return false;
        }

        return true;


    }

    protected NextEvent createOrder() {

        return new NextEvent(Event.CANCEL, 3000);
    }

    private static class TimerTaskImpl extends TimerTask {

        RandomTrader trader;
        NextEvent nextevent;

        public TimerTaskImpl(RandomTrader trader) {
            this.trader = trader;

        }

        @Override
        public void run() {
            switch (this.nextevent.event) {

            }

            this.cancel();
            timer.schedule(new TimerTaskImpl(trader), 1000);

        }
    }

}
