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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import opensesim.gui.util.Json;
import opensesim.gui.util.Json.Import;
import opensesim.sesim.interfaces.Configurable;
import opensesim.util.idgenerator.Id;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AbstractAsset {

    World world;


    private String symbol;
    private String name;
    private String description;
    int decimals;

    /**
     * Constructor
     * @param world
     * @param cfg
     */
    public AbstractAsset(World world, JSONObject cfg) {
        if (world == null)
            return;
        symbol = cfg.getString("symbol");
        name = cfg.getString("name");
        decimals = cfg.optInt("decimals",0);
        
        this.world = world;
    }

  

    public abstract String getTypeName();

    public int getDecimals() {
        return decimals;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCurrency() {
        return false;
    }

    public boolean isAsset() {
        return false;
    }

    public static final String JSON_ID = "id";
    public static final String JSON_CLASS = "class";
    public static final String JSON_SYMBOL = "symbol";
    public static final String JSON_NAME = "name";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_DECIMALS = "decimals";
    public static final int DECIMALS_DEFAULT = 2;

    public static void rename(World world, AbstractAsset a, String symbol) throws Exception {
        if (world.assetsBySymbol.get(symbol) != null) {
            throw new java.lang.Exception("Can't rename asset symbol. Symbol '" + symbol + "' is already in use.");
        }

        world.assetsBySymbol.remove(a.getSymbol());
        a.symbol = symbol;
        world.assetsBySymbol.put(a.getSymbol(), a);

    }

    /*    public static AbstractAsset create(World world, Class<AbstractAsset> cls, String symbol) throws Exception {
        AbstractAsset a = cls.newInstance();
        
        
        if (world.assetsBySymbol.get(symbol) != null) {
            throw new java.lang.Exception("Can't create asset. Symbol '" + symbol + "' is already in use.");
        }

        a.id = world.assetIdGenerator.getNext();
        a.symbol=symbol;

        
        world.assetsById.add(a);
        world.assetsBySymbol.put(a.getSymbol(), a);

        return a;
    }
     */
 /*  public static AbstractAsset create(World world, JSONObject cfg){
        AbstractAsset a;
        String class_name;
        Class cls;
        
        String symbol;
        try {
            symbol = cfg.getString(JSON_SYMBOL);
            class_name = cfg.getString(JSON_CLASS);
            cls = Class.forName(class_name);
            a = create(world,cls,symbol);
            a.name = cfg.optString(JSON_NAME, symbol);
            a.description = cfg.optString(JSON_DESCRIPTION);
            a.decimals = cfg.optInt(JSON_DECIMALS);
            
        }catch (JSONException ex){
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AbstractAsset.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (Exception ex) {
            Logger.getLogger(AbstractAsset.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
     */
 /* public JSONObject getConfig() {
        JSONObject cfg = new JSONObject();
        cfg.put(AbstractAsset.JSON_ID,id.toString());
        cfg.put(AbstractAsset.JSON_CLASS, this.getClass().getName());
        cfg.put(AbstractAsset.JSON_SYMBOL, this.getSymbol());
        cfg.put(AbstractAsset.JSON_DECIMALS, this.getDecimals());
        cfg.put(AbstractAsset.JSON_NAME, this.getName());
        cfg.put(AbstractAsset.JSON_DESCRIPTION, this.getDescription());
        return cfg;
    }


    public void putConfig(JSONObject cfg) {
        symbol = cfg.optString(AbstractAsset.JSON_SYMBOL);
        decimals = cfg.optInt(AbstractAsset.JSON_DECIMALS, AbstractAsset.DECIMALS_DEFAULT);
        name = cfg.optString(AbstractAsset.JSON_NAME, "");
        description = cfg.optString(AbstractAsset.JSON_DESCRIPTION);
    }
     */
    public JPanel getEditGui() {
        return null;
    }
}
