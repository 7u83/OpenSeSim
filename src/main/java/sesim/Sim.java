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

import gui.Globals;
import gui.NewStrategyDialog;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.SplittableRandom;
import org.json.JSONObject;
import org.json.JSONArray;
import sesim.Scheduler.Event;

/**
 *
 * @author 7u83
 */
public class Sim {

    private HashMap<String, Asset> assets = new HashMap<>();
    private HashMap<Asset, HashMap<Asset, Market>> currencies = new HashMap();

    /**
     * Keys in cconfig file used by Sim
     */
    public static final class CfgKeys {

        public static final String SESIMVERSION = "version";
        public static final String STRATEGIES = "strategies";
        public static final String TRADERS = "traders";
        public static final String EXCHANGE = "exchange";
        public static final String RANDOM = "random";

    }

    public static int randNextInt() {
        return random.nextInt();

    }

    public static int randNextInt(int bounds) {

        return random.nextInt(bounds);

    }

    public static double randNextDouble() {
        return random.nextDouble();

    }

    private Market defaultMarket;

    public Market getDefaultMarket() {
        return defaultMarket;
    }

    /*   static class TempAsset implements Asset {

        private final String symbol;
        private final Market se;

        TempAsset(String sym, Market se) {
            symbol = sym;
            this.se = se;

        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public Market getMarket() {
            return se;
        }

        @Override
        public float getDf() {
            return se.getDf();
        }

        @Override
        public DecimalFormat getFormatter() {
            return null;
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public int getDecimals() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

    }*/
 /*    public Asset getAsset(String symbol) {
        Asset a = assets.get(symbol);
        if (a == null) {
            a = new TempAsset(symbol, se);
            assets.put(symbol, a);
        }
        return a;
    }*/
    public AutoTraderLoader tloader;

    Scheduler scheduler;

    public void setAcceleration(double a) {
        scheduler.setAcceleration(a);
    }

    public void setPause(boolean p) {
        scheduler.setPause(p);
    }

    public boolean getPause() {
        return scheduler.getPause();
    }

    public long getCurrentTimeMillis() {
        return scheduler.getCurrentTimeMillis();
    }

    public void startScheduler() {
        scheduler.start();
    }

    public void stop() {
        Logger.info("Sim stopped");
        scheduler.terminate();
    }

    public void addEvent(long t, Event e) {
        scheduler.addEvent(t, e);
    }

    public boolean delEvent(long t, Event e) {
        return scheduler.delEvent(t, e);
    }

    Asset defaultAsset = new AssetBase("RBTN", "VEB Robotron", 0);

    public Sim() {
        scheduler = new Scheduler();
        defaultMarket = new Market(this, defaultCurrency, defaultAsset, new JSONObject());
        initAutoTraderLoader();
        reset();
    }

    AssetBase defaultCurrency = new AssetBase("TLR", "Taler", 2);

    public ArrayList<AutoTrader> traders = null;

    public final void reset() {
        if (traders != null) {
            for (AutoTrader t : traders) {
                t.stop();
            }
        }
        traders = new ArrayList();
        scheduler = new Scheduler();
        
        
        defaultMarket.reset();
    }

    /**
     *
     * @param se
     * @param id
     * @param name
     * @param money
     * @param shares
     * @param cfg
     * @return
     */
    private AutoTrader createTraderNew(Market se, long id, String name, float money, float shares, String strat, JSONObject cfg) {

        String base = cfg.getString("base");
        AutoTrader ac = tloader.getStrategyBase(base);
        if (ac == null) {
            return null;
        }
        ac.setConfig(cfg);
        ac.init(this, id, name, money, shares, strat, cfg);

        return ac;
    }

    void resetAutoTraders() {
        ArrayList<String> names = tloader.getDefaultStrategyNames();
        for (String name : names) {
            AutoTrader ac = tloader.getStrategyBase(name);
            ac.reset();
        }
    }

