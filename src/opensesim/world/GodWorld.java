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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import opensesim.sesim.interfaces.GetJson;
import opensesim.util.Scollection;
import opensesim.util.SeSimException;
import opensesim.util.idgenerator.IDGenerator;
import opensesim.world.scheduler.Scheduler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class GodWorld implements GetJson, World {

    public static final class JKEYS {

        public static final String ASSETS = "assets";
        public static final String EXCHANGES = "exchanges";

        public static final String ASSET_SYMBOL = "symbol";
        public static final String ASSET_TYPE = "type";

    }

    /*   HashSet<AbstractAsset> assetsById = new HashSet<>();
    HashMap<String, AbstractAsset> assetsBySymbol = new HashMap<>();
     */
    Scollection<String, AbstractAsset> assets = new Scollection<>();

    IDGenerator assetIdGenerator = new IDGenerator();
    IDGenerator orderIdGenerator = new IDGenerator();

    HashSet<AssetPair> assetPairs = new HashSet<>();

    //ArrayList<Exchange> exchanges = new ArrayList<>();
    private Scheduler scheduler = new Scheduler();

    /**
     * Create a World object.
     *
     * @param cfg
     */
    public GodWorld(JSONObject cfg) {

        putJson(cfg);

    }

    public GodWorld() {
        this(new JSONObject("{}"));
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

            assets.add(a.getSymbol(), a);
        }

        // Read exchanges
        JSONArray exs = cfg.optJSONArray(GodWorld.JKEYS.EXCHANGES);
        if (exs == null) {
            exs = new JSONArray();
        }
        for (int i = 0; i < exs.length(); i++) {
            JSONObject o = exs.optJSONObject(i);
            if (o==null)
                continue;
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

    private long masterkey;

    public GodWorld(JSONObject cfg, long masterkey) {
        this.masterkey = masterkey;
        putJson(cfg);
    }

    /*   public boolean checkMasterKey(long masterkey) {
        return masterkey == this.masterkey;
    }
     */
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

        assets.add(a.getSymbol(), a);
        return a;
    }

    HashMap<String, Exchange> exchanges = new HashMap<>();

    public void createExchange(JSONObject cfg) {
        Exchange ex = new Exchange(this.getWorld(), cfg);
        exchanges.put(ex.getSymbol(), ex);
    }

    @Override
    public Collection<Exchange> getExchangeCollection() {
        return Collections.unmodifiableCollection(exchanges.values());
    }

    public Collection<AbstractAsset> getAssetCollection() {
        return assets.getCollection(); //Collections.unmodifiableCollection(assetsById);
    }

    public AbstractAsset getAssetBySymbol(String symbol) {
        return this.assets.get(symbol);
    }

    public Collection<AssetPair> getAssetPairsCollection() {
        return Collections.unmodifiableCollection(assetPairs);
    }

    public void add(AssetPair pair) {
        assetPairs.add(pair);
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
            c = asset_type.getConstructor(GodWorld.class,JSONObject.class);
            AbstractAsset ait = c.newInstance(null, null);
            return ait.getTypeName();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GodWorld.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public World getWorld() {
        return new RealWorld(this);
    }

    HashSet traders;

    public Trader createTrader() {

        return null;
    }

}
