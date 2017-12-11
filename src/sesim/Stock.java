/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Stock {

    private final String symbol;
    private String name;

    Stock(String symbol) {
        this.symbol = symbol;

        reset();
    }

    /**
     *
     */
    public final void reset() {
        order_books = new HashMap();

        // Create an order book for each order type
        for (Order.OrderType type : Order.OrderType.values()) {
            this.order_books.put(type, new TreeSet(new Exchange.OrderComparator(type)));
        }

        quoteHistory = new TreeSet();
        ohlc_data = new HashMap();
    }

    String getSymbol() {
        return symbol;
    }

    String getName() {
        return name;
    }

    protected HashMap<Order.OrderType, SortedSet<Order>> order_books;
    //   protected ConcurrentLinkedQueue<Order> order_queue = new ConcurrentLinkedQueue();

    /**
     * Histrory of quotes
     */
    public TreeSet<Quote> quoteHistory;

    HashMap<Integer, OHLCData> ohlc_data = new HashMap<>();

    private OHLCData buildOHLCData(int timeFrame) {
        OHLCData data = new OHLCData(timeFrame);
        if (quoteHistory == null) {
            return data;
        }

        Iterator<Quote> it = quoteHistory.iterator();
        while (it.hasNext()) {
            Quote q = it.next();
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);

        }

        return data;
    }

    public OHLCData getOHLCdata(Integer timeFrame) {
        OHLCData data;
        data = ohlc_data.get(timeFrame);
        if (data == null) {

            synchronized (this) {
                data = buildOHLCData(timeFrame);
                ohlc_data.put(timeFrame, data);
            }
        }
        return data;
    }

    protected void updateOHLCData(Quote q) {

        Iterator<OHLCData> it = ohlc_data.values().iterator();
        while (it.hasNext()) {
            OHLCData data = it.next();
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        }
    }

    protected void addOrderToBook(Order o) {
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

        /**
     *
     * @param stock
     * @param type
     * @param depth
     * @return
     */
    public ArrayList<Order> getOrderBook(Order.OrderType type, int depth) {

        SortedSet<Order> book = order_books.get(type);
        if (book == null) {
            return null;
        }
        ArrayList<Order> ret;
        synchronized (this) {

            ret = new ArrayList<>();

            Iterator<Order> it = book.iterator();

            for (int i = 0; i < depth && it.hasNext(); i++) {
                Order o = it.next();
                
                if (o.volume <= 0) {
                    // throw an exception here
                }
                ret.add(o);
            }
        }
        return ret;
    }

    
    
}
