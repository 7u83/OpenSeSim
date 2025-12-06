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
package sesim;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * * @author 7u83 Config helpers
 */
public class Config {

    public static long getRandomSeed(JSONObject cfg) {
        JSONObject rand = getRandomCfg(cfg);
        return rand.optLong("seed", 0);
    }

    static public JSONObject getStrategies(JSONObject cfg) {
        return cfg.optJSONObject("strategies");
    }

    static public void putStrategies(JSONObject sobj, JSONObject strategies) {
        sobj.put("strategies", strategies);
    }

    static public JSONObject getStrategy(JSONObject cfg, String name) {
        return getStrategies(cfg).optJSONObject(name);
    }

    public static final String DEFAULT_RANDOM_CFG
            = "{"
            + "seed: 0,"
            + "use_seed: false"
            + "}";

    public static JSONObject getRandomCfg(JSONObject cfg) {
        JSONObject rand = cfg.optJSONObject("random");
        if (rand != null) {
            return rand;
        }
        return new JSONObject(DEFAULT_RANDOM_CFG);
    }

    public static boolean getUseRandomSeed(JSONObject cfg) {
        JSONObject rand = getRandomCfg(cfg);
        return rand.optBoolean("use_seed", false);
    }

    static public JSONArray getTraders(JSONObject cfg) {
        JSONArray traders = cfg.getJSONArray("traders");
        return traders;
    }

    static public final void putTraders(JSONObject cfg, JSONArray traders) {
        cfg.put("traders", traders);
    }

    public static JSONObject getAssets(JSONObject cfg) {
        JSONObject assets = cfg.optJSONObject("assets");
        if (assets == null) {
            assets = new JSONObject();
        }
        return assets;
    }

    public static JSONObject getAsset(JSONObject cfg, String symbol) {
        JSONObject assets = getAssets(cfg);
        return assets.optJSONObject(symbol);
    }

    public static JSONObject getMarkets(JSONObject cfg) {
        JSONObject currencies = cfg.optJSONObject("markets");
        if (currencies == null) {
            currencies = new JSONObject();
        }
        return currencies;
    }

    public static JSONObject getDefaultMarket(JSONObject cfg) {
        return cfg.optJSONObject("default_market");
    }

    public static String getDefaultCurrency(JSONObject cfg) {
        JSONObject dm = getDefaultMarket(cfg);
        if (dm == null) {
            return null;
        }
        return dm.optString("currency");
    }

    public static String getDefaultAsset(JSONObject cfg) {
        JSONObject dm = getDefaultMarket(cfg);
        if (dm == null) {
            return null;
        }
        return dm.optString("asset");
    }

    public static void putDefaultCurrency(JSONObject cfg, String currency) {
        JSONObject dm = getDefaultMarket(cfg);
        if (dm == null) {
            dm = new JSONObject();
        }
        dm.put("currency", currency);
        cfg.put("default_market", dm);
    }

    public static void putDefaultAsset(JSONObject cfg, String asset) {
        JSONObject dm = getDefaultMarket(cfg);
        if (dm == null) {
            dm = new JSONObject();
        }
        dm.put("asset", asset);
        cfg.put("default_market", dm);
    }

}
