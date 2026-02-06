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

import sesim.util.FixedPoint;
import java.util.Collections;
import java.util.HashMap;
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

    //  private final Market defaultMarket;
    final Asset currency;

    private Market.AccountListener listener = null;

    long cash;
    
    long maxMargin = 100;

  //  long initial_equity;

    protected AutoTrader owner;

    final ConcurrentHashMap<Long, Order> orders;
    private final HashMap<Market, Position> positions;

    private HashMap<Market, Position> snap_positions = new HashMap<>();
    private long snap_cash;

    /*    Account(Asset currency, Market se, double cash) {
        this.currency = currency;

        //this.defaultMarket = se;
        orders = new ConcurrentHashMap();
        positions = new HashMap<>();

        this.cash = currency.round_Long(FixedPoint.toInternal(cash));

        initial_equity = this.getEquity_Long();
    }*/
    Account(Asset currency, long initialCash) {
        orders = new ConcurrentHashMap();
        positions = new HashMap<>();
        this.currency = currency;
        this.cash = currency.round_Long(initialCash);
//        initial_equity = this.getEquity_Long();
    }

    Account(Asset currency, double initialCash) {
/*        orders = new ConcurrentHashMap();
        positions = new HashMap<>();
        this.currency = currency;
        this.cash = currency.getDf()*initialCash();
        initial_equity = this.getEquity_Long();*/
        this(currency,(long)(FixedPoint.toInternal(initialCash)));
    }

    void makeSnapShot() {
        snap_positions = new HashMap<>();
        for (Market m : positions.keySet()) {
            Position p = new Position(positions.get(m));
            snap_positions.put(m, p);
        }
        snap_cash = cash;
    }

    public Map<Market, Position> getPositions() {
        return Collections.unmodifiableMap(positions);
    }

    void cancelAllOrders() {
        for (Order o : orders.values()) {
            o.getMarket().cancelOrder(this, o.id);
        }
    }

    long getMarginUsed_Long() {
        long totalMargin = 0;
        for (Position pos : positions.values()) {
            totalMargin += pos.getMargin_Long();
        }
        return totalMargin;
    }

    public double getMarginUsed() {
        return FixedPoint.toExternal(getMarginUsed_Long()); // / currency.getDf();

    }

    public double getShares(Market m) {
        return FixedPoint.toExternal(getShares_Long(m));

    }

    public long getShares_Long(Market m) {
        return getPosition(m).getShares_Long();

    }

    public double getMoney() {
        return FixedPoint.toExternal(cash);
    }

    public long getMoney_Long() {
        return cash;
    }

    void addCash(double m) {
        cash += m * this.currency.getDf();
    }

    public AutoTrader getOwner() {
        return owner;
    }

    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    public void setListener(Market.AccountListener al) {
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
        long orderCash = 0;

        while (it.hasNext()) {
            Map.Entry e = it.next();
            Order o = (Order) e.getValue();
            if (o.isBuy() && o.hasLimit() && o.id != exclude) {
                orderCash += (o.getInitialVolume()
                        - FixedPoint.multiply(
                                o.getExecuted_Long(), o.getLimit_Long()));
            }
        }
        return orderCash;

    }

    public long getCashInOpenOrders_Long() {
        return getCashInOpenOrders_Long(-1);
    }

    public double getCashInOpenOrders() {
        return getCashInOpenOrders_Long() / currency.getDf();
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
        return 0;
    }

    public long getCashAvailabale_Long() {
        return this.cash; // - this.getCashInOpenOrders_Long();
    }

    public double getCashAvailable() {
        return this.getMoney() - this.getCashInOpenOrders();
    }

    public Order getOrderByID(long oid) {
        return orders.get(oid);
    }

    public boolean isOrderCovered_Long(Position p, long volume, long price, int leverage) {
        long cashNeeded = p.getRequiredCashForOrder_Long(volume, price, leverage);
        return cashNeeded <= this.getFreeMargin();
    }

    public boolean isOrderCovered_Long(Market market, long volume, long price, int leverage) {
        return isOrderCovered_Long(getPosition(market), volume, price, leverage);
    }

    public boolean isOrderCovered(Market market, double volume, double price, int leverage) {
        return isOrderCovered_Long(getPosition(market),
                (long) (volume * market.getAsset().getDf()),
                (long) (price * currency.getDf()),
                leverage);
    }

    public double getRequiredCashForOrder(Market market, double volume, double price, int leverage) {
        return getPosition(market).getRequiredCashForOrder_Long(
                (long) (volume * market.getAsset().getDf()),
                (long) (price * currency.getDf()),
                leverage
        ) / currency.getDf();
    }

    public double getPerformance(double lastPrice) {

        double total = getEquity();
        double iniTotal = getSnapShotEquity(); //lastPrice * getInitialShares() + getInitialMoney();

        return total / (iniTotal / 100) - 100;

    }

    public final long getEquity_Long() {
        long equity = cash;
        for (Position pos : positions.values()) {
            equity += pos.getEquityValue_Long();
        }
        return equity;
    }

    public final long getEquity_Long(long price) {
        long equity = cash;
        for (Position pos : positions.values()) {
            equity += pos.getEquityValue_Long(price);
        }
        return equity;
    }

    double getCash() {
        return FixedPoint.toExternal(cash);
    }

    public double getEquity() {
        return FixedPoint.toExternal(getEquity_Long());
    }

    public final long getSnapshotEquity_Long() {
        long equity = snap_cash;
        for (Position pos : snap_positions.values()) {
            equity += pos.getEquityValue_Long();
        }
        return equity;
    }

    public double getSnapShotEquity() {
        return FixedPoint.toExternal(getSnapshotEquity_Long());
    }

    public long getFreeMargin_Long() {
        return getEquity_Long() * this.maxMargin/100 - getMarginUsed_Long();
    }

    public double getFreeMargin() {
        return FixedPoint.toExternal(getFreeMargin_Long());
    }

    public final Position getPosition(Market asset) {
        Position p = this.positions.get(asset);
        if (p != null) {
            return p;
        }
        p = new Position(asset, this);

        positions.put(asset, p);
        return p;
    }

    boolean isLiquided = false;

    public boolean isLiquidated() {
        return this.isLiquided;
    }

    void calculateLiquidationStops(long lastPrice) {
        if (isLiquided) {
            return;
        }
        double currentEquity = getEquity();

        double criticalEquity = 200.00; //totalUsedMargin;

        double maxEquityToLose = currentEquity - criticalEquity;

        if (maxEquityToLose <= 0) {

            return;
        }

        double totalAbsoluteVolumeSum = 0;
        for (Position p : positions.values()) {
            // Absolute Volumen (P_aktuell * Shares) zur korrekten Gewichtung des Risikos
            totalAbsoluteVolumeSum += Math.abs(p.getMarketValue());

        }

        //Map<String, Double> stopPrices = new HashMap<>();
        for (Position p : positions.values()) {

            // I. Gewichtung (W_i)
            double positionVolume = Math.abs(p.getMarketValue());
            double weight = (double) positionVolume / (double) totalAbsoluteVolumeSum;

            // II. Tolerierter Verlust für diese Position (L_i) in Cents
            double toleratedLoss_i_long = maxEquityToLose * weight;

            // III. Verlust pro Aktie (V_Aktie) in Euro
            double shares = Math.abs(p.getShares());
            if (shares == 0) {
                continue;
            }

            //long lossPerShare_double = (toleratedLoss_i_long) / shares;
            double lossPerShare_double = toleratedLoss_i_long / shares;
            //long lossPerShare = (long) Math.round(lossPerShare_double);

            // IV. Endgültiger Stop-Kurs (S_check, i)
            long currentPrice = p.market.getLastPrice_Long();
            long stopPrice;

            if (p.isShort()) {
                // Short: Verlust bei steigendem Kurs -> Stop liegt ÜBER dem aktuellen Preis
                stopPrice = currentPrice + FixedPoint.toInternal(lossPerShare_double);
            } else {
                // Long: Verlust bei fallendem Kurs -> Stop liegt UNTER dem aktuellen Preis
                stopPrice = currentPrice - FixedPoint.toInternal(lossPerShare_double);
            }

            // p.stopPrice=(long)(stopPrice*se.money_df);
            p.setStopPrice(stopPrice);
        }

    }

    public Asset getCurrency() {
        return currency;
    }
    
    public long setMaxMargin(long m){
        if (m<=100 && m>=0)
            maxMargin=m;
        return maxMargin;
    }
    
    public long getMaxMargin(){
        return maxMargin;
    }

}
