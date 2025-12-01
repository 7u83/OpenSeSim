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
package sesim;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.json.JSONObject;
import sesim.Scheduler.EventProcessor;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AutoTraderBase implements AutoTraderInterface, EventProcessor {

    protected Account account;
    protected Market market;
    protected Sim sim;
    // protected AutoTraderConfig config;

    protected String name;
    private String strategyName = "default";

    int[] color = null;

    /*    public AutoTraderBase(Exchange market, long id, String name, float money, float shares, AutoTraderConfig config) {
        account_id = market.createAccount(money, shares);
        Exchange.Account a = market.getAccount(account_id);

        //   a.owner=this;
        this.market = market;
        this.config = config;
        this.name = name;
        this.id = id;

    }
     */
    @Override
    public AutoTraderGui getGui() {
        return null;
    }

    public AutoTraderBase() {
        market = null;
        id = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int[] getColor() {
        return color;
    }


    @Override
    public long getID() {
        return id;
    }
    long id;

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void init(Sim sim, long id, String name, float money, float shares, String strat, JSONObject cfg) {
        this.account = new Account(sim.getExchange(), money, shares); // market.createAccount(money, shares);
        //       market.getAccount(account_id).owner = this;

        this.sim = sim;
        this.market = sim.getExchange();
        this.account.owner = this;
//        this.market = market;
        this.name = name;
        this.id = id;
        this.strategyName = strat;

    }

    public Market getSE() {
        return market;
    }

    @Override
    public abstract void start();

    String status = "";

    protected void setStatus(String format, Object... arguments) {

        status = String.format(format, arguments);
        //  System.out.printf("%s: %s\n", this.getName(), status);
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public JDialog getGuiConsole(Frame parent) {
        return null;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String s) {
        strategyName = s;
    }

    @Override
    public void reset() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Object initGlobal(Sim sim, Object global, JSONObject cfg) {
        return null;
    }

    @Override
    public boolean getDevelStatus() {
        return true;
    }

    /**
     * Generates a random price delta (change) based on the given last price and
     * deviation parameters.
     * <p>
     * The returned value can be added to the current price to obtain a new
     * price within the specified minimum and maximum deviation range. The
     * deviations are relative to the last price, expressed in ‰ (per mille). An
     * absolute minimum deviation ensures that rounding or small percentages do
     * not produce a zero delta.
     * </p>
     *
     * @param value the current price (in smallest currency unit, e.g.,
     * cents)
     * @param minDeviation the minimum relative deviation (per mille, can be
     * negative)
     * @param maxDeviation the maximum relative deviation (per mille)
     * @param minAbsoluteDeviation the minimum absolute deviation to enforce (in
     * the same units as lastPrice)
     * @return a randomly generated delta that can be added to lastPrice to get
     * a new price
     */
    static public long getRandomDelta_Long(long value,
            long minDeviation, long maxDeviation, long minAbsoluteDeviation) {

        // Calculate minimum and maximum delta based on relative deviations 
        // (per mille)
        long product;
        product = value * minDeviation;
       long minDelta = (product >= 0)
                ? (product + 5000) / 10000
                : (product - 5000) / 10000;
        //long minDelta = (value * minDeviation) / 10000;
        
        
        product = value * maxDeviation;
        long maxDelta = (product >= 0)
                ? (product + 5000) / 10000
                : (product - 5000) / 10000;
        //long maxDelta = (value * maxDeviation) / 10000;

        // Ensure minimum delta is at least the absolute minimum
        if (Math.abs(minDelta) < minAbsoluteDeviation && minDeviation != 0) {
            minDelta = (minDeviation < 0) ? -minAbsoluteDeviation : minAbsoluteDeviation;
        }
        if (Math.abs(maxDelta) < minAbsoluteDeviation && maxDeviation != 0) {
            maxDelta = (maxDeviation < 0) ? -minAbsoluteDeviation : minAbsoluteDeviation;
        }

        // Prevent negative price
        if (minDelta + value < 0) {
            minDelta = -value;
        }

        // Calculate range of possible deltas
        long range = maxDelta - minDelta + 1;

        // Generate random delta within the range
        long delta = Sim.random.nextLong(range) + minDelta;

        return delta;

    }

    /**
     * Generates a new random price based on the given last price and deviation
     * parameters.
     * <p>
     * This function internally computes a random price delta using
     * {@link #getRandomDelta_Long(long, long, long, long)} and adds it to
     * the last price. The resulting price is guaranteed to be at least 1
     * (cannot go below 1 unit).
     * </p>
     *
     * @param lastPrice the current price (in smallest currency unit, e.g.,
     * cents)
     * @param minDeviation the minimum relative deviation (per mille, can be
     * negative)
     * @param maxDeviation the maximum relative deviation (per mille)
     * @param minAbsDeviation the minimum absolute deviation to enforce (in the
     * same units as lastPrice)
     * @return a new price obtained by adding a random delta to lastPrice, never
     * less than 1
     */
    public static long getRandomPrice_Long(long lastPrice,
            long minDeviation, long maxDeviation, long minAbsDeviation) {

        // Compute random delta for the last price
        long delta = getRandomDelta_Long(lastPrice,
                minDeviation, maxDeviation, minAbsDeviation);

        // Apply delta to last price
        long newPrice = lastPrice + delta;

        // Ensure new price is at least 1
        if (newPrice < 1) {
            newPrice = 1;
        }

        return newPrice;
    }

}
