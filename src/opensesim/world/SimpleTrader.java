/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.world;

import opensesim.util.scheduler.Event;
import opensesim.util.scheduler.EventListener;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SimpleTrader extends AbstractTrader implements EventListener {

    Exchange ex = null;

    TradingAPI api;

    @Override
    public String getStrategyTypeName() {
        return "Very Simple Trader";
    }

    public SimpleTrader(World world, JSONObject cfg) {
        super(world, cfg);
        if (cfg == null) {
            return;
        }

    }

    public SimpleTrader() {
        this(null, null);
    }

    float initial_delay[] = new float[2];

    /**
     * Get a (long) random number between min an max
     *
     * @param min minimum value
     * @param max maximeum value
     * @return the number
     */
    protected float getRandom(float min, float max) {
        //double r = se.randNextDouble();

        // System.out.printf("RD: %f", r);
        // System.exit(0);
        //   return (max - min) * r + min;
        return 0;
    }

    public Account account_b, account_1;

    @Override
    public void start() {
        setVerbose(true);
        setStatus("Trader started");

        // set exchange if we haven't one already
        if (ex == null) {
            ex = getWorld().getDefaultExchange();
        }
        if (ex != null) {
            this.log(String.format("Exchange is %s", ex.getName()));
        } else {
            this.log("No exchange. Stopping.");
            setStatus("Stopped.");
            return;
        }
        AbstractAsset c, a;

        AssetPair p = getWorld().getDefaultAssetPair();

        account_b = new Account(getWorld());
        account_1 = new Account(getWorld());

        AssetPack pack;
      //  pack = new AssetPack(p.getAsset(), 0);
      //  account_b.add(pack);

        pack = new AssetPack(p.getCurrency(), 1000);
       account_b.add(pack);
        account_b.setLeverage(9);

        pack = new AssetPack(p.getCurrency(), 10000);
        account_1.add(pack);
        account_1.setLeverage(0.0);
        account_1.setUnlimied(true);

        long delay = (long) (1000.0f * getWorld().randNextFloat(5.0f, 5.7f));
        setStatus(String.format("Initial delay: Sleeping for %d seconds.", delay));

        getWorld().schedule(this, delay);

        //  long delay = (long) (getRandom(initial_delay[0], initial_delay[1]) * 1000);
        //    setStatus("Inital delay: %d", delay);
        //   timerTask = se.timer.startTimerTask(this, delay);
    }

    long last_time = 0;

    double limit = 253.871239;

    @Override
    public long receive(Event task) {
       //   System.out.printf("Here we are !!! %f\n", getWorld().randNextFloat(12f, 27f));

        long diff = getWorld().currentTimeMillis() - last_time;
        last_time = getWorld().currentTimeMillis();

        AssetPair p = getWorld().getDefaultAssetPair();

        ex = getWorld().getDefaultExchange();
        api = ex.getAPI(p);

        AssetPair msftp = getWorld().getAssetPair(getWorld().getAssetBySymbol("MSFT"),
                getWorld().getAssetBySymbol("EUR"));
        TradingAPI mapi = ex.getAPI(msftp);

        Order ob = api.createOrder(account_b, Order.Type.BUYLIMIT, 20, 100);
      Order obm = mapi.createOrder(account_b, Order.Type.BUYLIMIT, 20, 100);
             

Order oba = api.createOrder(account_1, Order.Type.SELLLIMIT, 10, 100);
Order obam = mapi.createOrder(account_1, Order.Type.SELLLIMIT, 20, 100);



        return -1;
    }

}
