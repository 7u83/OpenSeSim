/*
 * Copyright (c) 2017, 7u83
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
package traders;

import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.AutoTrader;
import sesim.AutoTraderConfig;
import sesim.AutoTraderGui;
import sesim.Exchange;

/**
 *
 * @author 7u83
 */
public class RandomTraderConfig implements AutoTraderConfig {

    public Float[] sell_volume = {77f, 100f};
    public Float[] sell_limit = {-5.3f, 5f};
    public Integer[] sell_wait = {10000, 50000};
    public Integer[] wait_after_sell = {10, 30};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-5f, 5f};
    public Integer[] buy_wait = {10000, 50000};
    public Integer[] wait_after_buy = {10, 30};

    @Override
    public AutoTrader createTrader(Exchange se, JSONObject cfg, double money, double shares) {
        if (cfg != null) {
            this.putConfig(cfg);
        }
        return new traders.RandomTrader(se, money, shares, this);
    }

    @Override
    public String getName() {
        return "RandomA";
    }

    @Override
    public AutoTraderGui getGui() {
        return new RandomTraderGui(this);
    }
    
    final String SELL_VOLUME = "sell_volume";
    final String BUY_VOLUME = "buy_volume";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_WAIT = "sell_wait";
    final String BUY_WAIT = "wait_wait";
    
    @Override
    public JSONObject getConfig() {
        JSONObject jo = new JSONObject();
        jo.put(SELL_VOLUME, sell_volume);
        jo.put(BUY_VOLUME, buy_volume);
        jo.put(SELL_LIMIT, sell_limit);
        jo.put(BUY_LIMIT, buy_limit);
        jo.put(SELL_WAIT, sell_wait);
        jo.put(BUY_WAIT, buy_wait);
        return jo;
    }

    public void putConfig(JSONObject cfg) {
        sell_volume = (Float[]) cfg.get(SELL_VOLUME);
        buy_volume = (Float[]) cfg.get(BUY_VOLUME);
        sell_limit = (Float[]) cfg.get(SELL_LIMIT);
        buy_limit = (Float[]) cfg.get(BUY_LIMIT);
        sell_wait = (Integer[]) cfg.get(SELL_WAIT);
        buy_wait = (Integer[]) cfg.get(SELL_WAIT);
       
    }
}
