package sesim;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @desc Echchange class
 * @author 7u83
 */
public class Exchange {

    private double money_df = 10000;

    /**
     * Set the number of decimals used with money
     *
     * @param n number of decimals
     */
    public void setMoneyDecimals(int n) {
        money_df = Math.pow(10, n);
    }

    private double shares_df = 1;

    /**
     * Set the number of decimals for shares
     *
     * @param n number of decimals
     */
    public void setSharesDecimals(int n) {
        shares_df = Math.pow(10, n);
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

    /**
     * Definition of order types
     */
    public enum OrderType {
        BID, ASK
    }

    IDGenerator account_id = new IDGenerator();
    //public static Timer timer = new Timer();

    public Scheduler timer; // = new Scheduler();

    public ArrayList<AutoTrader> traders;

    /**
     * Implements a trading account
     */
    public class Account implements Comparable {

        private final double id;
        private double shares;
        private double money;

        private final HashMap<Long, Order> orders;

        @Override
        public int compareTo(Object a) {
            Account account = (Account) a;
            return this.id - account.id < 0 ? -1 : 1;
        }

        Account(double money, double shares) {
            id = (random.nextDouble() + (account_id.getNext()));
            orders = new HashMap();
            this.money = money;
            this.shares = shares;
        }

        public double getID() {
            return id;
        }

        public double getShares() {
            return shares;
        }

        public double getMoney() {
            return money;
        }

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

        Account a = new Account(money, shares);
        accounts.put(a.id, a);
        return a.id;
    }

    class OrderComparator implements Comparator<Order> {

        OrderType type;

        OrderComparator(OrderType type) {
            this.type = type;
        }

        @Override
        public int compare(Order left, Order right) {
            double d;
            switch (this.type) {
                case BID:
                    d = right.limit - left.limit;
                    break;
                case ASK:
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

//            return left.id < right.id ? -1 : 1;
        }

    }

    HashMap<OrderType, SortedSet<Order>> order_books;

    IDGenerator order_id = new IDGenerator();

    public class Order {

        OrderType type;
        private double limit;
        private double volume;
        private final double initial_volume;
        private long id;
        long created;
        private Account account;

        Order(Account account, OrderType type, double volume, double limit) {
            id = order_id.getNext();
            this.account = account;
            this.type = type;
            this.limit = roundMoney(limit);
            this.volume = roundShares(volume);
            this.initial_volume = this.volume;
            this.created = timer.currentTimeMillis();
        }

        public long getID() {
            return id;
        }

        public double getVolume() {
            return volume;
        }

        public double getLimit() {
            return limit;
        }

        public OrderType getType() {
            return type;
        }

        public double getExecuted() {
            return initial_volume - volume;
        }

        public double getInitialVolume() {
            return initial_volume;
        }

        public Account getAccount() {
            return account;
        }

    }

    /**
     * Histrory of quotes
     */
    public TreeSet<Quote> quoteHistory; // = new TreeSet<>();

    final void initExchange() {
        timer = new Scheduler();         //  timer = new Scheduler();
        random = new Random(12);

        quoteHistory = new TreeSet();
        accounts = new ConcurrentHashMap<>();

        traders = new ArrayList();

        num_trades = 0;

        // Create order books
        order_books = new HashMap();
        for (OrderType type : OrderType.values()) {
            order_books.put(type, new TreeSet(new OrderComparator(type)));
        }

    }

    /**
     * Constructor
     */
    public Exchange() {
        qrlist = (new CopyOnWriteArrayList<>());

        initExchange();

    }

    public class Statistics {

        public long trades;
        public long orders;
    };

    Statistics statistics;

    long num_trades = 0;
    long num_orders = 0;

    public Statistics getStatistics() {
        Statistics s = new Statistics();
        s.trades = num_trades;
        s.orders = num_orders;
        return s;

    }

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
    public SortedSet<Quote> getQuoteHistory(long start) {

        Quote s = new Quote();
        s.time = start * 1000;
        s.id = 0;

        TreeSet<Quote> result = new TreeSet<>();
        result.addAll(this.quoteHistory.tailSet(s));

        return result;

    }

    public Quote getCurrentPrice() {

        SortedSet<Order> bid = order_books.get(OrderType.BID);
        SortedSet<Order> ask = order_books.get(OrderType.ASK);

        Quote q = null;

        tradelock.lock();
        if (!bid.isEmpty() && !ask.isEmpty()) {
            q = new Quote();
            q.price = (bid.first().limit + ask.first().limit) / 2.0;

        }
        tradelock.unlock();

        if (q != null) {
            return q;
        }

        if (this.quoteHistory.isEmpty()) {

            return null;
        }

        q = this.quoteHistory.last();

        return q;

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
            case ASK:
                return ask_bookreceivers;
            case BID:
                return bid_bookreceivers;
        }
        return null;
    }

    public void addBookReceiver(OrderType t, BookReceiver br) {
        ArrayList<BookReceiver> bookreceivers;
        bookreceivers = selectBookReceiver(t);
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
    private List<QuoteReceiver> qrlist;

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

    // long time = 0;
    double theprice = 12.9;
    long orderid = 1;

    double lastprice = 100.0;
    long lastsvolume;

    private final Locker tradelock = new Locker();

    public ArrayList<Order> getOrderBook(OrderType type, int depth) {

        SortedSet<Order> book = order_books.get(type);
        if (book == null) {
            return null;
        }
        tradelock.lock();
        ArrayList<Order> ret = new ArrayList<>();

        Iterator<Order> it = book.iterator();

        for (int i = 0; i < depth && it.hasNext(); i++) {
            Order o = it.next();
            //   System.out.print(o.volume);
            if (o.volume <= 0) {
                System.exit(0);
            }
            ret.add(o);
        }
        // System.out.println();
        tradelock.unlock();
        return ret;
    }

    public Quote getLastQuoete() {
        return this.quoteHistory.last();
    }

    private void transferMoneyAndShares(Account src, Account dst, double money, double shares) {
        src.money -= money;
        dst.money += money;
        src.shares -= shares;
        dst.shares += shares;
    }

    public boolean cancelOrder(double account_id, long order_id) {
        Account a = accounts.get(account_id);
        if (a == null) {
            return false;
        }

        boolean ret = false;

        tradelock.lock();
        Order o = a.orders.get(order_id);

        //   System.out.print("The Order:"+o.limit+"\n");
        if (o != null) {
            SortedSet ob = order_books.get(o.type);

            boolean rc = ob.remove(o);

            a.orders.remove(o.id);
            ret = true;
        }

        tradelock.unlock();
        this.updateBookReceivers(OrderType.BID);

        return ret;
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
    long nextQuoteId = 0;

    public double fairValue = 0;

    private void removeOrderIfExecuted(Order o) {
        if (o.volume != 0) {
            return;
        }

        o.account.orders.remove(o.id);

        SortedSet book = order_books.get(o.type);

        book.remove(book.first());

    }

    /**
     *
     */
    public void executeOrders() {

        SortedSet<Order> bid = order_books.get(OrderType.BID);
        SortedSet<Order> ask = order_books.get(OrderType.ASK);

        double volume_total = 0;
        double money_total = 0;

        while (!bid.isEmpty() && !ask.isEmpty()) {

            Order b = bid.first();
            Order a = ask.first();

            //System.out.printf("In %f (%f) < %f (%f)\n",b.limit,b.volume,a.limit,a.volume);
            if (b.limit < a.limit) {
                break;
            }

            // There is a match, calculate price and volume
            double price = b.id < a.id ? b.limit : a.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;

            //System.out.printf("Price %f Vol %f\n", price,volume);
            // Transfer money and shares
            transferMoneyAndShares(b.account, a.account, volume * price, -volume);
//System.out.print("Transfer Shares was called with volume "+volume+"\n");
            // Update volume
            b.volume -= volume;
            a.volume -= volume;

            //System.out.printf("In %f (%f) < %f (%f)\n",b.limit,b.volume,a.limit,a.volume);
            volume_total += volume;
            money_total += price * volume;

            num_trades++;

            removeOrderIfExecuted(a);
            removeOrderIfExecuted(b);

        }
//System.out.print("Volume total is "+volume_total+"\n");
        if (volume_total == 0) {
            return;
        }
        Quote q = new Quote();
        q.price = money_total / volume_total;
        q.volume = volume_total;
        q.time = timer.currentTimeMillis();

//        System.out.print("There was a trade:"+q.price+"\n");
        this.quoteHistory.add(q);

        this.updateQuoteReceivers(q);

    }

    private void addOrderToBook(Order o) {
        order_books.get(o.type).add(o);

    }

    /**
     *
     * @param account_id
     * @param type
     * @param volume
     * @param limit
     * @return
     */
    public long createOrder(double account_id, OrderType type, double volume, double limit) {
        
        
        Account a = accounts.get(account_id);
        if (a == null) {
            return -1;
        }

        Order o = new Order(a, type, volume, limit);
        if (o.volume <= 0 || o.limit <= 0) {
            System.out.print("binweg\n");
            return -1;
        }
        tradelock.lock();
        num_orders++;

        addOrderToBook(o);
        a.orders.put(o.id, o);

        this.executeOrders();

        tradelock.unlock();
        this.updateBookReceivers(OrderType.ASK);
        this.updateBookReceivers(OrderType.BID);

        return o.id;
    }

    public double getBestLimit(OrderType type) {
        Order o = order_books.get(type).first();
        if (o == null) {
            return -1;
        }
        return o.limit;
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

    public AccountData getAccountData(double account_id) {
        tradelock.lock();
        Account a = accounts.get(account_id);
        tradelock.unlock();
        if (a == null) {
            return null;
        }

        AccountData ad = new AccountData();
        ad.id = account_id;
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

    public ArrayList<OrderData> getOpenOrders(double account_id) {

        Account a = accounts.get(account_id);
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

}
