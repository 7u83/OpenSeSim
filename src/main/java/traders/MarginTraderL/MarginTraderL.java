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
import sesim.Market;
import sesim.Order;
import sesim.Position;
import sesim.Scheduler;
import sesim.Scheduler.Event;
import sesim.Sim;

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

    private class Config {

        // Time before the first execution (in milliseconds)
        long initialDelay = 0;

        // Additional random delay added to initialDelay (in milliseconds)
        long initialDelayRange = 60000*120;

        // Leverage used for trades
        int leverage = 50;

        // Minimum percentage of free margin to use for the next 
        // trade (percent * 10)
        long minFreeMarginUsage = 100;

        // Maximum percentage of free margin to use for the next 
        // trade (percent * 10)
        long maxFreeMarginUsage = 100;

        long minShortLimit = -20;
        long maxShortLimit = +21;
        long minLongDeviation = 1;

        long minLongLimit = -20;
        long maxLongLimit = +21;
        long minShortDeviation = 1;

        long waitForFill = 30000;
        long waitforFillRange = 60000;

        long holdLongPositionTime = 30000;
        long holdLongPositionTimeRange = 60000*120;

        long holdShortPositionTime = 30000;
        long holdShortPositionTimeRange = 60000*120;

        long coolDownTime = 3000;
        long coolDownTimeRange = 60000*10;

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
        
        if (account.isLiquidated()){
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

        setStatus("Open %s- vol:%d", (type == Order.SELL ? "Short" : "Long"),volume);
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
        
        setStatus("Close %s, vol:%d", (type == Order.SELL ? "Long" : "Short"),volume);

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
        waitForFill = sim.getCurrentTimeMillis()+cfg.waitForFill + 
                Sim.random.nextLong(cfg.waitforFillRange + 1);

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

        setStatus("Hold Long");
        long c = cfg.coolDownTime + Sim.random.nextLong(cfg.coolDownTimeRange + 1);
        sim.addEvent(sim.getCurrentTimeMillis() + c, NEXTTRADE);
        setStatus("Cool Down %.1f", c / 1000f);
    }

    @Override
    public void accountUpdated(Account a, Order o) {

        if (currentOrder == null) {
            return;
        }

        if (currentOrder.getStatus() != Order.CLOSED) {
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
        return new JSONObject();
    }

    @Override
    public void setConfig(JSONObject cfg) {

    }

}
