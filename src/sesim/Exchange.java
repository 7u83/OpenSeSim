package sesim;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONObject;

/**
 * @desc Echchange class
 * @author 7u83
 */
public class Exchange {

 //   ConcurrentLinkedQueue<Order> order_queue = new ConcurrentLinkedQueue();

    private float money_df = 10000;
    private int money_decimals = 2;
    DecimalFormat money_formatter;

    /**
     * Set the number of decimals used with money
     *
     * @param n number of decimals
     */
    public void setMoneyDecimals(int n) {
        money_df = (float) Math.pow(10, n);
        money_decimals = n;
        money_formatter = getFormatter(n);
    }

    private float shares_df = 1;
    private float shares_decimals = 0;
    private DecimalFormat shares_formatter;

    /**
     * Set the number of decimals for shares
     *
     * @param n number of decimals
     */
    public void setSharesDecimals(int n) {
        shares_df = (float) Math.pow(10, n);
        shares_decimals = n;
        shares_formatter = getFormatter(n);
    }

    public float roundToDecimals(double val, double f) {
        return (float) ((Math.floor(val * f) / f));
    }

    public float roundShares(double shares) {
        return roundToDecimals(shares, shares_df);
    }

    public float roundMoney(double money) {
        return roundToDecimals(money, money_df);
    }

