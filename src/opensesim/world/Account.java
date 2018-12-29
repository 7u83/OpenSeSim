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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import opensesim.util.scheduler.Event;
import opensesim.util.scheduler.EventListener;
import org.json.JSONObject;

/**
 * Class to hold account data of traders
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Account {

    HashMap<AbstractAsset, Double> assets = new HashMap<>();
    HashMap<AbstractAsset, Double> assets_avail = new HashMap<>();

    Trader owner;
    Exchange exchange = null;

    public Map<AbstractAsset, Double> getAssets() {
        return Collections.unmodifiableMap(assets);
    }

    public Map<AbstractAsset, Double> getAssetsAavail() {
        return Collections.unmodifiableMap(assets_avail);
    }

    public Trader getOwner() {
        return owner;
    }

    protected Account(Exchange exchange) {
        this.exchange = exchange;
    }

    protected Account(Exchange exchange, JSONObject cfg) {
        this.exchange = exchange;
    }

    Account() {

    }

    synchronized void add(AssetPack pack) {
        assets.put(pack.asset, get(pack.asset) + pack.volume);
        assets_avail.put(pack.asset, getAvail(pack.asset) + pack.volume);
    }

    synchronized void sub(AssetPack pack) {
        assets.put(pack.asset, get(pack.asset) - pack.volume);
     //   assets_avail.put(pack.asset, getAvail(pack.asset) - pack.volume);
    }

    
    
    
    public double get(AbstractAsset asset) {
        return assets.getOrDefault(asset, 0.0);
    }

    public double getAvail(AbstractAsset asset) {
        return assets_avail.getOrDefault(asset, 0.0);
    }

    public void addAvail(AbstractAsset asset, double val) {
        double avail = getAvail(asset);
        assets_avail.put(asset, avail+val);
    }    
    
    HashSet<EventListener> listeners = new HashSet<>();
    public void addListener(EventListener l){
        listeners.add(l);
    }
    
    public void notfiyListeners(){
        Event e = new Event() {};
        for(EventListener l: listeners){
            l.receive(e);
        }
    }
}
