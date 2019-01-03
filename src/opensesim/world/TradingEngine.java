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
import opensesim.util.idgenerator.LongIDGenerator;
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
     * @param pair The AssetPair object to create the trading engine for
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
    LongIDGenerator quote_id_generator = new LongIDGenerator();

    private HashMap<Order.Type, SortedSet<Order>> order_books;
    private SortedSet<Order> bidbook, askbook;
    private SortedSet<Order> ul_buy, ul_sell;
    AssetPair assetpair;

    TreeSet<Quote> quote_history;
    Quote last_quote;

    protected final void reset() {
        order_books = new HashMap<>();

        // Create an order book for each order type
        for (Order.Type type : Order.Type.values()) {
            order_books.put(type, new TreeSet<>());
        }
        // Save order books to variables for quicker access
        bidbook = order_books.get(Order.Type.BUYLIMIT);
        askbook = order_books.get(Order.Type.SELLLIMIT);
        ul_buy = order_books.get(Order.Type.BUY);
        ul_sell = order_books.get(Order.Type.SELL);

        quote_history = new TreeSet<>();

        last_quote = null;

        Quote q = new Quote(-1);
        q.price = 100.0;
        last_quote = q;

        //  ohlc_data = new HashMap();
    }

    void addQuoteToHistory(Quote q) {
        /*    if (statistics.heigh == null) {
            statistics.heigh = q.price;
        } else if (statistics.heigh < q.price) {
            statistics.heigh = q.price;
        }
        if (statistics.low == null) {
            statistics.low = q.price;
        } else if (statistics.low > q.price) {
            statistics.low = q.price;
        }
         */
//        Stock stock = getDefaultStock();
        quote_history.add(q);
//        stock.updateOHLCData(q);
        //       updateQuoteReceivers(q);
    }

    boolean compact_history = false;
    boolean compact_last = true;

    private void transferMoneyAndShares(Account src, Account dst, double money, double shares) {
//        src.money -= money;

        AssetPack pack;

        pack = new AssetPack(assetpair.getCurrency(), money);
        src.sub(pack);
        dst.add(pack);

        pack.asset = assetpair.getAsset();
        pack.volume = shares;
        src.add(pack);
        dst.sub(pack);

        /*    src.addMoney(-money);

        //      dst.money += money;
        dst.addMoney(money);
        //    src.shares -= shares;
        src.addShares(-shares);
        //    dst.shares += shares;

        src.addShares(shares);
         */
    }

    private void finishTrade(Order b, Order a, double price, double volume) {

        // Transfer money and shares
        transferMoneyAndShares(b.account, a.account, volume * price, volume);

        // Update volume
        b.volume -= volume;
        a.volume -= volume;

        b.cost += price * volume;
        a.cost += price * volume;

        removeOrderIfExecuted(a);
        removeOrderIfExecuted(b);

        a.account.notfiyListeners();
        b.account.notfiyListeners();

    }

    private void removeOrderIfExecuted(Order o) {

        if (o.volume != 0) {

            o.status = Order.Status.PARTIALLY_EXECUTED;
            //o.account.update(o);
            return;
        }

//        o.account.orders.remove(o.id);
        SortedSet book = order_books.get(o.type);

        book.remove(book.first());

        //      o.status = OrderStatus.CLOSED;
        //      o.account.update(o);
    }

    /**
     *
     */
    private void executeOrders() {

        Quote q = null;

        double volume_total = 0;
        double money_total = 0;
        while (true) {

            // Match unlimited sell orders against unlimited buy orders
            while (!ul_sell.isEmpty() && !ul_buy.isEmpty()) {
                Order a = ul_sell.first();
                Order b = ul_buy.first();
                Double price = getBestPrice();

                if (price == null) {
                    // Threre is no price available, we can't  match, we
                    // have to wait until some limited orders come in
                    break;
                }

                // calculate volume by best fit
                double volume = b.volume >= a.volume ? a.volume : b.volume;

                double avdiff = b.limit - price * volume;
                b.account.addAvail(assetpair.getCurrency(), avdiff);

                finishTrade(b, a, price, volume);
                volume_total += volume;
                money_total += price * volume;

                Order.Type type = Order.Type.BUYLIMIT;
                if (!compact_history) {
                    q = new Quote(quote_id_generator.getNext());
                    q.price = price;
                    q.volume = volume;
                    q.time = outer.world.currentTimeMillis();
                    q.type = type;
                    addQuoteToHistory(q);
                }

                //this.checkSLOrders(price);
            }

            /*
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
            //
            // Match limited orders against limited orders
            //
            if (bidbook.isEmpty() || askbook.isEmpty()) {
                // no limit orders at all, nothing to do
                break;
            }

            // Get the top orders - each from bidbook and askbook, but 
            // let orders stay in their order books
            Order b = bidbook.first();
            Order a = askbook.first();

            if (b.limit < a.limit) {
                // limits do not match, so there is nothing to do
                break;
            }

            // There is a match. Next we calculate price and volume.
            // The price is set by the order with lower ID because the
            // order with lower ID was placed first. Also the order with
            // the lower id is maker, while the higher ID is the taker.
            double price;
            Order.Type type;
            if (b.id.compareTo(a.id) < 0) {
                price = b.limit;
                type = Order.Type.SELL;
            } else {
                price = a.limit;
                type = Order.Type.BUY;
            }

            // The volume is calculated by best fit
            double volume = b.volume >= a.volume ? a.volume : b.volume;

            // Update available currency for the buyer.
            // For sellers there is no need to update.
            double avdiff = b.limit * volume - price * volume;
            b.account.addAvail(assetpair.getCurrency(), avdiff);
            if (b.account.getLeverage()>0.0){
                //b.account.margin_bound-=avdiff;
                b.account.margin_bound-=b.limit*volume;
            }

            //  b.account.addMarginAvail(assetpair.getCurrency(), avdiff/b.account.getLeverage());
            finishTrade(b, a, price, volume);

            if (!compact_history) {
                q = new Quote(quote_id_generator.getNext());
                q.price = price;
                q.volume = volume;
                q.time = outer.world.currentTimeMillis();
                q.type = type;
                addQuoteToHistory(q);
            }

            volume_total += volume;
            money_total += price * volume;

            //        statistics.trades++;
            //        this.checkSLOrders(price);
        }

        if (volume_total == 0) {
            return;
        }

        Quote qc;
        qc = new Quote(quote_id_generator.getNext());
        qc.price = money_total / volume_total;
        qc.volume = volume_total;
        qc.time = outer.world.currentTimeMillis();

        if (compact_history) {
            addQuoteToHistory(qc);
        }

        if (compact_last) {
            last_quote = qc;
        } else {
            last_quote = q;
        }

    }

    public Double getBestPrice() {

        Order b;
        Order a;

        // Get first limited orders from bid and ask, 
        // assign null if no order is present
        b = !bidbook.isEmpty() ? bidbook.first() : null;
        a = !askbook.isEmpty() ? askbook.first() : null;

        // If there is neither bid nor ask and also no last quote
        // we can't return a price
        if (last_quote == null && b == null && a == null) {
            return null;
        }

        // Both limited bid and ask are present
        if (a != null && b != null) {

            // if there is no last quote, we calculate the prpice 
            // from bid and ask by simply averaging the limits
            if (last_quote == null) {
                return (bidbook.first().limit + askbook.first().limit) / 2.0;
            }

            // Last quote is below bid, so the best price is the
            // current bid
            if (last_quote.price < b.limit) {
                return b.limit;
            }

            // Last price is grater ask, so return the current ask
            if (last_quote.price > a.limit) {
                return a.limit;
            }

            // Last price is somewhere between bid and ask, 
            // we return the last price
            return last_quote.price;

        }

        // There is no limited ask, but limited bid
        if (a != null) {

            // retrun last quote if present or lower than ask,
            // otherwise return the current ask
            if (last_quote == null) {
                return a.limit;
            }
            if (last_quote.price > a.limit) {
                return a.limit;

            }
            return last_quote.price;

        }

        // No bid, but ask is present
        // Same as a !=null like before but reversed 
        if (b != null) {
            if (last_quote == null) {
                return b.limit;
            }
            if (last_quote.price
                    < b.limit) {
                return b.limit;
            }
            return last_quote.price;
        }

        // Both bid and ask are not present, return last quote.
        // The case that last_quote is null can never happen here.
        return last_quote.price;

    }

    @Override
    public Order createOrder(Account account, Order.Type type,
            double volume, double limit) {

        Order o;

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

            if (account.isUnlimied()) {
                order_limit = l;
            } else {
                switch (type) {
                    case BUYLIMIT: {
                        Double avail;
  
                        // verfify available currency for a buy limit order
                        AbstractAsset currency = this.assetpair.getCurrency();
                        if (account.getLeverage()==0.0){
                            avail = account.getAvail(currency);
                            account.addAvail(currency, -(v * l));

                        }
                        else{


                            avail = account.getMargin(assetpair.getCurrency());
                                
                        }
                        
                        // return if not enough funds are available
                        if (avail < v * l) {
                           o = new Order(this, account, type, v, l);
                           o.status=Order.Status.ERROR;
                           
                           System.out.printf("Error order no funds\n");
                           return o;
                        }

                        account.margin_bound+=v*l;
                        // reduce the available money 
//                  account.assets_avail.put(currency, avail - v * l);

//account.addMarginAvail(currency, -((v * l)/account.getLeverage()));
                        order_limit = l;
                        break;

                    }

                    case BUY: {
                        // For an unlimited by order there is nothing to check
                        // other than currency is > 0.0
                        AbstractAsset currency = this.assetpair.getCurrency();
                        Double avail = account.getAvail(currency);

                        if (avail <= 0.0) {
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
                            //    return null;
                        }
                        account.assets_avail.put(asset, avail - v);
                        order_limit = l;
                        break;

                    }

                    default:
                        return null;

                }
            }
        
        o = new Order(this, account, type, v, order_limit);

        //System.out.printf("The new Order has: volume: %f limit: %f\n", o.getVolume(), o.getLimit());
        synchronized (this) {
            order_books.get(o.type).add(o);

        }
    }

    executeOrders();
    last_quote.price = 200; //75-12.5;
    for (FiringEvent e : book_listener

    
        ) {
            e.fire();
    }
    return o ;

}

HashSet

<FiringEvent

> book_listener 

= new HashSet

<>();

    @Override
        public 

void addOrderBookListener

(EventListener 

listener

) {
        book_listener

.add

(new FiringEvent

(listener

));

    

}

    @Override
        public Set 

getOrderBook

(Order

.Type 

type

) {
        switch (type

) {
            case BUYLIMIT

:
            case BUY

:
                return Collections

.unmodifiableSet

(bidbook

);

            

case SELLLIMIT

:
            case SELL

:
                return Collections

.unmodifiableSet

(askbook

);

        

}
        return null;
    

}

    @Override
        public Set
            

getBidBook

() {
        return getOrderBook

(Order

.Type

.BUYLIMIT
        

);

    

}

    @Override
        public Set
            

getAskBook

() {
        return getOrderBook

(Order

.Type

.SELL
        

);

    

}

    @Override
        public Set

<Quote

> getQuoteHistory

() {
        return Collections
                

.unmodifiableSet

(quote_history
                

);
    }

}
