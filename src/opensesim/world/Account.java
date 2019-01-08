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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import opensesim.util.scheduler.Event;
import opensesim.util.scheduler.EventListener;
import org.json.JSONObject;

/**
 * Class to hold account data of traders
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Account {

    HashMap<AbstractAsset, Double> assets = new HashMap<>();
    HashMap<AbstractAsset, Double> assets_bound = new HashMap<>();
    HashMap<AbstractAsset, Double> stop_los = new HashMap<>();

    public double margin_bound = 0.0;

    Trader owner;
    //public Exchange exchange = null;

    private RealWorld world;
    private Exchange exchange;

    public Exchange getExchange() {
        return exchange;
    }

    private boolean unlimited = false;

    public boolean isUnlimied() {
        return unlimited;
    }

    void setUnlimied(boolean unlimied) {
        this.unlimited = unlimied;
    }

    private double leverage = 0.0;

    public double getLeverage() {
        return leverage;
    }

    protected void setLeverage(double leverage) {
        this.leverage = leverage;
    }

    public Map<AbstractAsset, Double> getAssets() {
        return Collections.unmodifiableMap(assets);
    }

    public Map<AbstractAsset, Double> getAssetsAavail() {
        return Collections.unmodifiableMap(assets_bound);
    }

    public Trader getOwner() {
        return owner;
    }

    protected Account(World world) {
        this(world, null, null);
    }

    protected Account(World world, Exchange exchange, JSONObject cfg) {
        this.world = (RealWorld) world;
        if (exchange == null) {
            this.exchange = world.getDefaultExchange();
        }
        if (cfg == null) {
            return;
        }
    }

    public Double getMargin(AbstractAsset currency) {
        /*   Double d = this.getAssetDebt(world.getDefaultExchange(), currency);

        Double f = this.getFinalBalance(currency) * getLeverage() ;
        System.out.printf("Debth %f - Final: %f Return margin %f\n", d,f, f-d);
        
        return f-d;*/
        if (!this.isLeveraged())
            return 0.0;

        return this.getFinalBalance(currency) * getLeverage() + this.getFinalBalance(currency)
                - this.getAssetDebt(world.getDefaultExchange(), currency);
        // + this.get(currency);

    }

    synchronized void add(AssetPack pack) {
        assets.put(pack.asset, get(pack.asset) + pack.volume);
        //  assets_bound.put(pack.asset, getAvail(pack.asset) + pack.volume);
    }

    synchronized void sub(AssetPack pack) {
        assets.put(pack.asset, get(pack.asset) - pack.volume);
        //   assets_bound.put(pack.asset, getAvail(pack.asset) - pack.volume);
    }

    public double get(AbstractAsset asset, boolean bound) {
        return assets.getOrDefault(asset, 0.0)
                + (bound ? this.getBound(asset) : 0.0);
    }

    public double get(AbstractAsset asset) {
        return get(asset, true);
    }

    /*public double getAvail(AbstractAsset asset) {
        if (this.getLeverage() > 0) {
            Double margin = this.getMargin(world.getDefaultCurrency());

            AssetPair ap = world.getAssetPair(asset, world.getDefaultCurrency());

            return margin / world.getDefaultExchange().getAPI(ap).getLastQuote().price;
        }

        return 0.0;

        //return assets_bound.getOrDefault(asset, 0.0);
    }
     */
 /*  public void addAvail(AbstractAsset asset, double val) {
        double avail = getAvail(asset);
      //  assets_bound.put(asset, (avail + val));
    }
     */
    HashSet<EventListener> listeners = new HashSet<>();

    public void addListener(EventListener l) {
        listeners.add(l);
    }

    public void notfiyListeners() {
        Event e = new Event() {
        };
        for (EventListener l : listeners) {
            l.receive(e);
        }
    }

    public Double getFreeMargin(AbstractAsset asset) {

        return 0.0;
    }

    public Double getAssetDebt(Exchange ex, AbstractAsset currency) {
        Double result = 0.0;

        boolean bound = true;

        for (AbstractAsset a : assets.keySet()) {
            if (a.equals(currency)) {
                continue;
            }
            AssetPair pair = world.getAssetPair(a, currency);
            if (pair == null) {
                continue;
            }

            TradingEngine api = (TradingEngine) ex.getAPI(pair);
            Double v = get(a) * api.last_quote.price;
            Double sl = this.calcStopLoss(a);
            Double n = get(a);
            if (n == 0.0) {
                continue;
            }

//            System.out.printf("Asset: %s - %f %f %f\n", a.getSymbol(), n, v, sl * n);
            Double sld = v - sl * n;

            result = result + Math.abs(v); // - sl * n);
            //          System.out.printf("Result is now %f\n", result);

        }
        //    System.out.printf("Return Dresult %f\n", result);
        return result;
    }

    /**
     * Determine final balance of this account, as if all assets would be sold
     * on exchange ex against given currency asset.
     *
     * @param ex Exchange to operate on
     * @param currency Currency against the assets should be sold.
     * @return final balance
     *
     */
    public Double getFinalBalance(Exchange ex, AbstractAsset currency,
            boolean bound) {

        Double result = 0.0; //get(currency);
        for (AbstractAsset a : assets.keySet()) {
            Double v;
            if (a.equals(currency)) {
                v = get(a, bound);
                result += v;
                continue;
            }
            AssetPair pair = world.getAssetPair(a, currency);
            if (pair == null) {
                continue;
            }
            v = get(a, bound);

            if (v == 0.0) {
                continue;
            }

            TradingEngine api = (TradingEngine) ex.getAPI(pair);
            result = result + v * api.last_quote.price;
        }
        return result;
    }

    /**
     * Return the amount of bound assets
     *
     * @param asset Asset to check
     * @return amount
     */
    public Double getBound(AbstractAsset asset) {
        return assets_bound.getOrDefault(asset, 0.0);
    }

    void addBound(AbstractAsset asset, Double vol) {
        assets.put(asset, get(asset, false));
        assets_bound.put(asset, getBound(asset) + vol);
    }

    /**
     * Get the final balance as if all assets would be sold ob the default
     * exchange against given currency.
     * {@link #getFinalBalance(opensesim.world.Exchange, opensesim.world.AbstractAsset)}
     *
     * @param currency Currency for final balance
     * @return final balance
     */
    public Double getFinalBalance(AbstractAsset currency) {
        return getFinalBalance(this.getExchange(), currency, true);
    }

    /**
     * Determine final balance 
     * {@link #getFinalBalance(opensesim.world.Exchange, opensesim.world.AbstractAsset) }
     *
     * @see DoublegetFinalBalance( Exchange ex, AbstractAsset currency)
     * @return Balance
     */
    public Double getFinalBalance() {
        return getFinalBalance(world.getDefaultCurrency());
    }

    /**
     *
     * @param ex
     * @param asset
     * @param currency
     * @return
     */
    public Double calcStopLoss(Exchange ex, AbstractAsset asset, AbstractAsset currency) {
        Double e = (get(currency));
        for (AbstractAsset a : assets.keySet()) {
            if (a.equals(asset)) {
                continue;
            }

            AssetPair pair = world.getAssetPair(a, currency);
            if (pair == null) {
                continue;
            }

            TradingEngine api = (TradingEngine) ex.getAPI(pair);
            Double v = get(a) * api.last_quote.price;
            e = e + v;

        }

        return -(double) e / (double) get(asset);
    }

    public Double calcStopLoss(AbstractAsset asset) {
        return calcStopLoss(world.getDefaultExchange(), asset, world.getDefaultAssetPair().getCurrency());
    }

    /**
     * Return the world this account belongs to
     *
     * @return world
     */
    public World getWorld() {
        return world;
    }

    private boolean isLeveraged() {
        return getLeverage() > 0.0;
    }

    /**
     * Bind asset which will be locked in an order.
     *
     * @param pair
     * @param volume
     * @param limit
     * @return true if asset could be bound, false if assets couldn't be bound
     */
    boolean bind(AssetPair pair, double volume, double limit) {

        // Bind asset and currecy
        this.addBound(pair.getAsset(), volume);
        this.addBound(pair.getCurrency(), -(volume * limit));

        if (this.isUnlimied()) {
            // in case it is an unlimited account we can return 
            // true without further checks
            return true;
        }

        // checks for leveraged account
        if (!this.isLeveraged()) {
            if (limit == 0.0) {
                // an unlimited order is always considered to be
                // covereable. When the trade comes to execution, 
                // the limits will be checked.
                return true;
            }
            if (volume < 0) {
                // It's a limited sell order, we have just to check
                // if a sufficient amount of assets is available 

                if (get(pair.getAsset()) >= 0) {
                    return true;
                }

                // unbind and return false
                this.addBound(pair.getAsset(), -volume);
                this.addBound(pair.getCurrency(), (volume * limit));
                return false;

            }
            // Check if enough money is available to cover the 
            // entiere volume to by
            if (get(pair.getCurrency()) >= 0) {
                return true;
            }
            // unbind and return false
            this.addBound(pair.getAsset(), -volume);
            this.addBound(pair.getCurrency(), (volume * limit));
            return false;

        }

        // we are dealing here with a leveraged account
        Double margin = this.getMargin(pair.getCurrency());
        if (margin >= 0) {
            return true;
        }

        // Unbind asset and currency
        this.addBound(pair.getAsset(), -volume);
        this.addBound(pair.getCurrency(), (volume * limit));
        return false;

    }

}
