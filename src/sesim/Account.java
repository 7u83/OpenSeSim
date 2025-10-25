/*
 * Copyright (c) 2025, 7u83 <7u83@mail.ru>
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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tube
 */
/**
 * Implements a trading account
 */
public class Account {
    private Exchange se;

    private Exchange.AccountListener listener = null;

     long shares;
     long money;
    protected AutoTraderInterface owner;

    final ConcurrentHashMap<Long, Order> orders;

    Account(Exchange se, float money, float shares) {
        this.se = se;

        orders = new ConcurrentHashMap();
        
        // FLOAT_CONVERT
        this.money = (long)(money*se.money_df);
        this.shares = (long)(shares*se.shares_df);
    }

    public float getShares() {
        return shares/se.shares_df;
    }

    public float getMoney() {
        return money/se.money_df;
    }

    public AutoTraderInterface getOwner() {
        return owner;
    }

    /*  public ConcurrentHashMap<Long, Exchange.Order> getOrders() {
        return orders;
    }*/
    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    public void setListener(Exchange.AccountListener al) {
        this.listener = al;
    }

    public void update(Order o) {
        if (listener == null) {
            return;
        }
        listener.accountUpdated(this, o);
    }

    public int getNumberOfOpenOrders() {

        return orders.size();
    }

    public long getCashInOpenOrders_Long() {
        Iterator<Map.Entry<Long, Order>> it = this.getOrders().entrySet().iterator();
        long cash=0;

        while (it.hasNext()) {
            Map.Entry e = it.next();
            Order o = (Order) e.getValue();
            if (o.type == Order.BUYLIMIT) {
                cash += (o.getInitialVolume() - o.getExecuted()) * o.limit;
            }
        }
        return cash;

    }
    
    public float getCashInOpenOrders(){
        return getCashInOpenOrders_Long()/se.money_df;
    }

    public long getSharesInOpenOrders_Long() {
        Iterator<Map.Entry<Long, Order>> it = this.getOrders().entrySet().iterator();
        long volume = 0;

        while (it.hasNext()) {
            Map.Entry e = it.next();
            Order o = (Order) e.getValue();
            if (o.type == Order.SELLLIMIT) {
                volume += o.getInitialVolume() - o.getExecuted();
            }
        }
        return volume;
    }
    
    public float getSharesInOpenOrders(){
        return getSharesInOpenOrders_Long()/se.shares_df;
    }

    public boolean couldBuy(float volume, float limit) {
        float avail = this.getMoney() - this.getCashInOpenOrders();

        System.out.printf("CouldBuy %f<%f\n", volume * limit, avail);
        return volume * limit <= avail;
    }

    public boolean couldSell(float volume) {
        float avail = this.getShares() - this.getSharesInOpenOrders();
        return volume <= avail;
    }

    public float getSharesAvailable() {
        return getShares() - getSharesInOpenOrders();
    }

    public float getCashAvailable() {
        return this.getMoney() - this.getCashInOpenOrders();
    }
    
    public Order getOrderByID(long oid){
        return orders.get(oid);
    }
    
    public boolean isOrderCovered(byte type, float volume, float limit){
      
        
        switch (type){
            case Order.BUYLIMIT:
            case Order.BUY:
                return this.couldBuy(volume, limit);
            case Order.SELLLIMIT:
            case Order.SELL:
                return this.couldSell(volume);
                
        }
        return false;
    }
}
