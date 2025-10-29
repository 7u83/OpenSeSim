/*
 * Copyright (c) 2025, 7u83 <7u83@mail.ru>
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
package sesim;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tube
 */
/**
 * Implements a trading account
 */
public class Account {

    private Exchange se;

    private Exchange.AccountListener listener = null;

    long shares;
    long money;

    long initial_shares;
    long initial_money;

    protected AutoTraderInterface owner;

    final ConcurrentHashMap<Long, Order> orders;

    Account(Exchange se, float money, float shares) {
        this.se = se;

        orders = new ConcurrentHashMap();

        // FLOAT_CONVERT
        this.money = (long) (money * se.money_df);
        initial_money = this.money;
        this.shares = (long) (shares * se.shares_df);
        initial_shares = this.shares;
    }

    public float getShares() {
        return shares / se.shares_df;
    }

    public float getInitialShares() {
        return initial_shares / se.shares_df;
    }

    public long getShares_Long() {
        return shares;
    }

    public float getMoney() {
        return money / se.money_df;
    }

    public float getInitialMoney() {
        return initial_money / se.money_df;
    }

    public long getMoney_Long() {
        return money;
    }

    public AutoTraderInterface getOwner() {
        return owner;
    }

    /*  public ConcurrentHashMap<Long, Exchange.Order> getOrders() {
        return orders;
    }*/
    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    public void setListener(Exchange.AccountListener al) {
        this.listener = al;
    }

    public void update(Order o) {
        if (listener == null) {
            return;
        }
        listener.accountUpdated(this, o);
    }

    public int getNumberOfOpenOrders() {

        return orders.size();
    }

    public long getCashInOpenOrders_Long(long exclude) {
        Iterator<Map.Entry<Long, Order>> it = this.getOrders().entrySet().iterator();
        long cash = 0;

        while (it.hasNext()) {
            Map.Entry e = it.next();
            Order o = (Order) e.getValue();
            if (o.isBuy() && o.hasLimit() && o.id != exclude) {
                cash += (o.getInitialVolume() - o.getExecuted_Long()) * o.getLimit_Long();
            }
        }
        return cash;

    }

    public long getCashInOpenOrders_Long() {
        return getCashInOpenOrders_Long(-1);
    }

    public float getCashInOpenOrders() {
        return getCashInOpenOrders_Long() / se.money_df;
    }

    public long getSharesInOpenOrders_Long(long exclude) {
        Iterator<Map.Entry<Long, Order>> it = this.getOrders().entrySet().iterator();
        long volume = 0;

        while (it.hasNext()) {
            Map.Entry e = it.next();
            Order o = (Order) e.getValue();
            if (o.isSell() && o.id != exclude) {
                volume += o.getInitialVolume() - o.getExecuted();
            }
        }
        return volume;
    }

    public long getSharesInOpenOrders_Long() {
        return getSharesInOpenOrders_Long(-1);
    }

    public float getSharesInOpenOrders() {
        return getSharesInOpenOrders_Long() / se.shares_df;
    }

    /*    public boolean couldBuy(float volume, float limit) {
        float avail = this.getMoney() - this.getCashInOpenOrders();

        System.out.printf("CouldBuy %f<%f\n", volume * limit, avail);
        return volume * limit <= avail;
    }

    public boolean couldSell(float volume) {
        float avail = this.getShares() - this.getSharesInOpenOrders();
        return volume <= avail;
    }*/
    public float getSharesAvailable() {
        return getShares() - getSharesInOpenOrders();
    }

    public long getCashAvailabale_Long() {
        return this.money - this.getCashInOpenOrders_Long();
    }

    public float getCashAvailable() {
        return this.getMoney() - this.getCashInOpenOrders();
    }

    public Order getOrderByID(long oid) {
        return orders.get(oid);
    }

    /**
     * Checks whether an order is covered using long-integer arithmetic.
     * <p>
     * Sell orders are checked against available shares. Limited buy orders are
     * checked against available cash. Other order types are assumed to be
     * covered and are handled by the matching engine.
     * <p>
     * The order type is a bit field. Relevant flags for this method:
     * <ul>
     * <li>{@code Order.SELL = 0x01} – sell order</li>
     * <li>{@code Order.LIMIT = 0x02} – limited order</li>
     * </ul>
     * Other flags (e.g., {@code Order.BUY = 0x00}, {@code Order.STOP = 0x04})
     * are ignored here.
     *
     * @param type The order type as a bit field (see above).
     * @param volume The number of shares to buy or sell, represented as a long
     * integer.
     * @param limit The price limit for limited orders, represented as a long
     * integer.
     * @return true if the order is covered; false if there are insufficient
     * shares or cash.
     */
    public boolean isOrderCovered_Long(byte type, long volume, long limit, long exclude) {

        // In case of a sell order just check the number of available shares
        if ((type & Order.SELL) != 0) {
            return volume <= this.getShares_Long() - this.getSharesInOpenOrders_Long(exclude);
        }

        // It's a buy order, we have just to check for limited orders
        if ((type & Order.LIMIT) != 0) {
            return volume * limit <= this.getMoney_Long() - this.getCashInOpenOrders_Long(exclude);
        }

        // all other types will be cecked by the matching engine
        return true;
    }

    /**
     * Checks whether an order is covered using floating-point inputs.
     * <p>
     * Converts the float volume and limit to long integers using the scaling
     * factors se.shares_df and se.money_df, and delegates to
     * {@link #isOrderCovered_Long(byte, long, long)}.
     *
     * @param type The order type, e.g., Order.SELL | Order.LIMIT.
     * @param volume The number of shares to buy or sell, as a float.
     * @param limit The price limit for limited orders, as a float.
     * @return true if the order is covered; false if there are insufficient
     * shares or cash.
     */
    public boolean isOrderCovered(byte type, float volume, float limit) {
        return isOrderCovered_Long(type,
                (long) (volume * se.shares_df),
                (long) (limit * se.money_df), -1
        );
    }

    public boolean isOrderCovered(byte type, float volume, float limit, long exclude) {
        return isOrderCovered_Long(type,
                (long) (volume * se.shares_df),
                (long) (limit * se.money_df), exclude
        );
    }

    public Exchange getSe() {
        return se;
    }

    public float gerPerformance(float lp) {
        float total = lp * getShares() + getMoney();
        float iniTotal = lp * getInitialShares() + getInitialMoney();
        return total / (iniTotal / 100) - 100;
    }

    public float getTotal(float lp) {
        return lp * getShares() + getMoney();
    }

}
