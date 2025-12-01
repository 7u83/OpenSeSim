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
package traders.MarketMaker;

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Order;
import sesim.Scheduler;
import sesim.Scheduler.Event;

/**
 *
 * Market maker trading bot that automatically places buy and sell limit orders.
 *
 * @author tube
 */
public class MarketMaker extends AutoTraderBase {

    /**
     * Number of positions to trade
     */
    int numPositions = 15;

    /**
     * Interval in seconds to run the trader
     */
    float timerInterval = 3;

    /**
     * Depth of the lowest order relative to the current market price, in
     * percent.
     *
     * For example, 8 means the lowest buy order is 8% below the current price.
     */
    float depthPercent = 8;
    
    
    float cashToUse = 50.0f;

    int interval = 1000;

    /**
     * Represents a single market maker order.
     */
    class MMOrder {

        Order o = null;     // Underlying order object
        float buyLimit;     // Limit price for buying
        float sellLimit;    // Limit price for selling
        float volume;       // Volume of shares to trade
    }

    /**
     * Array of market maker orders
     */
    private MMOrder[] orders;

    /**
     * Timer event for scheduling order checks
     */
    private Event TIMEREVENT;

    /**
     * Initializes orders and schedules the recurring timer event.
     */
    @Override
    public void start() {

        this.initOrders();

        TIMEREVENT = new Event(this); // Create event object

        // Schedule the first timer event
        sim.addEvent(sim.getCurrentTimeMillis()
                + (long) (interval * this.timerInterval), TIMEREVENT);

    }

    /**
     * Creates and initializes the market maker orders
     */
    private void initOrders() {
        float centerPrice = market.getLastPrice();

        // Lowest price for buy orders
        float lowestPrice = centerPrice - depthPercent * centerPrice / 100f;

        orders = new MMOrder[numPositions];

        // Spacing between orders
        float dist = (centerPrice - lowestPrice) / (numPositions + 1);

        // Allocate cash per order
        float cashPerBuyOrder = account.getMoney()* (cashToUse/100.0f) / (numPositions + 1);

        float price = lowestPrice + dist;

        for (int i = 0; i < numPositions; i++) {
            orders[i] = new MMOrder();
            orders[i].buyLimit = market.getCurrency().round(price);
            price += dist;
            orders[i].sellLimit = market.getCurrency().round(price);
            orders[i].volume = market.roundShares(cashPerBuyOrder / price);

            // Create initial buy order
            orders[i].o = market.createOrder(account, Order.BUYLIMIT,
                    orders[i].volume, orders[i].buyLimit, 0f);

        }

        
        setStatus("%s - %s", 
                market.getFormatter().format(lowestPrice),
                market.getFormatter().format(centerPrice)
        );

    }

    /**
     * Flips closed orders to the opposite order type (buy ↔ sell)
     */
    private void flipOrders() {
        for (int i = 0; i < numPositions; i++) {
            Order o = orders[i].o;
            if (orders[i].o == null) {
                continue;
            }

            if (o.getStatus() == Order.CLOSED) {
                if (o.getType() == Order.SELLLIMIT) {
                    // Create new buy order if previous was sell
                    Order n = market.createOrder(account, Order.BUYLIMIT,
                            orders[i].volume, orders[i].buyLimit, 0f);
                    if (n != null) {
                        orders[i].o = n;
                    }
                } else {
                    // Create new sell order if previous was buy
                    Order n = market.createOrder(account, Order.SELLLIMIT,
                            orders[i].volume, orders[i].sellLimit, 0f);
                    if (n != null) {
                        orders[i].o = n;
                    }
                }
            }
        }
    }

    /**
     * Cancels and reinitializes orders if the market price exceeds the highest
     * sell limit.
     *
     * @return true if orders were readjusted, false otherwise
     */
    private boolean readjustOrders() {

        float price = market.getLastPrice();
        if (price <= orders[numPositions - 1].sellLimit
                && price >= orders[0].buyLimit) {
            return false;  // No adjustment needed
        }

        // Cancel all current orders
        for (int i = 0; i < numPositions; i++) {
            if (orders[i].o == null) {
                continue;
            }
            market.cancelOrder(account, orders[i].o.getID());
        }

        resetTrader();
        return true;

    }

    Order resetOrder = null;

    void resetTrader() {
        setStatus("Reset");
        if (account.getPosition(sim.getDefaultMarket()).getShares() == 0) {
            initOrders();
            return;
        }

        resetOrder = market.createOrder_Long(account, Order.SELL, account.getShares_Long(sim.getDefaultMarket()), 0, 0, 1);

    }

    /**
     * Processes the scheduled timer event.
     *
     * @param time current time in milliseconds
     * @param e the event to process
     * @return 0 (not used)
     */
    @Override
    public void processEvent(long time, Scheduler.Event e) {
        if (resetOrder != null) {
            if (resetOrder.getStatus() != Order.CLOSED) {
                sim.addEvent(sim.getCurrentTimeMillis() + (long) (interval * this.timerInterval), TIMEREVENT);
                
                return;
            }
            resetOrder = null;
            this.initOrders();
        }

        if (!readjustOrders()) {
            this.flipOrders();  // Flip orders if no readjustment
        }

        // Schedule the next timer event
        sim.addEvent(sim.getCurrentTimeMillis() + (long) (interval * this.timerInterval), TIMEREVENT);

    }

    @Override
    public boolean getDevelStatus() {
        return true;

    }

    @Override
    public String getDisplayName() {
        return "Market Maker";
    }

    @Override
    public AutoTraderGui getGui() {
        return new MarkeMakerGui(this);

    }

    @Override
    public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();
        cfg.put("num_positions", this.numPositions);
        cfg.put("timer_interval", this.timerInterval);
        cfg.put("depth_percent", this.depthPercent);
        cfg.put("cash_to_use",this.cashToUse);
        return cfg;
    }

    @Override
    public void setConfig(JSONObject cfg) {
        this.numPositions=cfg.optInt("num_positions",16);
        this.timerInterval=(float)cfg.optDouble("timer_interval",3.0);
        this.depthPercent=(float) cfg.optDouble("depth_percent",8);
        this.cashToUse=(float) cfg.optDouble("cash_to_use",50);
    }

}
