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

import java.text.DecimalFormat;

/**
 *
 * @author tube
 */
public class AssetBase implements Asset {

    private String symbol;
    private String name;
    private float df = 100;
    private int decimals;
    private DecimalFormat formatter;

    public AssetBase(String symbol, String name, int decimals) {
        this.symbol = symbol;
        this.name = name;
        this.setDecimals(decimals);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

//    @Override
/*    public Market getMarket() {
        return null;
    }*/

    @Override
    public float getDf() {
        return df;
    }

    @Override
    public DecimalFormat getFormatter() {
        return formatter;
    }

    private void setDecimals(int n) {
        df = (float) Math.pow(10, n);
        decimals = n;
        formatter = getFormatter(n);
    }

    public DecimalFormat getFormatter(int n) {

        String s = "#0.";
        if (n == 0) {
            s = "#";
        } else {
            for (int i = 0; i < n; i++) {
                s = s + "0";
            }
        }
        return new DecimalFormat(s);
    }

    @Override
    public int getDecimals() {
        return decimals;
    }

    @Override
    public float round(double val) {
        return roundToDecimals(val, df);
    }

    public float roundToDecimals(double val, double f) {
        return (float) ((Math.floor(val * f) / f));
    }

}
