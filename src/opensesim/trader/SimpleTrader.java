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
package opensesim.trader;

import opensesim.world.AbstractAsset;
import opensesim.world.AbstractTrader;
import opensesim.world.AssetPair;
import opensesim.world.Exchange;
import opensesim.world.Order;
import opensesim.world.TradingAPI;
import opensesim.world.World;
import opensesim.world.scheduler.Event;
import opensesim.world.scheduler.FiringEvent;
import opensesim.world.scheduler.EventListener;
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
        AbstractAsset c,a;

        AssetPair p = getWorld().getDefaultAssetPair();
        
        ex = getWorld().getDefaultExchange();
        api = ex.getAPI(p);        
        Order o = api.createOrder(account, Order.Type.BUY, 100, 200);        
        

        long delay = (long) (1000.0f * getWorld().randNextFloat(3.0f, 12.7f));
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

        System.out.printf("Here we are: %d - [%d]\n", Thread.currentThread().getId(), diff);
        getWorld().schedule(this, 1000);
        
                AssetPair p = getWorld().getDefaultAssetPair();
        
        ex = getWorld().getDefaultExchange();
        api = ex.getAPI(p);        
        Order o = api.createOrder(account, Order.Type.BUY, 112.987123, limit);       
        limit += 12;
        
        return -1;
    }

}
