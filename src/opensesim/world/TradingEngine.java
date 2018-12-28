/*
 * Copyright (c) 2018, 7u83
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import opensesim.util.idgenerator.IDGenerator;
import opensesim.util.scheduler.EventListener;
import opensesim.util.scheduler.FiringEvent;

/**
 *
 * @author 7u83
 */
class TradingEngine implements TradingAPI {

    private final Exchange outer;

    /**
     * Construct a trading engine for an asset pair
     *
     * @param pair The AssetPair obect to create the trading engine for
     * @param outer Outer class - points to an Exchange object thins trading
     * engine belongs to.
     */
    TradingEngine(AssetPair pair, final Exchange outer) {
        this.outer = outer;
        assetpair = pair;
        reset();
    }

    @Override
    public AssetPair getAssetPair() {
        return assetpair;
    }
    IDGenerator id_generator = new IDGenerator();
    private HashMap<Order.Type, SortedSet<Order>> order_books;
    private SortedSet<Order> bidbook, askbook;
    private SortedSet<Order> ul_buy,ul_sell;
    AssetPair assetpair;

    protected final void reset() {
        order_books = new HashMap<>();
      
        // Create an order book for each order type
        for (Order.Type type : Order.Type.values()) {
            order_books.put(type, new TreeSet<>());
        }
        // Save order books to variables for quicker access
        bidbook = order_books.get(Order.Type.BUYLIMIT);
        askbook = order_books.get(Order.Type.SELLLIMIT);
        ul_buy=order_books.get(Order.Type.BUY);
        ul_sell=order_books.get(Order.Type.SELL);        
        
        
        //  quoteHistory = new TreeSet();
        //  ohlc_data = new HashMap();
    }

    /**
     *
     */
    private void executeOrders() {

        double volume_total = 0;
        double money_total = 0;
        while (true) {
            /*           // Match unlimited sell orders against unlimited buy orders
            if (!ul_sell.isEmpty() && !ul_buy.isEmpty()) {
            Order a = ul_sell.first();
            Order b = ul_buy.first();
            Double price = getBestPrice(stock);
            if (price == null) {
            break;
            }
            double volume = b.volume >= a.volume ? a.volume : b.volume;
            finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;
            this.checkSLOrders(price);
            }
            while (!ul_buy.isEmpty() && !ask.isEmpty()) {
            Order a = ask.first();
            Order b = ul_buy.first();
            double price = a.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;
            finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;
            this.checkSLOrders(price);
            }
            // Match unlimited sell orders against limited buy orders
            while (!ul_sell.isEmpty() && !bid.isEmpty()) {
            Order b = bid.first();
            Order a = ul_sell.first();
            double price = b.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;
            finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;
            this.checkSLOrders(price);
            }
             */
            
            // Match limited orders against limited orders
            if (bidbook.isEmpty() || askbook.isEmpty()) {
                // there is nothing to do
                break;
            }
            Order b = bidbook.first();
            Order a = askbook.first();
            if (b.limit < a.limit) {
                break;
            }
            // There is a match, calculate price and volume
            double price = b.id.compareTo(a.id) < 0 ? b.limit : a.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;
            //         finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;
            //        statistics.trades++;
            //        this.checkSLOrders(price);
        }
        if (volume_total == 0) {
            return;
        }
        Quote q = new Quote();
        q.price = money_total / volume_total;
        q.volume = volume_total;
        //     q.time = timer.currentTimeMillis();
        //   addQuoteToHistory(q);
    }

/*    protected void addOrderToBook(Order o) {
        order_books.get(o.type).add(o);
        switch (o.type) {
            case BUY:
            case BUYLIMIT:
                break;
            case SELL:
            case SELLLIMIT:
                break;
        }
    }
*/
    
    
    public Double getBestPrice() {
        SortedSet<Order> bid = order_books.get(Order.Type.BUYLIMIT);
        SortedSet<Order> ask = order_books.get(Order.Type.SELLLIMIT);
        

        Quote lq = null; //this.getLastQuoete();
        Order b = null;
        Order a = null;
        if (!bid.isEmpty()) {
            b = bid.first();
        }
        if (!ask.isEmpty()) {
            a = ask.first();
        }
        // If there is neither bid nor ask and no last quote
        // we can't return a quote
        if (lq == null && b == null && a == null) {
            return null;
        }
        // there is bid and ask
        if (a != null && b != null) {
            Quote q = new Quote();
            System.out.printf("aaaaa bbbbb %f %f \n", a.limit, b.limit);
            // if there is no last quote calculate from bid and ask
            //if (lq == null) {
            double rc = (bid.first().limit + ask.first().limit) / 2.0;
            System.out.printf("RCRC2.0: %f\n", rc);
            return rc;
            // }
            /*
            if (lq.price < b.limit) {
            return b.limit;
            }
            if (lq.price > a.limit) {
            return a.limit;
            }
            return lq.price;
             */
        }
        if (a != null) {
            Quote q = new Quote();
            if (lq == null) {
                return a.limit;
            }
            if (lq.price > a.limit) {
                return a.limit;
            }
            return lq.price;
        }
        if (b != null) {
            Quote q = new Quote();
            if (lq == null) {
                return b.limit;
            }
            if (lq.price < b.limit) {
                return b.limit;
            }
            return lq.price;
        }
        if (lq == null) {
            return null;
        }
        return lq.price;
    }