    public DecimalFormat getFormatter(int n) {
        //      DecimalFormat formatter;
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


    public Scheduler timer; 

    //public ArrayList<AutoTraderInterface> traders_old;
    /**
     *
     */
    public interface AccountListener {

        public void accountUpdated(Account a, Order o);
    }

    public interface OrderListener {

        public void orderUpdated(Order o);
    }

    HashMap<Integer, OHLCData> ohlc_data = new HashMap<>();

    private OHLCData buildOHLCData(int timeFrame) {
        OHLCData data = new OHLCData(timeFrame);
        if (this.quoteHistory == null) {
            return data;
        }

        //      System.out.printf("--- build quote hostory for tf: %d\n", timeFrame);
        for (Quote q : quoteHistory) {
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        }

        return data;
    }

    public OHLCData getOHLCdata(Integer timeFrame) {
        OHLCData data; //=new OHLCData(timeFrame);
        data = ohlc_data.get(timeFrame);
        if (data == null) {

            synchronized (executor) {
                data = this.buildOHLCData(timeFrame);
                ohlc_data.put(timeFrame, data);
            }
        }

        return data;

    }

    void updateOHLCData(Quote q) {
        for (OHLCData data : ohlc_data.values()) {
            data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        }
    }



    class OrderComparator implements Comparator<Order> {

        byte type;

        OrderComparator(byte type) {
            this.type = type;
        }

        @Override
        public int compare(Order left, Order right) {
            float d;
            switch (this.type) {
                case Order.BUYLIMIT:
                case Order.STOPBUY:
                case Order.BUY:
                    d = right.limit - left.limit;
                    break;
                case Order.SELLLIMIT:
                case Order.STOPLOSS:
                case Order.SELL:
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

    class zzOrderComparator implements Comparator<Order> {

        byte type;

        zzOrderComparator(byte type) {
            this.type = type;
        }

        @Override
        public int compare(Order left, Order right) {
            float d;
            switch (this.type) {
                case Order.BUYLIMIT:
                case Order.STOPBUY:
                case Order.BUY:
                    d = right.limit - left.limit;
                    break;
                case Order.SELLLIMIT:
                case Order.STOPLOSS:
                case Order.SELL:
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

    //HashMap<Byte, SortedSet<Order>> order_books;
    IDGenerator order_id = new IDGenerator();



    public static class CompOrderBookEntry implements OrderBookEntry {

        public float limit;
        public float volume;

        public CompOrderBookEntry() {

        }

        public CompOrderBookEntry(Order oe) {

            this.volume = oe.volume;
            this.limit = oe.limit;
        }

        public CompOrderBookEntry(OrderBookEntry oe) {

            this.volume = oe.getVolume();
            this.limit = oe.getLimit();
        }

        public float getVolume() {
            return volume;
        }

        public float getLimit() {
            return limit;
        }

        public String getOwnerName() {
            return "";
        }

        /*     public OrderBookEntry(Order o) {
            volume = o.volume;
            limit = o.limit;
            account = o.account;
        }*/

 /*     @Override
        public int compareTo(OrderBookEntry o) {

            if (this.limit < o.limit) {
                return -1;
            }
            if (this.limit < o.limit) {
                return 1;
            }
            return 0;

        }*/
        @Override
        public void addVolume(float v) {
            volume += v;
        }

        @Override
        public float getStop() {
            return -1;
        }
    }

   

    /**
     * Histrory of quotes
     */
    public List<Quote> quoteHistory; // = new TreeSet<>();

    SortedSet bidBook;
    SortedSet askBook;
    SortedSet ulBidBook;
    SortedSet ulAskBook;
    SortedSet stopBuyBook;
    SortedSet stopSellBook;

    //       random = new Random(12);
    public SplittableRandom random;

    //random = new SplittableRandom(19);
    final void initExchange() {
        //   quoteReceiverList = (new CopyOnWriteArrayList<>());

        this.quoteReceiverList.clear();
        this.askBookListeners.clear();
        this.bidBookListeners.clear();

        buy_orders = 0;
        sell_orders = 0;
        timer = new Scheduler();         //  timer = new Scheduler();

   //     random = new SplittableRandom();

        quoteHistory = new ArrayList();
        statistics = new Statistics();

        this.ohlc_data = new HashMap();

        // Create order books
        //   order_books = new HashMap();
        bidBook = new ConcurrentSkipListSet(new OrderComparator(Order.BUYLIMIT));
        askBook = new ConcurrentSkipListSet(new OrderComparator(Order.SELLLIMIT));
        ulBidBook = new ConcurrentSkipListSet(new OrderComparator(Order.BUY));
        ulAskBook = new ConcurrentSkipListSet(new OrderComparator(Order.SELL));
        stopSellBook = new ConcurrentSkipListSet(new OrderComparator(Order.STOPLOSS));
        stopBuyBook = new ConcurrentSkipListSet(new OrderComparator(Order.BUY));

        /*   order_books.put(Order.BUYLIMIT, new ConcurrentSkipListSet(new OrderComparator(Order.BUYLIMIT)));
        order_books.put(Order.SELLLIMIT, new ConcurrentSkipListSet(new OrderComparator(Order.SELLLIMIT)));
        order_books.put(Order.BUY, new ConcurrentSkipListSet(new OrderComparator(Order.BUY)));
        order_books.put(Order.SELL, new ConcurrentSkipListSet(new OrderComparator(Order.SELL)));
        order_books.put(Order.STOPLOSS, new ConcurrentSkipListSet(new OrderComparator(Order.STOPLOSS)));
         */
 /*for (Order type : Order.values()) {
//            order_books.put(type, new TreeSet(new OrderComparator(type)));
            order_books.put(type, new ConcurrentSkipListSet(new OrderComparator(type)));
        }*/
    }

    /**
     * Constructor
     */
    public Exchange() {
        quoteReceiverList = (new CopyOnWriteArrayList<>());

        initExchange();
        //       executor.start();

    }

    public class Statistics {

        public long trades;
        public long orders;
        public Float heigh;
        public Float low;

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

    public Statistics getStatistics() {
        return statistics;
    }

    class Executor extends Thread {

        @Override
        public void run() {
            /*          synchronized (this) {
                try {
                    while (true) {

                        this.wait();

                        Order o;
                        while (null != (o = order_queue.poll())) {
                            addOrderToBook(o);
                            Account a = o.account;
                            a.orders.put(o.id, o);
                            a.update(o);
                            executeOrders();
                        }

                        updateBookReceivers(Order.SELLLIMIT);
                        updateBookReceivers(Order.BUYLIMIT);
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(Exchange.class.getName()).log(Level.SEVERE, null, ex);
                }

            }*/
        }

    }

    final Executor executor = new Executor();

    /**
     * Start the exchange
     */
    /*    public void start() {
        timer.start();

    }*/
    public void reset() {
        initExchange();
    }

    /*   public void terminate() {
        timer.terminate();
    }*/
    public void initLastQuote() {
        Quote q = new Quote();
        q.price = this.fairValue;
        q.volume = 0;
        q.ask = q.price;
        q.bid = q.price;
        q.ask_volume = 0;
        q.bid_volume = 0;
        q.time = timer.getCurrentTimeMillis();
        quoteHistory.add(q);
        this.updateQuoteReceivers(q);
    }

    public float getLastPrice() {
        Quote q = this.getLastQuoete();
        if (q == null) {
            System.out.printf("get last quote failed\n");
            return 0f;
        }
        return q.price;
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
 /*   public SortedSet<Quote> getQuoteHistory(long start) {

        Quote s = new Quote();
        s.time = start * 1000;
        s.id = 0;

        TreeSet<Quote> result = new TreeSet<>();
        result.addAll(this.quoteHistory.tailSet(s));

        return result;

    }*/
    public final String CFG_MONEY_DECIMALS = "money_decimals";
    public final String CFG_SHARES_DECIMALS = "shares_decimals";
    public final String CFG_AUTO_INITIAL_PRICE = "auto_initial_price";
    public final String CFG_INITIAL_PRICE = "initial_price";

    public void putConfig(JSONObject cfg) {
        try {
            this.setMoneyDecimals(cfg.getInt(CFG_MONEY_DECIMALS));
            this.setSharesDecimals(cfg.getInt(CFG_SHARES_DECIMALS));
        } catch (Exception e) {

        }

    }

    public Float getBestPrice() {
        //      System.out.printf("Get BP\n");
        SortedSet<Order> bid = bidBook;
        SortedSet<Order> ask = askBook;

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
            //     System.out.printf("aaaaa bbbbb %f %f \n", a.limit, b.limit);
            // if there is no last quote calculate from bid and ask
            //if (lq == null) {
            float rc = (bid.first().limit + ask.first().limit) / 2.0f;
            //    System.out.printf("RCRC2.0: %f\n", rc);
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

    public Quote getBestPrice_0() {

        synchronized (executor) {
            SortedSet<Order> bid = bidBook;
            SortedSet<Order> ask = askBook;

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
                    q.price = (bid.first().limit + ask.first().limit) / 2.0f;
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

    // Class to describe an executed order
    // QuoteReceiver has to be implemented by objects that wants 
    // to receive quote updates  	
    public interface QuoteReceiver {

        void UpdateQuote(Quote q);
    }

    /**
     * Bookreceiver Interface
     */
    public interface BookListener {

        void UpdateOrderBook();
    }

    final private Set<BookListener> askBookListeners = ConcurrentHashMap.newKeySet();
    final private Set<BookListener> bidBookListeners = ConcurrentHashMap.newKeySet();
    final private Set<BookListener> ulAskBookListeners = ConcurrentHashMap.newKeySet();
    final private Set<BookListener> ulBidBookListeners = ConcurrentHashMap.newKeySet();
    final private Set<BookListener> stopBuyBookListeners = ConcurrentHashMap.newKeySet();
    final private Set<BookListener> stopSellBookListeners = ConcurrentHashMap.newKeySet();

    private Set<BookListener> selectBookReceiver(byte t) {
        switch (t) {
            case Order.SELLLIMIT:
                return askBookListeners;
            case Order.BUYLIMIT:
                return bidBookListeners;
            case Order.SELL:
                return ulAskBookListeners;
            case Order.BUY:
                return ulBidBookListeners;
            case Order.BUYSTOP:
                return stopBuyBookListeners;
            case Order.SELLSTOP:
                return stopSellBookListeners;

        }
        return null;
    }

    public void addBookListener(byte type, BookListener bl) {
        sesim.Logger.debug("Add book listener %s", bl.toString());
        if (bl == null) {
            return;
        }

        Set<BookListener> booklisteners = selectBookReceiver(type);
        if (booklisteners == null) {
            return;
        }
        booklisteners.add(bl);
    }

    public void removeBookListener(BookListener bl) {
        sesim.Logger.debug("Remove book listener %s", bl.toString());
        askBookListeners.remove(bl);
        bidBookListeners.remove(bl);
        ulAskBookListeners.remove(bl);
        ulBidBookListeners.remove(bl);
        stopBuyBookListeners.remove(bl);
        stopSellBookListeners.remove(bl);
    }

    void updateBookReceivers(byte t) {
        Set<BookListener> booklisteners;
        booklisteners = selectBookReceiver(t);

        Iterator<BookListener> i = booklisteners.iterator();
        while (i.hasNext()) {
            i.next().UpdateOrderBook();
        }

    }

    // Here we store the list of quote receivers
    private final List<QuoteReceiver> quoteReceiverList;

    /**
     *
     * @param qr
     */
    public void addQuoteReceiver(QuoteReceiver qr) {
        quoteReceiverList.add(qr);
    }

    // send updated quotes to all quote receivers
    private void updateQuoteReceivers(Quote q) {
        Iterator<QuoteReceiver> i = quoteReceiverList.iterator();
        while (i.hasNext()) {
            i.next().UpdateQuote(q);
        }
    }

    /**
     * Returns a raw snapshot of the order book for the specified order type.
     * <p>
     * The method retrieves up to {@code depth} orders from the sorted order
     * book corresponding to the given {@link Order}. If the order book is empty
     * or not found, {@code null} is returned.
     * </p>
     *
     * @param type the type of orders to retrieve (e.g. BUY or SELL)
     * @param depth the maximum number of orders to include in the result
     * @return a list of up to {@code depth} {@link Order} objects, or
     * {@code null} if the order book does not exist
     */
    public ArrayList<Order> getRawOrderBook(byte type, int depth) {

        // Get the sorted order book for the specified type
        SortedSet<Order> book = getBook(type); //order_books.get(type);
        if (book == null) {
            return null;
        }
        ArrayList<Order> ret = new ArrayList<>();

        Iterator<Order> it = book.iterator();

        // Iterate through the orders up to the given depth
        for (int i = 0; i < depth && it.hasNext(); i++) {
            Order o = new Order(it.next());

            // Skip orders with non-positive volume
            if (o.volume <= 0) {
                continue;

            }
            ret.add(o);
        }
        return ret;
    }

    SortedSet<Order> getBook(byte type) {
        switch (type & 0x07) {
            case Order.BUYLIMIT:
                return bidBook;
            case Order.SELLLIMIT:
                return askBook;
            case Order.BUY:
                return ulBidBook;
            case Order.SELL:
                return ulAskBook;
            case Order.SELLSTOP:
                return stopSellBook;
            case Order.BUYSTOP:
                return stopBuyBook;

            default:
                return null;
        }
    }

//    public ArrayList<OrderBookEntry> getCompressedOrderBook(Order type, int depth) {
    public TreeMap<Float, OrderBookEntry> getCompressedOrderBook(byte type, int depth) {

        TreeMap<Float, OrderBookEntry> map = new TreeMap<>();

        // Get the sorted order book for the specified type
        SortedSet<Order> book = getBook(type); //order_books.get(type);
        if (book == null) {
            return null;
        }

        Iterator<Order> it = book.iterator();

        // Iterate through the orders up to the given depth
        for (int i = 0; it.hasNext(); i++) {
            Order o = new Order(it.next());

            // Skip orders with non-positive volume
            if (o.volume <= 0) {
                continue;
            }

            OrderBookEntry oe = map.get(o.limit);
            if (oe == null) {
                map.put(o.limit, new CompOrderBookEntry(o));

            } else {
                oe.addVolume(o.volume);
                map.put(o.limit, oe);
            }

            if (map.size() >= depth) {
                break;
            }

        }


        /*
        book = this.getRawOrderBook(type, depth*4);
        int d=0;
        for (Order o : book) {
            OrderBookEntry oe = map.get(o.limit);
            if (oe == null) {
                map.put(o.limit, new OrderBookEntry(o));
                     d++;
            } else {
                oe.volume += o.volume;
                map.put(o.limit, oe);
            }
       
            if (d>=depth)
                break;

        }*/
        return map;

    }

    public Quote getLastQuoete() {
        if (this.quoteHistory.isEmpty()) {
            return null;
        }

        return quoteHistory.get(quoteHistory.size() - 1);
        //    return this.quoteHistory.last();
    }

    private void transferMoneyAndShares(Account buyer, Account seller, float money, float shares) {
        buyer.money -= money;
        seller.money += money;
        buyer.shares += shares;
        seller.shares -= shares;

    }

    public boolean cancelOrder(Account a, long order_id) {
        //Account a = accounts.get(account_id);
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
                SortedSet ob = getBook(o.type); //order_books.get(o.type);

                boolean rc = ob.remove(o);

                a.orders.remove(o.id);
                o.status = Order.CANCELED;
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
    //  long nextQuoteId = 0;
    public float fairValue = 0;

    private void removeOrderIfExecuted(Order o) {

        if (o.volume != 0 && o.status != Order.CLOSED) {

            o.status = Order.PARTIALLY_EXECUTED;
            o.account.update(o);
            return;
        }

        int n = o.account.orders.size();

        Order x = o.account.orders.remove(o.id);

        //SortedSet book = getBook(o.type); //order_books.get(o.type);
        //book.remove(book.first());
        if (o.isSell()) {
            if (o.hasLimit()) {
                askBook.remove(o);
            } else {
                ulAskBook.remove(o);
            }
        } else {
            if (o.hasLimit()) {
                bidBook.remove(o);
            } else {
                ulBidBook.remove(o);
            }
        }

        o.status = Order.CLOSED;
        o.account.update(o);

    }

    void checkSLOrders(float price) {
        SortedSet<Order> ss = stopSellBook; //order_books.get(Order.STOPLOSS);
        SortedSet<Order> sb = stopBuyBook; //order_books.get(Order.SELLLIMIT);

        while (!ss.isEmpty()) {
            Order s = ss.first();
            if (price > s.stop) {
                break;
            }
            ss.remove(s);
            if (s.hasLimit()) {
                askBook.add(s);
            } else {
                ulAskBook.add(s);
            }
        }

        while (!sb.isEmpty()) {
            Order s = sb.first();
            if (price > s.stop) {
                break;
            }
            sb.remove(s);
            if (s.hasLimit()) {
                bidBook.add(s);
            } else {
                ulBidBook.add(s);
            }
        }

    }

    public void executeUnlimitedOrders() {

    }

    private void finishTrade(Order buyer, Order seller, float price, float volume) {
        // Transfer money and shares
        transferMoneyAndShares(buyer.account, seller.account, volume * price, volume);
        statistics.trades++;
        // Update volume
        buyer.volume -= volume;
        seller.volume -= volume;

        buyer.cost += price * volume;
        seller.cost += price * volume;

        removeOrderIfExecuted(seller);
        removeOrderIfExecuted(buyer);
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

        //  statistics.trades++;
        //     System.out.printf("QUOTEHIST ADD: time:%d, vol:%f ID:%d\n", q.time,q.volume,q.id);
        quoteHistory.add(q);
        updateOHLCData(q);
        updateQuoteReceivers(q);
    }

    /**
     *
     */
    public void executeOrders() {

//        System.out.printf("Exec Orders\n");
        SortedSet<Order> bid = bidBook; //order_books.get(Order.BUYLIMIT);
        SortedSet<Order> ask = askBook; //order_books.get(Order.SELLLIMIT);

        SortedSet<Order> ul_buy = ulBidBook; //order_books.get(Order.BUY);
        SortedSet<Order> ul_sell = ulAskBook; //order_books.get(Order.SELL);

        float volume_total = 0;
        float money_total = 0;

        while (true) {

            // Match unlimited sell orders against unlimited buy orders
            while (!ul_sell.isEmpty() && !ul_buy.isEmpty()) {
                Order seller = ul_sell.first();
                Order buyer = ul_buy.first();

                float price = this.getBestPrice();
                /*if (price == null) {
                    break;
                }*/

                float volume = buyer.volume >= seller.volume ? seller.volume : buyer.volume;

                float bp = volume * price;
                float cashAvail = buyer.account.getCashAvailable();
                if (cashAvail < bp) {
                    volume = cashAvail / price;
                    volume = roundShares(volume);
                    buyer.status = Order.CLOSED;
                }

                finishTrade(buyer, seller, price, volume);
                volume_total += volume;
                money_total += price * volume;
                this.checkSLOrders(price);

                //System.out.printf("Cannot match two unlimited orders!\n");
                //System.exit(0);
            }

            while (!ul_buy.isEmpty() && !ask.isEmpty()) {
                Order seller = ask.first();
                Order buyer = ul_buy.first();
                float price = seller.limit;
                float volume = buyer.volume >= seller.volume ? seller.volume : buyer.volume;

                float bp = volume * price;
                float cashAvail = buyer.account.getCashAvailable();
                if (cashAvail < bp) {
                    volume = cashAvail / price;
                    volume = roundShares(volume);
                    buyer.status = Order.CLOSED;
                }

                finishTrade(buyer, seller, price, volume);
                volume_total += volume;
                money_total += price * volume;
                this.checkSLOrders(price);

            }

            // Match unlimited sell orders against limited buy orders
            while (!ul_sell.isEmpty() && !bid.isEmpty()) {
                Order buyer = bid.first();
                Order seller = ul_sell.first();
                float price = buyer.limit;
                float volume = buyer.volume >= seller.volume ? seller.volume : buyer.volume;
                finishTrade(buyer, seller, price, volume);
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
            float price = b.id < a.id ? b.limit : a.limit;
            float volume = b.volume >= a.volume ? a.volume : b.volume;

            finishTrade(b, a, price, volume);
            volume_total += volume;
            money_total += price * volume;

//            num_trades++;
            //      statistics.trades++;
            this.checkSLOrders(price);

        }

        if (volume_total == 0) {
            return;
        }
        Quote q = new Quote();
        q.price = money_total / volume_total;
        q.volume = volume_total;
        q.time = timer.getCurrentTimeMillis();

        addQuoteToHistory(q);

        //this.quoteHistory.add(q);
        //this.updateOHLCData(q);
        //this.updateQuoteReceivers(q);
    }

    long buy_orders = 0;
    long sell_orders = 0;

    private void addOrderToBook(Order o) {

        if (o.isSell()) {
            if (o.hasStop()) {
                stopSellBook.add(o);
            } else {
                if (o.hasLimit()) {
                    askBook.add(o);
                } else {
                    ulAskBook.add(o);
                }
            }
            sell_orders++;
            return;
        }

        if (o.hasStop()) {
            stopBuyBook.add(o);
        } else {
            if (o.hasLimit()) {
                bidBook.add(o);
            } else {
                ulBidBook.add(o);
            }
        }
        buy_orders++;

    }

    long buy_failed = 0;
    long sell_failed = 0;

    /**
     *
     * @param a
     * @param type
     * @param volume
     * @param limit
     * @return
     */
    public Order createOrder(Account a, byte type, float volume, float limit, float stop) {

        if (volume <= 0 || limit <= 0) {
            if ((type & Order.SELL) != 0) {
                sell_failed++;
            } else {
                buy_failed--;
            }

            return null;
        }

        Order o = new Order(this,a, type, volume, limit);
        o.stop = stop;

        synchronized (executor) {

            //num_orders++;
            statistics.orders++;

            addOrderToBook(o);

            a.orders.put(o.id, o);
            a.update(o);

            executeOrders();
            updateBookReceivers(Order.SELLLIMIT);
            updateBookReceivers(Order.BUYLIMIT);
            updateBookReceivers(Order.SELL);
            updateBookReceivers(Order.BUY);
            updateBookReceivers(Order.SELLSTOP);
            updateBookReceivers(Order.BUYSTOP);

        }

        return o;
    }

    public float getBestLimit(Order type) {
        //Order o = order_books.get(type).first();
        Order o = getBook(type.type).first();

        if (o == null) {
            return -1;
        }
        return o.limit;
    }

    public int getNumberOfOpenOrders(Account a) {
        //    Account a = accounts.get(account_id);
        if (a == null) {
            return 0;
        }
        return a.orders.size();
    }

 
}
