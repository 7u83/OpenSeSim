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

import opensesim.world.Order;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import opensesim.world.AbstractAsset;

import opensesim.sesim.interfaces.Asset;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetPair {

    private final AbstractAsset asset;
    private final AbstractAsset currency;

    public AbstractAsset getAsset() {
        return asset;
    }

    public AbstractAsset getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return asset.getSymbol() + "/" + currency.getSymbol();
    }

    public AssetPair(AbstractAsset asset, AbstractAsset currency) {
        this.asset = asset;
        this.currency = currency;
    }
    
    public AssetPair(World world, String asset, String currency){
        this.asset = world.getAssetBySymbol(asset);
        this.currency = world.getAssetBySymbol(currency);
    }
   

    @Override
    public boolean equals(Object o) {
        
        AssetPair ap = (AssetPair)o;
        if (ap.asset==asset && ap.currency==currency)
            return true;
 //       if (ap.asset==currency && ap.currency==asset)
  //          return false;
        return false;
     }

    

}