    @Override
    public Order createOrder(Account account, Order.Type type, double volume, double limit) {
        synchronized (account) {

            // Round volume 
            double v = assetpair.getAsset().roundToDecimals(volume);

            // Order volume must be grater than 0.0.
            if (v <= 0.0) {
                return null;
            }

            // Round currency (limit)
            double l = assetpair.getCurrency().roundToDecimals(limit);

            double order_limit;

            switch (type) {
                case BUYLIMIT: {
                    // verfify available currency for a buy limit order
                    AbstractAsset currency = this.assetpair.getCurrency();
                    Double avail = account.getAvail(currency);

                    // return if not enough money is available
                    if (avail < v * l) {
                        return null;
                    }

                    // reduce the available money 
                    account.assets_avail.put(currency, avail - v * l);
                    order_limit = l;
                    break;
                }

                case BUY: {
                    // For an unlimited by order there is nothing to check
                    // other than currency is > 0.0
                    AbstractAsset currency = this.assetpair.getCurrency();
                    Double avail = account.getAvail(currency);
                     if(avail <=0.0){
                         return null;
                     }

                    // All available monney is assigned to this unlimited order
                    account.assets_avail.put(currency, 0.0);
                    // we "mis"use order_limit to memorize occupied ammount \
                    // of currency
                    order_limit = avail;

                    break;
                }

                case SELLLIMIT:
                case SELL: {

                    // verfiy sell limit
                    AbstractAsset asset = this.assetpair.getAsset();
                    Double avail = account.getAvail(asset);

                    if (avail < v) {
                        // not enough items of asset (shares) available
                        return null;
                    }
                    account.assets_avail.put(asset, avail - v);
                    order_limit = l;
                    break;
                }

                default:
                    return null;

            }

            Order o = new opensesim.world.Order(this, account, type, v, order_limit);

            System.out.printf("The new Order has: volume: %f limit: %f\n", o.getVolume(), o.getLimit());
            synchronized (this) {
                order_books.get(o.type).add(o);
            }
            
            executeOrders();

            for (FiringEvent e : book_listener) {
                e.fire();
            }
            return o;
        }
    }

    HashSet<FiringEvent> book_listener = new HashSet<>();

    @Override
    public void addOrderBookListener(EventListener listener) {
        book_listener.add(new FiringEvent(listener));
    }

    @Override
    public Set getOrderBook(Order.Type type) {
        switch (type) {
            case BUYLIMIT:
            case BUY:
                return Collections.unmodifiableSet(bidbook);
            case SELLLIMIT:
            case SELL:
                return Collections.unmodifiableSet(askbook);

        }
        return null;
//        return Collections.unmodifiableSet(order_books.get(type));
    }

    @Override
    public Set getBidBook() {
        return getOrderBook(Order.Type.BUYLIMIT);
    }

    @Override
    public Set getAskBook() {
        return getOrderBook(Order.Type.SELL);
    }

}
