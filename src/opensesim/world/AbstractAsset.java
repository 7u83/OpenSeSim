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

import java.text.DecimalFormat;
import javax.swing.JPanel;
import opensesim.sesim.interfaces.GetJson;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AbstractAsset implements GetJson {

    GodWorld world;

    private String symbol;
    private String name;
    private String description;
    private int decimals;
    private double decimals_df;
    private DecimalFormat formatter;

    /**
     * Constructor
     *
     * @param world
     * @param cfg
     */
    public AbstractAsset(GodWorld world, JSONObject cfg) {
        if (world == null) {
            return;
        }
        symbol = cfg.getString("symbol");
        name = cfg.getString("name");
        setDecimals(cfg.optInt("decimals", 0));

        this.world = world;
    }

    public AbstractAsset() {

    }

    public double roundToDecimals(double val) {
        return Math.floor(val * decimals_df) / decimals_df;
    }

    protected void setDecimals(int n) {
        decimals = n;
        decimals_df = Math.pow(10, n);

        // create formatter string
        String fs = "#";
        if (n > 0) {
            fs = fs + "0.";
            for (int i = 0; i < n; i++) {
                fs = fs + "0";
            }
        }
        // create fromatter from string
        formatter = new DecimalFormat(fs);
    }

    public final int getDecimals() {
        return decimals;
    }

    public abstract String getTypeName();

    protected void setDescription(String description) {
        this.description = description;
    }

    public final String getSymbol() {
        return symbol;
    }

    protected final void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public final String getName() {
        return name;
    }

    protected final void setName(String name) {
        this.name = name;
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

    @Override
    public JSONObject getJson() {
        JSONObject cfg = new JSONObject();
        cfg.put(GodWorld.JKEYS.ASSET_TYPE, this.getClass().getName());

        cfg.put(AbstractAsset.JSON_SYMBOL, this.getSymbol());
        cfg.put(AbstractAsset.JSON_DECIMALS, this.getDecimals());
        cfg.put(AbstractAsset.JSON_NAME, this.getName());
        cfg.put(AbstractAsset.JSON_DESCRIPTION, this.getDescription());

        return cfg;
    }

    public JPanel getEditGui() {
        return null;
    }
}
