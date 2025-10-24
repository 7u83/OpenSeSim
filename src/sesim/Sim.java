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

import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author 7u83
 */
public class Sim {

    public static final class CfgKeys {

        public static final String SESIMVERSION = "version";
        public static final String STRATEGIES = "strategies";
        public static final String TRADERS = "traders";
        public static final String EXCHANGE = "exchange";

    }

    static SplittableRandom random = new SplittableRandom(12);

    public static int randNextInt() {
        return random.nextInt();

    }

    public static int randNextInt(int bounds) {

        return random.nextInt(bounds);

    }

    public static double randNextDouble() {
        return random.nextDouble();

    }
    
    
    

    public Exchange se;
    public AutoTraderLoader tloader;

    public Sim() {
        se = new Exchange();
        initAutoTraderLoader();
        reset();
    }

    public ArrayList<AutoTraderInterface> traders;

    public final void reset() {
        traders = new ArrayList();
        se.reset();
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
    public AutoTraderInterface createTraderNew(Exchange se, long id, String name, float money, float shares, JSONObject cfg) {

        String base = cfg.getString("base");
        AutoTraderInterface ac = tloader.getStrategyBase(base);
        if (ac == null) {
            return null;
        }
        ac.setConfig(cfg);
        ac.init(se, id, name, money, shares, cfg);

        return ac;
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

    static public final JSONObject getStrategies(JSONObject cfg) {
        return cfg.getJSONObject(CfgKeys.STRATEGIES);
    }

    static public final void putStrategies(JSONObject sobj, JSONObject strategies) {
        sobj.put(CfgKeys.STRATEGIES, strategies);
    }

    static public final JSONArray getTraders(JSONObject cfg) {
        JSONArray traders = cfg.getJSONArray(CfgKeys.TRADERS);
        return traders;
    }

    static public final void putTraders(JSONObject cfg, JSONArray traders) {
        cfg.put(CfgKeys.TRADERS, traders);
    }

    static public JSONObject getStrategy(JSONObject cfg, String name) {
        return getStrategies(cfg).getJSONObject(name);
    }
    public static String DEFAULT_EXCHANGE_CFG
            = "{"
            + "  money_decimals: 2,"
            + "  shares_decimals: 0"
            + "}";

    public static JSONObject getExchangeCfg(JSONObject cfg) {
        JSONObject exchange = cfg.optJSONObject(CfgKeys.EXCHANGE);
        if (exchange == null) {
            exchange = new JSONObject(DEFAULT_EXCHANGE_CFG);
        }
        return exchange;
        //return cfg.getJSONObject(CfgKeys.EXCHANGE);
    }

    static public final void putExchangeCfg(JSONObject sobj, JSONObject exchange) {
        sobj.put(CfgKeys.EXCHANGE, exchange);
    }
    
    


    public void startTraders(JSONObject cfg) {

        
        se.putConfig(getExchangeCfg(cfg));
        
        //   Globals.sim.se.setMoneyDecimals(8);
        //    Globals.sim.se.setSharesDecimals(0);        
        JSONArray tlist = Sim.getTraders(cfg);

        Float moneyTotal = 0.0f;
        Float sharesTotal = 0.0f;
        long id = 0;
        for (int i = 0; i < tlist.length(); i++) {
            JSONObject t = tlist.getJSONObject(i);
            String strategy_name = t.getString("Strategy");
            JSONObject strategy = getStrategy(cfg, strategy_name);
            String base = strategy.getString("base");
            //    AutoTraderInterface ac = Globals.tloader.getStrategyBase(base);

            //     System.out.printf("Load Strat: %s\n", strategy_name);
            //      System.out.printf("Base %s\n", base);
            Integer count = t.getInt("Count");
            Float shares = (float)t.getDouble("Shares");
            Float money = (float)t.getDouble("Cash");

            Boolean enabled = t.optBoolean("Enabled",false);
            if (!enabled) {
                continue;
            }

            //      System.out.printf("Count: %d Shares: %f Money %f\n", count, shares, money);
            for (int i1 = 0; i1 < count; i1++) {
                AutoTraderInterface trader;

                trader = this.createTraderNew(this.se, id, t.getString("Name") + "-" + i1, money, shares, strategy);

                this.traders.add(trader);

                moneyTotal += money;
                sharesTotal += shares;

            }

        }
        
        
        float initialPrice=(float)(Sim.getExchangeCfg(cfg).optDouble(se.CFG_INITIAL_PRICE,100.0f));
        boolean autoInitialPrice=Sim.getExchangeCfg(cfg).optBoolean(se.CFG_AUTO_INITIAL_PRICE,true);
        
        System.out.printf("Cache total %f, Shares total\n", moneyTotal,sharesTotal);

        if (autoInitialPrice){
            this.se.fairValue = moneyTotal / sharesTotal;
        }
        else{
            this.se.fairValue = initialPrice;
        }
        
        
        se.initLastQuote();

        //  Globals.sim.se.fairValue = 1.0;
        System.out.printf("Failr Value is %f\n", se.fairValue);

        for (int i = 0; i < traders.size(); i++) {
            traders.get(i).start();
        }

    }

}
