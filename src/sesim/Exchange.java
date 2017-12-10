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

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.Order.OrderStatus;
import sesim.Order.OrderType;

/**
 * @desc Echchange class
 * @author 7u83
 */
public class Exchange {
//private  HashMap<Order.OrderType, SortedSet<Order>> order_books;

    HashMap<String, Stock> stocks;

    final String DEFAULT_STOCK = "def";

    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }

    public Stock getDefaultStock() {
        return getStock(DEFAULT_STOCK);
    }

    public String getDefaultStockSymbol() {
        return DEFAULT_STOCK;
    }

    private double money_df = 10000;
    private int money_decimals = 2;
    DecimalFormat money_formatter;

    /**
     * Set the number of decimals used with money
     *
     * @param n number of decimals
     */
    public void setMoneyDecimals(int n) {
        money_df = Math.pow(10, n);
        money_decimals = n;
        money_formatter = getFormatter(n);
    }

    private double shares_df = 1;
    private double shares_decimals = 0;
    private DecimalFormat shares_formatter;

    /**
     * Set the number of decimals for shares
     *
     * @param n number of decimals
     */
    public void setSharesDecimals(int n) {
        shares_df = Math.pow(10, n);
        shares_decimals = n;
        shares_formatter = getFormatter(n);
    }

    public double roundToDecimals(double val, double f) {
        return Math.floor(val * f) / f;
    }

    public double roundShares(double shares) {
        return roundToDecimals(shares, shares_df);
    }

    public double roundMoney(double money) {
        return roundToDecimals(money, money_df);
    }

    public DecimalFormat getFormatter(int n) {
        DecimalFormat formatter;
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

    public DecimalFormat getMoneyFormatter() {
        return money_formatter;
    }

    public DecimalFormat getSharesFormatter() {
        return shares_formatter;
    }

    IDGenerator account_id_generator = new IDGenerator();
    //public static Timer timer = new Timer();

    public Scheduler timer; // = new Scheduler();

    public ArrayList<AutoTraderInterface> traders;

    /**
     *
     */
    public interface AccountListener {

        public void accountUpdated(Account a, Order o);
    }

    public interface OrderListener {

        public void orderUpdated(Order o);
    }

    //HashMap<Integer, OHLCData> ohlc_data = new HashMap<>();

/*    public OHLCData buildOHLCData(int timeFrame) {
        Stock stock = getDefaultStock();

        OHLCData data = new OHLCData(timeFrame);
        if (stock.quoteHistory == null) {
            return data;
        }

        Iterator<Quote> it = stock.quoteHistory.iterator();
        while (it.hasNext()) {
            Quote q = it.next();
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);

        }

        return data;
    }
*/
    
    public void injectMoney() {

        accounts.forEach(new BiConsumer() {
            @Override
            public void accept(Object t, Object u) {
                Account a = (Account) u;
                //  a.money += 20000.0;
                a.addMoney(20000.0);

            }

        });

    }

    public void pointZero() {
        accounts.forEach(new BiConsumer() {
            @Override
            public void accept(Object t, Object u) {
                Account a = (Account) u;
                //   a.money = 20000.0;
                a.setMoney(20000.0);

            }

        });

    }

    public OHLCData getOHLCdata(Stock stock,Integer timeFrame) {
        return stock.getOHLCdata(timeFrame);
        
/*        OHLCData data;
        data = stock.ohlc_data.get(timeFrame);
        if (data == null) {

            synchronized (executor) {
                data = this.buildOHLCData(timeFrame);
                stock.ohlc_data.put(timeFrame, data);
            }
        }
        return data;
*/        
    }

    void updateOHLCData(Stock stock,Quote q) {
        Iterator<OHLCData> it = stock.ohlc_data.values().iterator();
        while (it.hasNext()) {
            OHLCData data = it.next();
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        }
    }

    ArrayList<Indicator> indicators;

    void updateIndicators(Quote q) {

    }

    public void addIndicator(Indicator i) {
        this.indicators.add(i);
    }

    public void createTraders(JSONArray traderdefs) {
        for (int i = 0; i < traderdefs.length(); i++) {
            JSONObject o = traderdefs.getJSONObject(i);

        }

        //    this.traders.add(randt);
        //    randt.setName("Bob");
        //    randt.start();
    }

    // private final ConcurrentHashMap<Double, Account> accounts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Double, Account> accounts;

    public double createAccount(double money, double shares) {

        double id = (random.nextDouble() + (account_id_generator.getNext()));

        Account a = new Account(id, money, shares);
        accounts.put(a.id, a);
        return a.id;
    }

    static class OrderComparator implements Comparator<Order> {

        OrderType type;

        OrderComparator(OrderType type) {
            this.type = type;
        }

        @Override
        public int compare(Order left, Order right) {
            double d;
            switch (this.type) {
                case BUYLIMIT:
                case STOPBUY:
                case BUY:
                    d = right.limit - left.limit;
                    break;
                case SELLLIMIT:
                case STOPLOSS:
                case SELL:
                    d = left.limit - right.limit;
                    break;
                default:
                    d = 0;

            }
            if (d != 0) {
                return d > 0 ? 1 : -1;
            }

            d = right.initial_volume - left.initial_volume;
            if (d != 0) {
                return d > 0 ? 1 : -1;
            }

            if (left.id < right.id) {
                return -1;
            }
            if (left.id > right.id) {
                return 1;
            }

            return 0;

        }

    }

    IDGenerator order_id_generator = new IDGenerator();

    final void initExchange() {

        Stock defstock = new Stock(DEFAULT_STOCK);
        stocks = new HashMap();
        stocks.put(defstock.getSymbol(), defstock);

        buy_orders = 0;
        sell_orders = 0;
        timer = new Scheduler();         //  timer = new Scheduler();
        //       random = new Random(12);
        random = new Random();

        //    quoteHistory = new TreeSet();
        getDefaultStock().reset();

        accounts = new ConcurrentHashMap<>();

        traders = new ArrayList();

        statistics = new Statistics();
        //num_trades = 0;

       // getDefaultStock().ohlc_data = new HashMap();

        // Create order books

        /*        order_books = new HashMap();
        for (OrderType type : OrderType.values()) {
            order_books.put(type, new TreeSet(new OrderComparator(type)));
        }
         */
    }

    /**
     * Constructor
     */
    public Exchange() {
        qrlist = (new CopyOnWriteArrayList<>());

        initExchange();
        //executor.start();

    }

    public class Statistics {

        public long trades;
        public long orders;
        public Double heigh;
        public Double low;

        public final void reset() {
            trades = 0;
            heigh = null;
            low = null;

        }

        Statistics() {
            reset();
        }

    };

    Statistics statistics;

//    long num_trades = 0;
//    long num_orders = 0;
    public Statistics getStatistics() {
        return statistics;
        /*        Statistics s = new Statistics();
        s.trades = num_trades;
        s.orders = num_orders;
        return s;
         */

    }

    class Executor extends Thread {

        @Override
        public void run() {
            /*            Stock stock = getDefaultStock();
            
            synchronized (this) {
                try {
                    while (true) {

                        this.wait();

                        Order o;
                        while (null != (o = stock.order_queue.poll())) {
                            addOrderToBook(o);
                            Account a = o.account;
                            a.orders.put(o.id, o);
                            a.update(o);
                            executeOrders(getDefaultStock());
                        }

                        updateBookReceivers(OrderType.SELLLIMIT);
                        updateBookReceivers(OrderType.BUYLIMIT);
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(Exchange.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
             */
        }

    }

    final Executor executor = new Executor();

    /**
     * Start the exchange
     */
    public void start() {
        timer.start();

    }

    public void reset() {
        initExchange();
    }

    public void terminate() {
        timer.terminate();
    }

    /*
    class BidBook extends TreeSet {

        TreeSet t = new TreeSet();

        boolean hallo() {
            t.comparator();
            return true;
        }
    }
     */
 /*    public SortedSet<Quote> getQuoteHistory(long start) {

        Quote s = new Quote();
        s.time = start * 1000;
        s.id = 0;

        TreeSet<Quote> result = new TreeSet<>();
        result.addAll(this.quoteHistory.tailSet(s));

        return result;

    }
     */
    public final String CFG_MONEY_DECIMALS = "money_decimals";
    public final String CFG_SHARES_DECIMALS = "shares_decimals";

    public void putConfig(JSONObject cfg) {
        try {
            this.setMoneyDecimals(cfg.getInt(CFG_MONEY_DECIMALS));
            this.setSharesDecimals(cfg.getInt(CFG_SHARES_DECIMALS));
        } catch (Exception e) {

        }

    }

    public Double getBestPrice(Stock stock) {

        SortedSet<Order> bid = stock.order_books.get(OrderType.BUYLIMIT);
        SortedSet<Order> ask = stock.order_books.get(OrderType.SELLLIMIT);

        Quote lq = this.getLastQuoete();
        Order b = null, a = null;
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

    public Quote getBestPrice_0(Stock stock) {

        synchronized (executor) {
            SortedSet<Order> bid = stock.order_books.get(OrderType.BUYLIMIT);
            SortedSet<Order> ask = stock.order_books.get(OrderType.SELLLIMIT);

            Quote lq = this.getLastQuoete();
            Order b = null, a = null;
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

                // if there is no last quote calculate from bid and ask
                if (lq == null) {
                    q.price = (bid.first().limit + ask.first().limit) / 2.0;
                    return q;
                }

                if (lq.price < b.limit) {
                    q.price = b.limit;
                    return q;
                }
                if (lq.price > a.limit) {
                    q.price = a.limit;
                    return q;
                }
                return lq;
            }

            if (a != null) {
                Quote q = new Quote();
                if (lq == null) {

                    q.price = a.limit;
                    return q;
                }
                if (lq.price > a.limit) {
                    q.price = a.limit;
                    return q;
                }
                return lq;
            }

            if (b != null) {
                Quote q = new Quote();
                if (lq == null) {
                    q.price = b.limit;
                    return q;
                }
                if (lq.price < b.limit) {
                    q.price = b.limit;
                    return q;
                }

                return lq;
            }

            return lq;
        }
    }

    public Quote getBestPrice_0() {
        return getBestPrice_0(getDefaultStock());
    }

    // Class to describe an executed order
    // QuoteReceiver has to be implemented by objects that wants 
    // to receive quote updates  	
    public interface QuoteReceiver {

        void UpdateQuote(Quote q);
    }

    /**
     * Bookreceiver Interface
     */
    public interface BookReceiver {

        void UpdateOrderBook();
    }

    final private ArrayList<BookReceiver> ask_bookreceivers = new ArrayList<>();
    final private ArrayList<BookReceiver> bid_bookreceivers = new ArrayList<>();

    private ArrayList<BookReceiver> selectBookReceiver(OrderType t) {
        switch (t) {
            case SELLLIMIT:
                return ask_bookreceivers;
            case BUYLIMIT:
                return bid_bookreceivers;
        }
        return null;
    }

    public void addBookReceiver(OrderType t, BookReceiver br) {

        if (br == null) {
//            System.out.printf("Br is null\n");
        } else {
            //          System.out.printf("Br is not Nukk\n");
        }

        ArrayList<BookReceiver> bookreceivers;
        bookreceivers = selectBookReceiver(t);
        if (bookreceivers == null) {
//            System.out.printf("null in bookreceivers\n");
        }
        bookreceivers.add(br);
    }

    void updateBookReceivers(OrderType t) {
        ArrayList<BookReceiver> bookreceivers;
        bookreceivers = selectBookReceiver(t);

        Iterator<BookReceiver> i = bookreceivers.iterator();
        while (i.hasNext()) {
            i.next().UpdateOrderBook();
        }

    }

    // Here we store the list of quote receivers
    private final List<QuoteReceiver> qrlist;

    /**
     *
     * @param qr
     */
    public void addQuoteReceiver(QuoteReceiver qr) {
        qrlist.add(qr);
    }

    // send updated quotes to all quote receivers
    private void updateQuoteReceivers(Quote q) {
        Iterator<QuoteReceiver> i = qrlist.iterator();
        while (i.hasNext()) {
            i.next().UpdateQuote(q);
        }
    }

    /**
     *
     * @param stock
     * @param type
     * @param depth
     * @return
     */
    public ArrayList<Order> getOrderBook(Stock stock, OrderType type, int depth) {

        SortedSet<Order> book = stock.order_books.get(type);
        if (book == null) {
            return null;
        }
        ArrayList<Order> ret;
        synchronized (executor) {

            ret = new ArrayList<>();

            Iterator<Order> it = book.iterator();

            for (int i = 0; i < depth && it.hasNext(); i++) {
                Order o = it.next();
                //   System.out.print(o.volume);
                if (o.volume <= 0) {
                    System.out.printf("Volume < 0\n");
                    System.exit(0);
                }
                ret.add(o);
            }
            // System.out.println();
        }
        return ret;
    }

    public ArrayList<Order> getOrderBook(OrderType type, int depth) {
        return getOrderBook(getDefaultStock(), type, depth);
    }

    /**
     *
     * @return
     */
    public Quote getLastQuoete() {
        Stock stock = getDefaultStock();

        if (stock.quoteHistory.isEmpty()) {
            return null;
        }

        return stock.quoteHistory.last();
    }

    private void transferMoneyAndShares(Account src, Account dst, double money, double shares) {
//        src.money -= money;
        src.addMoney(-money);

        //      dst.money += money;
        dst.addMoney(money);
        //    src.shares -= shares;
        src.addShares(-shares);
        //    dst.shares += shares;

        src.addShares(shares);

    }

    public boolean cancelOrder(Stock stock, double account_id, long order_id) {
        Account a = accounts.get(account_id);
        if (a == null) {
            return false;
        }

        boolean ret = false;

        Order o;

//        System.out.printf("Getting executor %d\n", Thread.currentThread().getId());
        synchronized (executor) {
//            System.out.printf("Have executor %d\n", Thread.currentThread().getId());
            o = a.orders.get(order_id);

            //   System.out.print("The Order:"+o.limit+"\n");
            if (o != null) {
                SortedSet ob = stock.order_books.get(o.type);

                boolean rc = ob.remove(o);

                a.orders.remove(o.id);
                a.update(o);
                ret = true;
            }

        }
        if (ret) {
            this.updateBookReceivers(o.type);
        }
//        System.out.printf("Levave executor %d\n", Thread.currentThread().getId());
        return ret;
    }

    public boolean cancelOrder(double account_id, long order_id) {
        return cancelOrder(getDefaultStock(), account_id, order_id);
    }

    Random random;

    public int randNextInt() {
        return random.nextInt();

    }

    public int randNextInt(int bounds) {

        return random.nextInt(bounds);

    }

    public double randNextDouble() {
        return random.nextDouble();

    }

    /**
     *
     * @param o
     */
//    long nextQuoteId = 0;
    public double fairValue = 0;

    private void removeOrderIfExecuted(Stock stock, Order o) {
        if (o.getAccount().getOwner().getName().equals("Tobias0")) {
//            System.out.printf("Tobias 0 test\n");
        }

        if (o.volume != 0) {

            if (o.getAccount().getOwner().getName().equals("Tobias0")) {
//                System.out.printf("Patially remove tobias\n");
            }

            o.status = OrderStatus.PARTIALLY_EXECUTED;
            o.account.update(o);
            return;
        }

        if (o.getAccount().getOwner().getName().equals("Tobias0")) {
//            System.out.printf("Fully remove tobias\n");
        }

        o.account.orders.remove(o.id);

        SortedSet book = stock.order_books.get(o.type);

        book.remove(book.first());

        o.status = OrderStatus.CLOSED;
        o.account.update(o);

    }

    private void removeOrderIfExecuted(Order o) {
        removeOrderIfExecuted(getDefaultStock(), o);
    }

    void checkSLOrders(Stock stock, double price) {
        SortedSet<Order> sl = stock.order_books.get(OrderType.STOPLOSS);
        SortedSet<Order> ask = stock.order_books.get(OrderType.SELLLIMIT);

        if (sl.isEmpty()) {
            return;
        }

        Order s = sl.first();
        if (price <= s.limit) {
            sl.remove(s);

            s.type = OrderType.SELL;
            addOrderToBook(s);

//            System.out.printf("Stoploss hit %f %f\n", s.volume, s.limit);
        }
    }

    void checkSLOrders(double price) {
        checkSLOrders(getDefaultStock(), price);
    }

    public void executeUnlimitedOrders() {

    }

    private void finishTrade(Order b, Order a, double price, double volume) {
        // Transfer money and shares
        transferMoneyAndShares(b.account, a.account, volume * price, -volume);

        // Update volume
        b.volume -= volume;
        a.volume -= volume;

        b.cost += price * volume;
        a.cost += price * volume;

        removeOrderIfExecuted(a);
        removeOrderIfExecuted(b);
    }

    void addQuoteToHistory(Quote q) {
        if (statistics.heigh == null) {
            statistics.heigh = q.price;
        } else if (statistics.heigh < q.price) {
            statistics.heigh = q.price;
        }
        if (statistics.low == null) {
            statistics.low = q.price;
        } else if (statistics.low > q.price) {
            statistics.low = q.price;
        }

        Stock stock = getDefaultStock();
        stock.quoteHistory.add(q);
        updateOHLCData(stock,q);
        updateQuoteReceivers(q);
    }

    /**
     *
     */
    public void executeOrders(Stock stock) {

//        System.out.printf("Exec Orders\n");
        SortedSet<Order> bid = stock.order_books.get(OrderType.BUYLIMIT);
        SortedSet<Order> ask = stock.order_books.get(OrderType.SELLLIMIT);

        SortedSet<Order> ul_buy = stock.order_books.get(OrderType.BUY);
        SortedSet<Order> ul_sell = stock.order_books.get(OrderType.SELL);

        double volume_total = 0;
        double money_total = 0;

        while (true) {

            // Match unlimited sell orders against unlimited buy orders
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

                //System.out.printf("Cannot match two unlimited orders!\n");
                //System.exit(0);
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

            // Match limited against limited orders
            if (bid.isEmpty() || ask.isEmpty()) {
                break;
            }

            Order b = bid.first();
            Order a = ask.first();

            if (b.limit < a.limit) {
                break;
            }

            // There is a match, calculate price and volume
            double price = b.id < a.id ? b.limit : a.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;

            finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;

//            num_trades++;
            statistics.trades++;

            this.checkSLOrders(price);

        }

        if (volume_total == 0) {
            return;
        }
        Quote q = new Quote();
        q.price = money_total / volume_total;
        q.volume = volume_total;
        q.time = timer.currentTimeMillis();

        addQuoteToHistory(q);

        //this.quoteHistory.add(q);
        //this.updateOHLCData(q);
        //this.updateQuoteReceivers(q);
    }

    long buy_orders = 0;
    long sell_orders = 0;

    private void addOrderToBook(Stock stock, Order o) {
        stock.order_books.get(o.type).add(o);
        switch (o.type) {
            case BUY:
            case BUYLIMIT:
                buy_orders++;
                break;
            case SELL:
            case SELLLIMIT:
                sell_orders++;
                break;

        }
//        System.out.printf("B/S  %d/%d Failed B/S: %d/%d\n", buy_orders, sell_orders,buy_failed,sell_failed);
    }

    private void addOrderToBook(Order o) {
        addOrderToBook(getDefaultStock(), o);
    }

    long buy_failed = 0;
    long sell_failed = 0;

    /**
     *
     * @param account_id
     * @param type
     * @param volume
     * @param limit
     * @return order_id
     */
    public long createOrder(double account_id,
            String stocksymbol, OrderType type, double volume, double limit) {

        Stock stock = this.getStock(stocksymbol);

        Account a = accounts.get(account_id);
        if (a == null) {
            return -1;
        }

        Order o = new Order(order_id_generator.getNext(),
                timer.currentTimeMillis(),
                a, type, roundShares(volume), roundMoney(limit));

        o.stock = stock;

        if (o.volume <= 0 || o.limit <= 0) {

            switch (o.type) {
                case SELL:
                case SELLLIMIT:
                    sell_failed++;
                    break;
                case BUY:
                case BUYLIMIT:
                    buy_failed++;
                    break;
            }

            return -1;
        }

        synchronized (executor) {

            //num_orders++;
            statistics.orders++;

            addOrderToBook(o);
            a.orders.put(o.id, o);
            a.update(o);

            executeOrders(getDefaultStock());
            updateBookReceivers(OrderType.SELLLIMIT);
            updateBookReceivers(OrderType.BUYLIMIT);

//            System.out.printf("Order to Queeue %s %f %f\n",o.type.toString(),o.volume,o.limit);
//            order_queue.add(o);
//            executor.notify();
        }
//       a.update(o);
        return o.getID();
    }

    public double getBestLimit(Stock stock, OrderType type) {
        Order o = stock.order_books.get(type).first();
        if (o == null) {
            return -1;
        }
        return o.limit;
    }

    public double getBestLimit(OrderType type) {
        return getBestLimit(getDefaultStock(), type);
    }

    public int getNumberOfOpenOrders(double account_id) {
        Account a = accounts.get(account_id);
        if (a == null) {
            return 0;
        }
        return a.orders.size();
    }

    public Account getAccount(double account_id) {
        return accounts.get(account_id);
    }

    /*public AccountData getAccountData(double account_id_generator) {
        tradelock.lock();
        Account a = accounts.get(account_id_generator);
        tradelock.unlock();
        if (a == null) {
            return null;
        }

        AccountData ad = new AccountData();
        ad.id = account_id_generator;
        ad.money = a.money;
        ad.shares = a.shares;

        ad.orders = new ArrayList<>();
        ad.orders.iterator();

        a.orders.values();
        Set s = a.orders.keySet();
        Iterator it = s.iterator();

        while (it.hasNext()) {
            long x = (long) it.next();

            Order o = a.orders.get(x);

            OrderData od = new OrderData();
            od.id = o.id;
            od.limit = o.limit;
            od.volume = o.volume;
            ad.orders.add(od);
        }

        //System.exit(0);
        //a.orders.keySet();
        //KeySet ks = a.orders.keySet();
        return ad;
    }
     */
 /*   public ArrayList<OrderData> getOpenOrders(double account_id_generator) {

        Account a = accounts.get(account_id_generator);
        if (a == null) {
            return null;
        }

        ArrayList<OrderData> al = new ArrayList();

        Iterator it = a.orders.entrySet().iterator();
        while (it.hasNext()) {
            Order o = (Order) it.next();
            OrderData od = new OrderData();
            od.limit = o.limit;
            od.volume = o.initial_volume;
            od.executed = o.initial_volume - o.volume;
            od.id = o.id;
            al.add(od);
        }

        return al;
    }
     */
}