    private void initAutoTraderLoader() {
        ArrayList pathlist = new ArrayList<>();
        String dp = new java.io.File(sesim.Sim.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()).toString();

        pathlist.add(dp);
        tloader = new AutoTraderLoader(pathlist);
    }

    public static String DEFAULT_EXCHANGE_CFG
            = "{"
            + "  money_decimals: 2,"
            + "  shares_decimals: 0"
            + "}";

/*    public static JSONObject getExchangeCfg(JSONObject cfg) {
        JSONObject exchange = cfg.optJSONObject(CfgKeys.EXCHANGE);
        if (exchange == null) {
            exchange = new JSONObject(DEFAULT_EXCHANGE_CFG);
        }
        return exchange;
        //return cfg.getJSONObject(CfgKeys.EXCHANGE);
    }*/

    static public final void putExchangeCfg(JSONObject sobj, JSONObject exchange) {
        sobj.put(CfgKeys.EXCHANGE, exchange);
    }

    public static double calculateInitialPrice(JSONArray tlist) {

        double cash = 0;
        double shares = 0;
        for (int i = 0; i < tlist.length(); i++) {

            JSONObject trader = tlist.getJSONObject(i);
            if (!trader.optBoolean("Enabled", false)) {
                continue;
            }
            if (trader.optBoolean("ExcludeInitial", false)) {
                continue;
            }
            long count = trader.optLong("Count", 0);
            cash += trader.optDouble("Cash", 0.0) * count;
            shares += trader.optDouble("Shares", 0.0) * count;

        }
        return shares == 0 ? 100.0 : cash / shares;
    }

    public static SplittableRandom random = new SplittableRandom(12);

    private void startRandomGenerator(JSONObject cfg) {
        long randomSeed = Config.getRandomSeed(cfg);
        boolean useSeed = Config.getUseRandomSeed(cfg);
        if (useSeed) {
            random = new SplittableRandom(randomSeed);
        } else {
            random = new SplittableRandom();
            randomSeed = random.nextLong(Long.MAX_VALUE);
            random = new SplittableRandom(randomSeed);
        }

        Logger.info("Random seed is %d", randomSeed);
    }

    private void initAssets(JSONObject cfg) {
        assets = new HashMap<>();
        JSONObject jassets = Config.getAssets(cfg);
        for (String symbol : jassets.keySet()) {
            JSONObject jasset = jassets.optJSONObject(symbol);
            AssetBase a = new AssetBase(
                    symbol,
                    jasset.optString("name", symbol),
                    jasset.optInt("decimals", 0)
            );
            assets.put(symbol, a);
        }
    }

    private void initMarkets(JSONObject cfg) {
        JSONObject jcurrencies = Config.getMarkets(cfg);

        String defaultCurrencySymbol = Config.getDefaultCurrency(cfg);
        String defaultAssetSymbol = Config.getDefaultAsset(cfg);

        for (String currencySymbol : jcurrencies.keySet()) {
            JSONObject jmarkets = jcurrencies.optJSONObject(currencySymbol);

            HashMap<Asset, Market> markets = new HashMap<>();
            Asset currency = assets.get(currencySymbol);

            for (String assetSymbol : jmarkets.keySet()) {
                Asset asset = assets.get(assetSymbol);
                Market market = new Market(this, currency, asset, jmarkets.optJSONObject(assetSymbol));
                
                
                
                markets.put(asset, market);

                if (assetSymbol.equals(defaultAssetSymbol)
                        && currencySymbol.equals(defaultCurrencySymbol)) {
                    
                    this.defaultMarket=market;
                }

                System.out.printf("Pair: %s/%s\n", currencySymbol, assetSymbol);
            }
            this.currencies.put(currency, markets);
        }

    }

