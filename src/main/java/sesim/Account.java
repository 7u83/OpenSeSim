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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

    long cash;  // cash available

    long initial_shares;
    long initial_money;

    protected AutoTraderInterface owner;

    final ConcurrentHashMap<Long, Order> orders;
    private final HashMap<Position, Position> positions;

    int leverage = 10;

    //   Position defaultPosition;
    Account(Exchange se, float money, float shares) {
        this.se = se;

        orders = new ConcurrentHashMap();
        positions = new HashMap<>();

        // FLOAT_CONVERT
        this.cash = (long) (money * se.money_df);
        initial_money = this.cash;
        //     this.shares = (long) (shares * se.shares_df);
        initial_shares = (long) (shares * se.shares_df);

        //     defaultPosition = new Position(se,1);
        //   defaultPosition.shares = (long) (shares * se.shares_df);
        getPosition(se, 1).shares = (long) (shares * se.shares_df);

    }

    public Map<PositionKey, Position> getPositions() {
        return Collections.unmodifiableMap(positions);
    }

    // Summe der gebundenen Margin
    public long getMarginUsed_Long() {
        long totalMargin = 0;
        for (Position pos : positions.values()) {
            totalMargin += pos.getMargin_Long();
        }
        return totalMargin;
    }

    public float getMarginUsed() {
        return getMarginUsed_Long() / se.money_df;
    }

    public float getShares() {
        return getPosition(se, 1).shares / se.shares_df;
    }

    public float getInitialShares() {
        return initial_shares / se.shares_df;
    }

    public long getShares_Long() {
        return getPosition(se, 1).shares;
    }

    public float getMoney() {
        return cash / se.money_df;
    }

    public float getInitialMoney() {
        return initial_money / se.money_df;
    }

    public long getMoney_Long() {
        return cash;
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

    public float getSharesAvailable() {
        return getShares() - getSharesInOpenOrders();
    }

    public long getCashAvailabale_Long() {
        return this.cash - this.getCashInOpenOrders_Long();
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

    /**
     * Return the total value if all share would be sold to the last price
     *
     * @param lastPrice last price
     * @return the total value
     */
    public float getTotal(float lastPrice) {
        return lastPrice * getShares() + getMoney();
    }

    public float getPerformance(float lastPrice) {

        float total = lastPrice * getShares() + getMoney();
        float iniTotal = lastPrice * getInitialShares() + getInitialMoney();

        return total / (iniTotal / 100) - 100;

    }

    // Equity = Cash + unrealized PnL aller Positionen
    public long getEquity_Long() {
        long equity = cash;
        for (Position pos : positions.values()) {
            long price = pos.se.getLastPrice_Long();
            equity += pos.getUnrealizedPnL_Long(price);
        }
        return equity;
    }

    public float getEquity() {
        return getEquity_Long() / se.money_df;
    }

    // Free Margin = Equity − MarginUsed
    public long getFreeMargin_Long() {
        return getEquity_Long() - getMarginUsed_Long();
    }

    public float getFreeMargin() {
        return getFreeMargin_Long() / se.money_df;
    }

    Position createPosition() {
        //    Position p = new Position();
//        positions.put(p,p);
        return null;
    }

    final Position getPosition(Exchange se, int leverage) {
        Position k = new Position(se, 1);
        Position p = positions.get(k);
        if (p != null) {
            return p;
        }
        positions.put(k, k);
        return k;
    }

    HashSet<Position> x = new HashSet<>();

    public class PositionKey {

        final Exchange se;
        final int leverage;

        PositionKey(Exchange se, int leverage) {
            this.se = se;
            this.leverage = leverage;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PositionKey)) {
                return false;
            }
            PositionKey key = (PositionKey) o;
            return leverage == key.leverage && se.equals(key.se);

        }

        @Override
        public final int hashCode() {
            return Objects.hash(se, leverage);
        }
    }

    public class Position extends PositionKey {

        long shares;
        long entryPrice;
        long margin;
        private boolean isShort;
        long borrowed = 0;

        public Position(Exchange se, int leverage) {
            super(se, leverage);
            shares = 0;
            entryPrice = 0;
            margin = 0;
        }

        public String getName() {
            return se.getName();
        }

        public float getShares() {
            return shares / se.shares_df;
        }

        public float getLeverage() {

            float leverage;
            if (getMargin() == 0) {
                leverage = 1;
            } else {
                leverage = getTotalEntryCost() / getMargin();
            }
            return leverage;
        }

        public float getMargin() {
            return getMargin_Long() / se.money_df;
        }

        public long getMargin_Long() {
            return Math.abs(margin);
        }

        public float getEntryPrice() {
            return entryPrice / se.money_df;
        }

        // Exposure = total Position value
        public long getExposure() {
            return Math.abs(shares * entryPrice);
        }

        // unrealized PnL für aktuelle Preis
        public long getUnrealizedPnL_Long(long currentPrice) {

            return currentPrice * shares - shadow_cash;

            //long diff = currentPrice - entryPrice;
            //return isShort ? -shares * diff : shares * diff;
        }

        public float getUnrealizedPnL() {
            return (se.getLastPrice_Long() * shares + shadow_cash) / se.money_df;

        }

        public float getTotalEntryCost() {
            return totalEntryCost / se.money_df;
        }

        long shadow_cash = 0;
        long totalEntryCost = 0;

        public float getNetBrokerLoan() {
            return shadow_cash / se.money_df;
        }
        public boolean mops = true;

        void addShares(long volume, long price, int leverage) {
            long val = volume * price;
            shadow_cash -= val;
            totalEntryCost += val;

            /*    if (leverage == 1 && volume > 0) {
            shares += volume;
            cash -= val;
            return;
        }*/
            long sharesBefore = shares;

            // 1. Positionserweiterung (Zukauf: shares und volume haben gleiches Vorzeichen)
            if (Long.signum(sharesBefore) == Long.signum(volume) || sharesBefore == 0) {

                // Führt zu Zukauf (Long->Long oder Short->Short).
                // Hier muss die Initial Margin des neuen Trades hinzugefügt werden.
                long marginNewTrade = Math.abs(val / leverage);

                shares += volume;
                margin += marginNewTrade;
                cash -= marginNewTrade; // Ziehe die benötigte Initial Margin vom Cash ab

            } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
            else {
                shares += volume;

                // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes Vorzeichen als sharesBefore.
                if (Long.signum(shares) != Long.signum(sharesBefore)) {
                    // Alte Position komplett schließen (Margin freigeben), neue eröffnen (Margin binden).

                    // 1. Alte Margin komplett freigeben
                    cash += margin;

                    // 2. Neue Margin für den "Überhang" berechnen
                    long marginNewPosition = Math.abs(shares * price) / leverage;

                    cash -= marginNewPosition;
                    margin = marginNewPosition;

                } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
                else {
                    // Hier ist Ihr Prinzip der anteiligen Reduzierung korrekt.
                    // Die Margin muss proportional zum geschlossenen Teil reduziert werden.

                    // Anteil des geschlossenen Teils: |volume| / |sharesBefore| (mit 1000er Faktor)
                    long reductionFactor = Math.abs(volume) * 1000 / Math.abs(sharesBefore);
                    long marginReduction = (margin * reductionFactor) / 1000;

                    margin -= marginReduction; // Reduziere die aggregierte Margin
                    cash += marginReduction;   // Freigegebene Margin zurück zu Cash
                }
            }

            if (shares == 0 || (shares > 0 && shadow_cash + margin >= 0)) {

                //cash += margin;
                cash += shadow_cash + margin;
                //     cash+=Math.abs(margin);
                margin = 0;
                shadow_cash = 0;
                return;
            }

        }

        void xaddShares(long volume, long price, int leverage) {

            long val = volume * price;
            shadow_cash -= val;

            if (leverage == 1 && volume > 0) {
                shares += volume;
                cash -= val;
                return;
            }

            long sharesBefore = shares;

            // 1. Positionserweiterung (Zukauf: shares und volume haben gleiches Vorzeichen)
            if (Long.signum(sharesBefore) == Long.signum(volume) || sharesBefore == 0) {

                // Führt zu Zukauf (Long->Long oder Short->Short).
                // Hier muss die Initial Margin des neuen Trades hinzugefügt werden.
                long marginNewTrade = Math.abs(val / leverage);

                shares += volume;
                margin += marginNewTrade;
                cash -= marginNewTrade; // Ziehe die benötigte Initial Margin vom Cash ab

            } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
            else {
                shares += volume;

                // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes Vorzeichen als sharesBefore.
                if (Long.signum(shares) != Long.signum(sharesBefore)) {
                    // Alte Position komplett schließen (Margin freigeben), neue eröffnen (Margin binden).

                    // 1. Alte Margin komplett freigeben
                    cash += margin;

                    // 2. Neue Margin für den "Überhang" berechnen
                    long marginNewPosition = Math.abs(shares * price) / leverage;

                    cash -= marginNewPosition;
                    margin = marginNewPosition;

                } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
                else {
                    // Hier ist Ihr Prinzip der anteiligen Reduzierung korrekt.
                    // Die Margin muss proportional zum geschlossenen Teil reduziert werden.

                    // Anteil des geschlossenen Teils: |volume| / |sharesBefore| (mit 1000er Faktor)
                    long reductionFactor = Math.abs(volume) * 1000 / Math.abs(sharesBefore);
                    long marginReduction = (margin * reductionFactor) / 1000;

                    margin -= marginReduction; // Reduziere die aggregierte Margin
                    cash += marginReduction;   // Freigegebene Margin zurück zu Cash
                }
            }

            if (shares == 0 || (shares > 0 && shadow_cash + margin >= 0)) {

                //cash += margin;
                cash += shadow_cash + margin;
                //     cash+=Math.abs(margin);
                margin = 0;
                shadow_cash = 0;
                return;
            }

            /*         long val = volume * price;

            netBrokerLoan -= val;
            totalEntryCost += val;

            shares += volume;
            long marginRequired = Math.abs(netBrokerLoan / leverage);

            if (shares == 0 || (shares > 0 && netBrokerLoan + marginRequired >= 0)) {
                cash += netBrokerLoan + margin;
                margin = 0;
                netBrokerLoan = 0;
                totalEntryCost = 0;
                return;
            }

            cash += margin - marginRequired;
            margin = marginRequired;*/
        }

        //      margin += marginRequired;
/*            if (shares == 0) {
                entryPrice = 0;
            } else {
                entryPrice = ((oldshares * entryPrice) + (volume * price)) / (oldshares + volume);
            }*/
//borrowed = (leverage > 1) ? Math.abs(shares * entryPrice - margin) : 0;

        /*if (shares == 0) {
                entryPrice = price;
                shares = volume;
            } else {
                // gewichteter Einstiegspreis
                entryPrice = (entryPrice * shares + price * volume) / (shares + volume);
                shares += volume;
            }
         
            // Margin = gebundenes Geld
            margin = Math.abs(shares * entryPrice / leverage);

            // Short-Flag setzen
            isShort = shares < 0;

            // borrowed nur bei Short oder gehebeltem Long
            borrowed = (leverage > 1) ? Math.abs(shares * entryPrice - margin) : 0;*/
    }

}
