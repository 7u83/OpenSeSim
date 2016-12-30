/*
 * Copyright (c) 2016, 7u83 <7u83@mail.ru>
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
package Traders;

import SeSim.Account;
import SeSim.Order;
import java.util.Random;
import SeSim.AutoTrader;
import SeSim.TraderConfig;

public class RandomTrader extends AutoTrader {
    
    protected enum Action {
        sell,buy
    }

    // config for this trader
    final private RandomTraderConfig myconfig;

    // object to generate random numbers
    final private Random rand = new Random();

    public RandomTrader(Account account, TraderConfig config) {
        super(account, config);
        if (config == null) {
            config = new RandomTraderConfig();
        }
        myconfig = (RandomTraderConfig) config;
    }

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

    public boolean waitForOrder(long seconds) {

        for (int i = 0; (i < seconds) && (0 != account.pending.size()); i++) {
            doSleep(1);
        }

        if (account.pending.size() != 0) {
            Order o = account.pending.get(0);
            account.se.CancelOrder(o);
            return false;
        }
        return true;
    }

    public boolean doBuy() {

        double money = getRandomAmmount(account.money, myconfig.sell_volume);

        double lp = account.se.getlastprice();
        double limit;
        limit = lp + getRandomAmmount(lp, myconfig.buy_limit);

        long volume = (int) (money / (limit * 1));
        if (volume <= 0) {
            return false;
        }

        buy(volume, limit);
        return waitForOrder(getRandom(myconfig.buy_order_wait));

    }

    public boolean doSell() {

        long volume;
        volume = (long) Math.round(getRandomAmmount(account.shares, myconfig.sell_volume));

        double lp = account.se.getlastprice();
        double limit;
        limit = lp + getRandomAmmount(lp, myconfig.sell_limit);

        sell(volume, limit);
        return waitForOrder(getRandom(myconfig.sell_order_wait));

    }

    /*    private boolean monitorTrades() {

        int numpending = account.pending.size();
        if (numpending == 0) {
//            System.out.print("RT: pending = 0 - return false\n");
            return false;
        }

        Order o = account.pending.get(0);
        long age = o.getAge();

        // System.out.print("RT: age is: "+age+"\n");
        if (age > myconfig.maxage) {
            //         System.out.print("MaxAge is"+myconfig.maxage+"\n");
            account.se.CancelOrder(o);
//            System.out.print("Age reached - canel return false\n");
            return false;
        }

        //System.out.print("RT: monitor return true\n");
        return true;
    }
     */
    public void trade() {

        float am[] = {-10, 200};

        double x = Math.round(this.getRandomAmmount(1000, am));
        /*        System.out.print(
                "Random:"
                + x
                + "\n"
        );
        
         */
 /*
        //  System.out.print("RT: Now trading\n");
        if (monitorTrades()) {
            return;
        }

        // What next to do?
        int action = rand.nextInt(5);

        if (account.money < 10 && account.shares < 5) {
            System.out.print("I'm almost ruined\n");
        }

        if (action == 1) {
            doBuy();
            return;
        }

        if (action == 2) {
            doSell();
            return;
        }
         */
    }

    protected Action getAction() {
        if (rand.nextInt(2)==0){
            return Action.buy;
        }
        else{
            return Action.sell;
        }
             
            
    }

    @Override
    public void run() {
//        System.out.print("Starting Random Trader\n");
        while (true) {
            
// What next to do? 
            Action action = getAction();

            if (account.isRuined()) {
//                System.out.print("I'm ruined\n");
//                System.exit(0);
            }
            boolean rc;
            //    action=1;
            switch (action) {

                case sell:
                    if (account.shares <= 0) {
                        // we have no shares
                        continue;
                    }
//                    System.out.print("Sell\n");
                    rc = doSell();
                    if (!rc) {
                        continue;
                    }
//                    System.out.print("Sold\n");
                    doSleep(getRandom(myconfig.wait_after_sell));
//                    System.out.print("Next\n");
                    break;
                case buy:
                    if (account.money <= 0) {
                        // we have no money
                        continue;
                    }
//                    System.out.print("Sell\n");
                    rc = doBuy();
                    if (!rc) {
                        continue;
                    }
//                    System.out.print("Bought\n");
                    doSleep(getRandom(myconfig.wait_after_buy));
//                    System.out.print("Next\n");
                    break;

            }
            //          doSleep(1);

        }

    }

}