    public void startTraders(JSONObject cfg) {

        Logger.info("Sim started");

        Order.resetIdGenerator();
        startRandomGenerator(cfg);

        this.initAssets(cfg);
        this.initMarkets(cfg);

   //     defaultMarket.putConfig(getExchangeCfg(cfg));

        resetAutoTraders();

        JSONArray tlist = Config.getTraders(cfg);

        boolean autoInitialPrice = defaultMarket.autoInitialPrice;
        double initialPrice;
        if (autoInitialPrice) {
            initialPrice = Sim.calculateInitialPrice(tlist);
        } else {
            initialPrice = (float) (defaultMarket.initalPrice);
        }

        Logger.info("Initial prices is: %f", initialPrice);
        this.defaultMarket.setFairValue((float) initialPrice);

        defaultMarket.initLastQuote();

        Float moneyTotal = 0.0f;
        Float sharesTotal = 0.0f;
        long id = 0;
        for (int i = 0; i < tlist.length(); i++) {
            JSONObject t = tlist.getJSONObject(i);
            String strategy_name = t.optString("Strategy", null);
            if (strategy_name == null) {
                continue;
            }
            JSONObject strategyCfg = Config.getStrategy(cfg, strategy_name);

            // String base = strategy.getString("base");
            //    AutoTrader ac = Globals.tloader.getStrategyBase(base);
            //     System.out.printf("Load Strat: %s\n", strategy_name);
            //      System.out.printf("Base %s\n", base);
            Integer count = t.getInt("Count");
            Float shares = (float) t.optDouble("Shares", 0);
            Float money = (float) t.optDouble("Cash", 0);

            Boolean enabled = t.optBoolean("Enabled", false);
            if (!enabled) {
                continue;
            }

            if (strategyCfg == null) {
                sesim.Logger.error("Strategy '%s' does't exists, will not start '%s'", strategy_name, t.getString("Name"));
                continue;
            }

            Object global = null;

            for (int i1 = 0; i1 < count; i1++) {
                AutoTrader trader;

                String base = strategyCfg.getString("base");
                trader = tloader.getStrategyBase(base);

                if (trader == null) {
                    continue;
                }

                global = trader.initGlobal(this, global, strategyCfg);
                trader.setConfig(strategyCfg);

                //trader.init(this, id, t.getString("Name") + "-" + i1, money, shares, strategy_name, strategyCfg);
                trader.init(this, id, t.getString("Name") + "-" + i1, money + (float) (initialPrice * shares), 0, strategy_name, strategyCfg);
                trader.getAccount().getPosition(defaultMarket).addShares(
                        (long) (shares * defaultMarket.shares_df),
                        (long) (initialPrice * defaultCurrency.getDf()),
                        1);
                trader.getAccount().makeSnapShot();

                if (trader == null) {
                    base = strategyCfg.getString("base");
                    sesim.Logger.error("Could not load base '%s', not starting %s", base, t.getString("Name"));
                    break;
                }

                ((AutoTraderBase) trader).setStrategyName(strategy_name);

                this.traders.add(trader);
                ((AutoTraderBase) trader).id = traders.size() - 1;

                Boolean exclude = t.optBoolean("ExcludeInitial", false);
                if (!exclude) {
                    moneyTotal += money;
                    sharesTotal += shares;
                }

                JSONArray color = t.optJSONArray("Color");
                if (color != null && color.length() == 3) {
                    int c[] = new int[3];
                    c[0] = color.getInt(0);
                    c[1] = color.getInt(1);
                    c[2] = color.getInt(2);

                    ((AutoTraderBase) trader).color = c;
                }

                if (this.traders.size() % 10000 == 0) {
                    sesim.Logger.info("Traders %d", traders.size());
                }

            }

        }

        //  Globals.sim.market.fairValue = 1.0;
        //System.out.printf("Failr Value is %f\n", market.fairValue);
        for (int i = 0; i < traders.size(); i++) {
            traders.get(i).start();
            if (i % 10000 == 0) {
                sesim.Logger.info("Traders start %d", i);
            }
        }

    }

    public Scheduler getScheduler() {
        return scheduler;
    }

}
