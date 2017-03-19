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


import org.json.JSONArray;
import org.json.JSONObject;

import sesim.AutoTraderConfigBase;
import sesim.AutoTraderConfig;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.OldAutoTrader;

/**
 *
 * @author 7u83
 */
public class RandomTraderConfig extends AutoTraderConfigBase implements AutoTraderConfig {

    public Float[] sell_volume = {100f, 100f};
    public Float[] sell_limit = {-0.1f, 0.10101f};
    public Long[] sell_wait = {10000L, 50000L};
    public Long[] wait_after_sell = {1000L, 30000L};

    public Float[] buy_volume = {100f, 100f};
    public Float[] buy_limit = {-0.1f, 0.10101f};
    public Long[] buy_wait = {10000L, 50000L};
    public Long[] wait_after_buy = {10L, 30L};

    @Override
    public OldAutoTrader createTrader(Exchange se, JSONObject cfg, long id, String name, double money, double shares) {
        if (cfg != null) {
            this.putConfig(cfg);
        }
        return null;
        //return new traders.RandomTrader(se, id, name, money, shares, this);
    }

    @Override
    public String getDisplayName() {
        return "Random A";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;
        //return new RandomTraderGui(this);
    }

    final String SELL_VOLUME = "sell_volume";
    final String BUY_VOLUME = "buy_volume";
    final String SELL_LIMIT = "sell_limit";
    final String BUY_LIMIT = "buy_limit";
    final String SELL_WAIT = "sell_wait";
    final String BUY_WAIT = "buy_wait";
    final String WAIT_AFTER_SELL = "sell_wait_after";
    final String WAIT_AFTER_BUY = "buy_wait_after";

    @Override
    public JSONObject getConfig() {
        JSONObject jo = new JSONObject();
        jo.put(SELL_VOLUME, sell_volume);
        jo.put(BUY_VOLUME, buy_volume);
        jo.put(SELL_LIMIT, sell_limit);
        jo.put(BUY_LIMIT, buy_limit);
        jo.put(SELL_WAIT, sell_wait);
        jo.put(BUY_WAIT, buy_wait);
        jo.put(WAIT_AFTER_SELL, wait_after_sell);
        jo.put(WAIT_AFTER_BUY, wait_after_buy);
        jo.put("base",this.getClass().getCanonicalName());

        return jo;
    }

    /*  private <T extends Number> T to(Double o){
       if (Float==T){
           System.out.printf("Double ret %", o.floatValue());
           return new T(3); 
       }
       return null;
   }
     */
    private Float[] to_float(JSONArray a) {
        Float[] ret = new Float[a.length()];
        for (int i = 0; i < a.length(); i++) {
            ret[i] = new Float(a.getDouble(i));

        }
        return ret;
    }

    private Integer[] to_integer(JSONArray a) {
        Integer[] ret = new Integer[a.length()];
        for (int i = 0; i < a.length(); i++) {
            ret[i] = a.getInt(i);

        }
        return ret;

    }
    
        private Long[] to_long(JSONArray a) {
        Long[] ret = new Long[a.length()];
        for (int i = 0; i < a.length(); i++) {
            ret[i] = a.getLong(i);

        }
        return ret;

    }


    private Number[] to_arn(JSONArray a) {
        Number[] ret = new Number[a.length()];
        //  Float x[] = new Float[2]; 

        for (int i = 0; i < a.length(); i++) {
            ret[i] = (Number) a.get(i);
        }
        return ret;
    }

    public void putConfig(JSONObject cfg) {
        if (cfg == null) {
            return;
        }

   //     System.out.printf("Putconfig %s\n", cfg.toString(4));

        String cname = cfg.get(SELL_VOLUME).getClass().getName();

        //     JSONArray a = cfg.getJSONArray(SELL_VOLUME);
     //   System.out.printf("Array = %s \n", cname);

        sell_volume = to_float(cfg.getJSONArray(SELL_VOLUME));
        buy_volume = to_float(cfg.getJSONArray(BUY_VOLUME));
        sell_limit = to_float(cfg.getJSONArray(SELL_LIMIT));
        buy_limit = to_float(cfg.getJSONArray(BUY_LIMIT));
        sell_wait = to_long(cfg.getJSONArray(SELL_WAIT));
        buy_wait = to_long(cfg.getJSONArray(BUY_WAIT));

        wait_after_sell = to_long(cfg.getJSONArray(WAIT_AFTER_SELL));
        wait_after_buy = to_long(cfg.getJSONArray(WAIT_AFTER_BUY));

    }

    @Override
    public boolean getDevelStatus() {
        return false;
        
    }
}
