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

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Exchange.Order;
import sesim.Exchange.OrderStatus;
import sesim.Exchange.OrderType;
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
        se.timer.addEvent(se.timer.getCurrentTimeMillis()
                + (long) (1000f * this.timerInterval), TIMEREVENT);

    }

    /**
     * Creates and initializes the market maker orders
     */
    private void initOrders() {
        float centerPrice = se.getLastPrice();

        // Lowest price for buy orders
        float lowestPrice = centerPrice - depthPercent * centerPrice / 100f;

        orders = new MMOrder[numPositions];

        // Spacing between orders
        float dist = (centerPrice - lowestPrice) / (numPositions + 1);

        // Allocate cash per order
        float cashPerBuyOrder = account_id.getMoney() / (numPositions + 1);

        float price = lowestPrice + dist;

        for (int i = 0; i < numPositions; i++) {
            orders[i] = new MMOrder();
            orders[i].buyLimit = se.roundMoney(price);
            price += dist;
            orders[i].sellLimit = se.roundMoney(price);
            orders[i].volume = se.roundShares(cashPerBuyOrder / price);

            // Create initial buy order
            orders[i].o = se.createOrder(account_id, Exchange.OrderType.BUYLIMIT,
                    orders[i].volume, orders[i].buyLimit);

        }

        setStatus("%f - %f", lowestPrice, centerPrice);

    }

    /**
     * Flips closed orders to the opposite order type (buy ↔ sell)
     */
    private void flipOrders() {
        for (int i = 0; i < numPositions; i++) {
            Order o = orders[i].o;
            if (o.getStatus() == OrderStatus.CLOSED) {
                if (o.getType() == OrderType.SELLLIMIT) {
                    // Create new buy order if previous was sell
                    Order n = se.createOrder(account_id, OrderType.BUYLIMIT,
                            orders[i].volume, orders[i].buyLimit);
                    if (n != null) {
                        orders[i].o = n;
                    }
                } else {
                    // Create new sell order if previous was buy
                    Order n = se.createOrder(account_id, OrderType.SELLLIMIT,
                            orders[i].volume, orders[i].sellLimit);
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

        // Do nothing if any order is still open
        for (int i = 0; i < numPositions; i++) {
            OrderStatus s = orders[i].o.getStatus();
            if (s != OrderStatus.OPEN) {
                return false;
            }
        }

        float price = se.getLastPrice();
        if (price <= orders[numPositions - 1].sellLimit) {
            return false;  // No adjustment needed
        }

        // Cancel all current orders
        for (int i = 0; i < numPositions; i++) {
            se.cancelOrder(account_id, orders[i].o.getID());
        }

        this.initOrders(); // Reinitialize orders with new prices
        return true;

    }

    /**
     * Processes the scheduled timer event.
     *
     * @param time current time in milliseconds
     * @param e the event to process
     * @return 0 (not used)
     */
    @Override
    public long processEvent(long time, Scheduler.Event e) {
        if (!readjustOrders()) {
            this.flipOrders();  // Flip orders if no readjustment
        }
        
        // Schedule the next timer event
        se.timer.addEvent(se.timer.getCurrentTimeMillis() + (long) (1000f * this.timerInterval), TIMEREVENT);
        return 0;
    }

    @Override
    public boolean getDevelStatus() {
        return true;

    }

    @Override
    public String getDisplayName() {
        return "MarketMaker";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;

    }

    @Override
    public JSONObject getConfig() {
        return new JSONObject();
    }

    @Override
    public void putConfig(JSONObject cfg) {

    }

}
