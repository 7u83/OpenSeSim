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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import opensesim.sesim.interfaces.GetJson;
import opensesim.util.idgenerator.IDGenerator;
import opensesim.util.SeSimException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class World implements GetJson {

    public static final class JKEYS {

        public static final String ASSETS = "assets";
        public static final String EXCHANGES = "exchanges";
        
        public static final String ASSET_SYMBOL = "symbol";
        public static final String ASSET_TYPE = "type";
        
        
    }

    HashSet<AbstractAsset> assetsById = new HashSet<>();
    HashMap<String, AbstractAsset> assetsBySymbol = new HashMap<>();
    
    
    
    IDGenerator assetIdGenerator = new IDGenerator();
    IDGenerator orderIdGenerator = new IDGenerator();

    HashSet<AssetPair> assetPairs = new HashSet<>();

    ArrayList<Exchange> exchanges = new ArrayList<>();
    
    
    
    
    

    /**
     * Create a World object.
     *
     * @param cfg
     */
    public World(JSONObject cfg) {

        putJson(cfg);
        

    }
    
    private void putJson(JSONObject cfg){
        // Read assets
        JSONArray jassets = cfg.optJSONArray(World.JKEYS.ASSETS);
        if (jassets==null){
            jassets=new JSONArray();
        }
        for (int i=0; i<jassets.length();i++) {
            JSONObject o = jassets.optJSONObject(i);
            AbstractAsset a;
            try {
                a = createAsset_p(o);
            } catch (SeSimException ex) {
                Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            assetsById.add(a);
            assetsBySymbol.put(a.getSymbol(), a);
        }        
    }

    @Override
    public JSONObject getJson() {
        JSONObject cfg=new JSONObject();
        // Write assets
        JSONArray jassets = new JSONArray();
        for (AbstractAsset asset: this.getAssetCollection()){
            jassets.put(asset.getJson());
        }
        cfg.put(World.JKEYS.ASSETS, jassets);
        return cfg;
    }

    private long masterkey;
    public World(JSONObject cfg, long masterkey){
        this.masterkey=masterkey;
        putJson(cfg);
    }
    
    public boolean checkMasterKey(long masterkey){
        return masterkey == this.masterkey;
    }

    private AbstractAsset createAsset_p(JSONObject cfg) throws SeSimException {
        AbstractAsset a;
        String class_name;
        Class<AbstractAsset> cls;

        try {
            class_name = cfg.getString(JKEYS.ASSET_TYPE);
        }catch (JSONException jex){
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, jex);            
            return null;
        }
        
     
        try {
            cls = (Class<AbstractAsset>) Class.forName(class_name);
            a =  cls.getConstructor(World.class,JSONObject.class).newInstance(this,cfg);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        if (this.assetsBySymbol.get(a.getSymbol())!=null){
            throw new SeSimException("Already defined");
        }
        
        this.assetsById.add(a);
        this.assetsBySymbol.put(a.getSymbol(),a);
        return a;
    }

    public Collection<AbstractAsset> getAssetCollection() {
        return Collections.unmodifiableCollection(assetsById);
    }
    
    public AbstractAsset getAssetBySymbol(String symbol){
        return this.assetsBySymbol.get(symbol);
    }

    public Collection<AssetPair> getAssetPairsCollection() {
        return Collections.unmodifiableCollection(assetPairs);
    }

    public Collection<Exchange> getExchangeCollection() {
        return Collections.unmodifiableCollection(exchanges);
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

        Exchange ex = new Exchange(this, symbol);
        exchanges.add(ex);
        return ex;
    }

    
    public AbstractAsset createAsset(long key, JSONObject cfg) throws SeSimException{
        if (key!=masterkey)
            throw new SeSimException("Access denied.");
        return this.createAsset_p(cfg);
    }
    
    
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
}
