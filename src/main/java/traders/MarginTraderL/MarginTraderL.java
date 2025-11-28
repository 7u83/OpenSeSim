/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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
package traders.MarginTraderL;

import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Market;
import sesim.Order;
import sesim.Scheduler.Event;
import sesim.Sim;
import static sesim.util.Math.toFixedLong;

/**
 *
 * @author tube
 */
public class MarginTraderL extends AutoTraderBase
        implements Market.AccountListener {

    private final Event NEXTTRADE = new Event(this);
    private final Event FILLTIMEOUT = new Event(this);

    Order currentOrder = null;
    long waitForFill = 0;
    private boolean isOpening = false;

    class Config {

        // Time before the first execution (in milliseconds)
        long initialDelay = 0;

        // Additional random delay added to initialDelay (in milliseconds)
        long initialDelayRange = 60000;

        // Leverage used for trades
        int leverage = 50;

        // Minimum percentage of free margin to use for the next 
        // trade (percent * 10)
        long minFreeMarginUsage = 500;

        // Maximum percentage of free margin to use for the next 
        // trade (percent * 10)
        long maxFreeMarginUsage = 500;

        long minShortLimit = -200;
        long maxShortLimit = +200;
        long minLongDeviation = 1;

        long minLongLimit = -200;
        long maxLongLimit = +200;
        long minShortDeviation = 1;

        long waitForFill = 60000;
        long waitForFillRange = 60000;

        long holdLongPositionTime = 30000;
        long holdLongPositionTimeRange = 60000 * 2;

        long holdShortPositionTime = 30000;
        long holdShortPositionTimeRange = 60000 * 2;

        long coolDownTime = 3000;
        long coolDownTimeRange = 60000;

    }
    Config cfg = new Config();

    @Override
    public void start() {
        //  sesim.Logger.info("Starting Margin Trader L");
        account.setListener(this);
        long initialDelay = Sim.random.nextLong(cfg.initialDelay + 1)
                + Sim.random.nextLong(cfg.initialDelayRange + 1);
        setStatus("Delay: %.1f", initialDelay / 1000.0f);
        sim.addEvent(sim.getCurrentTimeMillis() + initialDelay, NEXTTRADE);
    }

    @Override
    public void processEvent(long time, Event e) {

        if (account.isLiquidated()) {
            setStatus("Ruined/Liquidated");
            return;
        }

        if (e == NEXTTRADE) {
            //  sesim.Logger.info("Margin Trader Starting trade");
            nextTrade();
        }
        if (e == FILLTIMEOUT) {
            fillTimeout();
        }
    }

    private void openTrade() {

        // Determine the margin to use
        long freeMarginUsage = Sim.random.nextLong(
                cfg.minFreeMarginUsage, cfg.maxFreeMarginUsage + 1);
        long margin = account.getFreeMargin_Long() * freeMarginUsage / 1000;

        // Determine whether long or short and limit price
        byte type;
        long limit;
        //   long price = account.getDefaultMarket().getBestPrice_0().getPrice_Long();
        long price = account.getDefaultMarket().getLastPrice_Long(); //.getPrice_Long();

        if (Sim.random.nextBoolean()) {
            type = Order.BUY;
            limit = getRandomPrice_Long(price,
                    cfg.minLongLimit, cfg.maxLongLimit, cfg.minLongDeviation);
        } else {
            type = Order.SELL;
            limit = getRandomPrice_Long(price,
                    cfg.minShortLimit, cfg.maxShortLimit, cfg.minShortDeviation);
        }
        long volume = margin * cfg.leverage / limit;

        submitTrade(type, volume, limit);

        setStatus("Open %s- vol:%d", (type == Order.SELL ? "Short" : "Long"), volume);
        isOpening = true;

        //   sesim.Logger.info("My first trade uses %d margin and %d shares with %d limit\n", margin, volume, limit);
        //   sesim.Logger.info("ORDER=%s", currentOrder.toString());
    }

    void closeTrade(long volume) {
        byte type;
        long limit;
        long price = account.getDefaultMarket().getBestPrice_0().getPrice_Long();
        if (volume < 0) {
            type = Order.BUY;
            limit = getRandomPrice_Long(price,
                    cfg.minLongLimit, cfg.maxLongLimit, cfg.minLongDeviation);
        } else {
            type = Order.SELL;
            limit = getRandomPrice_Long(price,
                    cfg.minShortLimit, cfg.maxShortLimit, cfg.minShortDeviation);
        }

        isOpening = false;
        submitTrade(type, Math.abs(volume), limit);

        setStatus("Close %s, vol:%d", (type == Order.SELL ? "Long" : "Short"), volume);

    }

    void nextTrade() {
        long shares = account.getPosition(account.getDefaultMarket()).getShares_Long();
        if (shares == 0) {
            openTrade();
        } else {
            closeTrade(shares);
        }

    }

    void submitTrade(byte type, long volume, long limit) {
        // Calulate time to wait for fill
        waitForFill = sim.getCurrentTimeMillis() + cfg.waitForFill
                + Sim.random.nextLong(cfg.waitForFillRange + 1);

        sim.addEvent(waitForFill, FILLTIMEOUT);

        // Calculate volume and create order
        currentOrder = account.getDefaultMarket().createOrder_Long(account,
                (byte) (type | Order.LIMIT), volume, limit, 0, cfg.leverage);

    }

    void fillTimeout() {
        if (currentOrder != null) {
            currentOrder.getMarket().cancelOrder(account, currentOrder.id);
            currentOrder = null;
        }
        hold();

    }

    void hold() {
        long shares = account.getPosition(account.getDefaultMarket()).getShares_Long();
        if (isOpening) {
            // We have got no shares when opening position
            if (shares == 0) {
                long c = cfg.coolDownTime + Sim.random.nextLong(cfg.coolDownTimeRange + 1);
                sim.addEvent(sim.getCurrentTimeMillis() + c, NEXTTRADE);
                setStatus("Cool Down %.1f", c / 1000f);
                return;
            }
            if (shares < 0) {
                long c = cfg.holdShortPositionTime + Sim.random.nextLong(cfg.holdShortPositionTimeRange + 1);
                sim.addEvent(sim.getCurrentTimeMillis() + c, NEXTTRADE);
                setStatus("Hold Short %.1f", c / 1000f);
                return;
            }
            long c = cfg.holdLongPositionTime + Sim.random.nextLong(cfg.holdLongPositionTimeRange + 1);
            sim.addEvent(sim.getCurrentTimeMillis() + c, NEXTTRADE);
            setStatus("Hold Long %.1f", c / 1000f);
            return;
        }

        long c = cfg.coolDownTime + Sim.random.nextLong(cfg.coolDownTimeRange + 1);
        sim.addEvent(sim.getCurrentTimeMillis() + c, NEXTTRADE);
        setStatus("Cool Down %.1f", c / 1000f);
    }

    @Override
    public void accountUpdated(Account a, Order o) {

        if (o.getStatus() != Order.CLOSED) {
            return;
        }
        currentOrder = null;
        sim.delEvent(waitForFill, FILLTIMEOUT);
        hold();
    }

    @Override
    public String getDisplayName() {
        return "MarginTrader L";
    }

    @Override
    public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();
        cfg.put("initial_delay", (double) (this.cfg.initialDelay) / 1000.0);
        cfg.put("initial_delay_range", (double) (this.cfg.initialDelayRange) / 1000.0);
        cfg.put("min_short_limit", (double) (this.cfg.minShortLimit) / 100.0);
        cfg.put("max_short_limit", (double) (this.cfg.maxShortLimit) / 100.0);
        cfg.put("min_long_limit", (double) (this.cfg.minLongLimit) / 100.0);
        cfg.put("max_long_limit", (double) (this.cfg.maxLongLimit) / 100.0);
        cfg.put("wait_for_fill", (double) (this.cfg.waitForFill) / 1000.0);
        cfg.put("wait_for_fill_range", (double) (this.cfg.waitForFillRange) / 1000.0);

        cfg.put("hold_short_position_time", (double) (this.cfg.holdShortPositionTime)/1000.0);
        cfg.put("hold_short_position_time_range", (double) (this.cfg.holdShortPositionTimeRange)/1000.0);
        cfg.put("hold_long_position_time", (double) (this.cfg.holdLongPositionTime)/1000.0);
        cfg.put("hold_long_position_time_range", (double) (this.cfg.holdLongPositionTimeRange)/1000.0);
        cfg.put("cool_down_time", (double) (this.cfg.coolDownTime)/1000.0);
        cfg.put("cool_down_time_range", (double) (this.cfg.coolDownTimeRange)/1000.0);

        cfg.put("leverage", this.cfg.leverage);
        return cfg;
    }

    @Override
    public void setConfig(JSONObject cfg) {
        this.cfg.initialDelay = (long) (1000 * cfg.optDouble("initial_delay", 0.0));
        this.cfg.initialDelayRange = (long) (1000 * cfg.optDouble("initial_delay_range", 10.0));
        this.cfg.minShortLimit = toFixedLong(cfg.optDouble("min_short_limit", -2.0), 100);
        this.cfg.maxShortLimit = toFixedLong(cfg.optDouble("max_short_limit", 2.0), 100);
        this.cfg.minLongLimit = toFixedLong(cfg.optDouble("min_long_limit", -2.0), 100);
        this.cfg.maxLongLimit = toFixedLong(cfg.optDouble("max_long_limit", 2.0), 100);
        this.cfg.waitForFill = toFixedLong(cfg.optDouble("wait_for_fill", 10), 1000);
        this.cfg.waitForFillRange = toFixedLong(cfg.optDouble("wait_for_fill_range", 10), 1000);

        this.cfg.holdShortPositionTime = toFixedLong(cfg.optDouble("hold_short_position_time", 60), 1000);
        this.cfg.holdShortPositionTimeRange = toFixedLong(cfg.optDouble("hold_short_position_time_range", 120), 1000);
        this.cfg.holdLongPositionTime = toFixedLong(cfg.optDouble("hold_long_position_time", 60), 1000);
        this.cfg.holdLongPositionTimeRange = toFixedLong(cfg.optDouble("hold_long_position_time_range", 120), 1000);
        this.cfg.coolDownTime = toFixedLong(cfg.optDouble("cool_down_time", 30), 1000);
        this.cfg.coolDownTimeRange = toFixedLong(cfg.optDouble("cool_down_time_range", 60), 1000);

        this.cfg.leverage = cfg.optInt("leverage", 50);

    }

    @Override
    public AutoTraderGui getGui() {
        return new MarginTraderGui(cfg);
    }

}
