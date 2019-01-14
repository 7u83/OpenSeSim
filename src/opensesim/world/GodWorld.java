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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import opensesim.sesim.interfaces.GetJson;

import opensesim.util.SeSimException;
import opensesim.util.scheduler.Event;
import opensesim.util.scheduler.EventListener;
import opensesim.util.scheduler.Scheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class GodWorld implements GetJson, World {

    @Override
    public void schedule(EventListener listener, long t) {
        scheduler.startTimerTask(listener, t);
    }

    @Override
    public long currentTimeMillis() {
        return scheduler.currentTimeMillis();
    }

    @Override
    public AbstractAsset getDefaultCurrency() {
        return getDefaultAssetPair().getCurrency();
    }

    public static final class JKEYS {

        public static final String ASSETS = "assets";
        public static final String EXCHANGES = "exchanges";

        public static final String ASSET_SYMBOL = "symbol";
        public static final String ASSET_TYPE = "type";

    }

    /*   HashSet<AbstractAsset> assetsById = new HashSet<>();
    HashMap<String, AbstractAsset> assetsBySymbol = new HashMap<>();
     */
    //IDGenerator orderIdGenerator = new IDGenerator();

    private Scheduler scheduler = new Scheduler();

    /**
     * Create a World object.
     *
     * @param cfg
     */
    public GodWorld(JSONObject cfg) {
        init(cfg, false);

    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public GodWorld() {
        this(new JSONObject("{}"));
    }

    private void init(JSONObject cfg, boolean mt) {
        this.scheduler = new Scheduler();
        this.scheduler.start();
        putJson(cfg);
    }

    private void putJson(JSONObject cfg) {
        // Read assets
        JSONArray jassets = cfg.optJSONArray(GodWorld.JKEYS.ASSETS);
        if (jassets == null) {
            jassets = new JSONArray();
        }
        for (int i = 0; i < jassets.length(); i++) {
            JSONObject o = jassets.optJSONObject(i);
            AbstractAsset a;
            try {
                a = createAsset(o);
            } catch (SeSimException ex) {
                Logger.getLogger(RealWorld.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            if (a == null) {
                continue;
            }

            assets.put(a.getSymbol(), a);
        }

        // Read exchanges
        JSONArray exs = cfg.optJSONArray(GodWorld.JKEYS.EXCHANGES);
        if (exs == null) {
            exs = new JSONArray();
        }
        for (int i = 0; i < exs.length(); i++) {
            JSONObject o = exs.optJSONObject(i);
            if (o == null) {
                continue;
            }
            createExchange(o);
        }

    }

    @Override
    public JSONObject getJson() {
        JSONObject cfg = new JSONObject();
        // Write assets
        JSONArray out;
        out = new JSONArray();
        for (AbstractAsset asset : this.getAssetCollection()) {
            out.put(asset.getJson());
        }
        cfg.put(GodWorld.JKEYS.ASSETS, out);

        // Write exchanges
        out = new JSONArray();
        for (Exchange ex : this.getExchangeCollection()) {
            out.put(ex.getJson());
        }
        cfg.put(GodWorld.JKEYS.EXCHANGES, out);

        return cfg;
    }

    public AbstractAsset createAsset(JSONObject cfg) throws SeSimException {
        AbstractAsset a;
        String class_name;
        Class<AbstractAsset> cls;

        try {
            class_name = cfg.getString(JKEYS.ASSET_TYPE);
        } catch (JSONException jex) {
            Logger.getLogger(GodWorld.class.getName()).log(Level.SEVERE, null, jex);
            return null;
        }

        try {
            cls = (Class<AbstractAsset>) Class.forName(class_name);
            a = cls.getConstructor(GodWorld.class, JSONObject.class).newInstance(this, cfg);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GodWorld.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        if (this.assets.get(a.getSymbol()) != null) {
            throw new SeSimException("Already defined");
        }

        assets.put(a.getSymbol(), a);
        return a;
    }

    // --------------------------------------------------------------------
    // Exchanges in our world
    // --------------------------------------------------------------------
    private final HashMap<String, Exchange> exchanges = new HashMap<>();
    private Exchange default_exchange = null;

    public void createExchange(JSONObject cfg) {
        Exchange ex = new Exchange(this, cfg);
        exchanges.put(ex.getSymbol(), ex);
        if (default_exchange == null) {
            default_exchange = ex;
        }
    }

    public Exchange getDefaultExchange() {
        return default_exchange;
    }

    @Override
    public Collection<Exchange> getExchangeCollection() {
        return Collections.unmodifiableCollection(exchanges.values());
    }

    public Exchange addExchange(String symbol) throws SeSimException {
        for (Exchange ex : getExchangeCollection()) {
            if (ex.getSymbol().equals(symbol)) {
                throw (new SeSimException("Exchange already defined."));
            }
        }

        //      Exchange ex = new Exchange(this, symbol);
//        exchanges.add(ex);
//        return ex;
        return null;
    }

    // --------------------------------------------------------------------
    // Assets
    // --------------------------------------------------------------------
    private final HashMap<String, AbstractAsset> assets = new HashMap<>();

    @Override
    public Collection<AbstractAsset> getAssetCollection() {
        return Collections.unmodifiableCollection(assets.values());
    }

    @Override
    public AbstractAsset getAssetBySymbol(String symbol) {
        return this.assets.get(symbol);
    }

    // --------------------------------------------------------------------
    // AssetsPairs
    // --------------------------------------------------------------------    
    private final HashMap<String, AssetPair> asset_pairs = new HashMap<>();

    private AssetPair default_asset_pair = null;

    public Collection<AssetPair> getAssetPairsCollection() {
        return Collections.unmodifiableCollection(asset_pairs.values());
    }

    public void add(AssetPair pair) {
        if (pair.getAsset()==null || pair.getCurrency()==null){
            return;
        }
        asset_pairs.put(pair.getSymbol(), pair);
        if (default_asset_pair == null) {
            default_asset_pair = pair;
        }
    }

    public AssetPair addAssetPair(String asset, String currency) {
        AssetPair pair = new AssetPair(assets.get(asset), assets.get(currency));
        add(pair);
        return pair;
    }

    public AssetPair getDefaultAssetPair() {
        return default_asset_pair;
    }
    
    public AssetPair getAssetPair(Asset asset, Asset currency){
        String s = AssetPair.buildSymbol(asset.getSymbol(), currency.getSymbol());
        return asset_pairs.getOrDefault(s, null);
    }


    /*    public AbstractAsset createAsset(long key, JSONObject cfg) throws SeSimException{
        if (key!=masterkey)
            throw new SeSimException("Access denied.");
        return this.createAsset_p(cfg);
    }
     */
    //   static final String JSON_ASSET = "asset";
    //   static final String JSON_EXCHANGES = "exchanges";
    /*
    public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();

        // save assets
        JSONArray arr;
        arr = new JSONArray();
        for (AbstractAsset a : getAssetCollection()) {
            arr.put(a.getConfig());
        }
        cfg.put(JSON_ASSET, arr);

        // save exchanges
        arr = new JSONArray();
        for (Exchange ex : getExchangeCollection()) {
            arr.put(ex.getConfig());
        }
        cfg.put(JSON_EXCHANGES, arr);

        return cfg;
    }

    @Override
    public void putConfig(JSONObject cfg) {
        JSONArray arr = cfg.optJSONArray(JSON_ASSET);
        if (arr == null) {
            arr = new JSONArray();
        }
        for (Object o : arr) {
            JSONObject acfg = (JSONObject) o;
            AbstractAsset.create(this, acfg);
        }
    }
     */
 /*  public AbstractAsset createAsset(Class cls, String symbol) throws Exception {
        return AbstractAsset.create(this, cls, symbol);
    }
     */
 /*
        static public JSONArray toJson() {

        JSONArray all = new JSONArray();
        for (Map.Entry<Id, Asset> entry : assetsById.entrySet()) {
            Id key = entry.getKey();
            Asset value = entry.getValue();
            all.put(value.getConfig());

        }

        return all;
    }
     */
    /**
     * Get the typename of an AbstractAsset class
     *
     * @param asset_type AbstractAsset
     * @return the type name
     */
    public static String getTypeName(Class<AbstractAsset> asset_type) {
        Constructor<AbstractAsset> c;
        try {
            c = asset_type.getConstructor(GodWorld.class, JSONObject.class);
            AbstractAsset ait = c.newInstance(null, null);
            return ait.getTypeName();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GodWorld.class.getName()).log(Level.SEVERE, null, ex);
            return asset_type.getName();
        }
    }

    public World getWorld() {
        return new RealWorld(this);
    }

    // --------------------------------------------------------------------
    // Stuff belonging to traders
    // --------------------------------------------------------------------
    private final HashSet<Trader> traders = new HashSet<>();

    public Trader createTrader(JSONObject cfg) {
        AbstractTrader trader;
        String strategy = cfg.optString("strategy", null);
        if (strategy == null) {
            return null;
        }

        Class cls;
        try {
            cls = (Class<Trader>) Class.forName(strategy);
            trader = (AbstractTrader) cls.getConstructor(World.class, JSONObject.class).newInstance(this.getWorld(), cfg);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GodWorld.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return trader;
    }

    @Override
    public Collection<Trader> getTradersCollection() {
        return Collections.unmodifiableCollection(traders);
    }

    // --------------------------------------------------------------------
    // Stuff belonging to accounts
    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    // Pseudo random generator stuff
    // --------------------------------------------------------------------   
    Random random = new Random(34561);

    public int randNextInt() {
        return random.nextInt();
    }

    public int randNextInt(int bounds) {
        return random.nextInt(bounds);
    }

    public double randNextDouble() {
        return random.nextDouble();
    }

    public float randNextFloat() {
        return random.nextFloat();
    }

    public boolean randNextBool() {
        return random.nextBoolean();
    }

    @Override
    public float randNextFloat(float min, float max) {
        float r = randNextFloat();
        return (max - min) * r + min;
    }

    // --------------------------------------------------------------------
    // Update listeners
    // --------------------------------------------------------------------
    private final HashSet<EventListener> update_listeners = new HashSet<>();

    public class UpdateEvent extends Event {
    }

    public void addUpdateListener(EventListener u) {
        update_listeners.add(u);
    }

    public void notifyUpdateListeners() {
        Event e = new UpdateEvent();
        for (EventListener l : update_listeners) {
            l.receive(e);
        }
    }
    
    
    public Set getOrderBook(Exchange ex,AssetPair pair,Order.Type type){
        return ((TradingEngine)ex.getAPI(pair)).getOrderBook(type);
    }

}
