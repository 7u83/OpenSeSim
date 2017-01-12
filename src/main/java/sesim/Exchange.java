package sesim;

import java.util.*;
import java.util.concurrent.*;

import sesim.Order_old.OrderStatus;
import sesim.Order_old.OrderType_old;

/**
 *
 * @author tube
 */
public class Exchange extends Thread {

    public enum OrderType {
        BID, ASK
    }

    IDGenerator account_id = new IDGenerator();

    private class Account implements Comparable {

        protected double id;
        protected double shares;
        protected double money;

        protected HashMap<Long, Order> orders;

        @Override
        public int compareTo(Object a) {
            Account account = (Account) a;
            return this.id - account.id < 0 ? -1 : 1;
        }

        Account(double money, double shares) {
            id = (Math.random() + (account_id.getNext()));
            orders = new HashMap();
            this.money = money;
            this.shares = shares;
        }
    }

    //private TreeSet<Account> accounts = new TreeSet<>();
    HashMap<Double, Account> accounts = new HashMap<>();

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
            
            if(left.id<right.id)
                return -1;
            if(left.id>right.id)
                return 1;
            
            return 0;
            

//            return left.id < right.id ? -1 : 1;
        }

    }

    //TreeSet <Order> bid_bbook = new TreeSet <> (new OrderComperator(OrderType.BID) );
    //TreeSet <Order> ask_dbook = new TreeSet <> (new OrderComperator(OrderType.BID) );
    HashMap<OrderType, TreeSet<Order>> order_books = new HashMap();

    IDGenerator order_id = new IDGenerator();

    private class Order {

        OrderType type;
        double limit;
        double volume;
        double initial_volume;
        long id;
        long created;
        Account account;

        Order(Account account, OrderType type, double volume, double limit) {
            id = order_id.getNext();
            this.account = account;
            this.type = type;
            this.limit = limit;
            this.volume = volume;
            this.initial_volume = volume;
            this.created = System.currentTimeMillis();
        }
    }

    /**
     * Histrory of quotes
     */
    public TreeSet<Quote> quoteHistory = new TreeSet<>();

    /**
     * Constructor
     */
    public Exchange() {
        this.ask = new TreeSet<>();
        this.bid = new TreeSet<>();
        this.qrlist = new ArrayList<>();

        // Create order books
        for (OrderType type : OrderType.values()) {
            order_books.put(type, new TreeSet(new OrderComparator(type)));
        }

    }

    class BidBook extends TreeSet {

        TreeSet t = new TreeSet();

        boolean hallo() {
            t.comparator();
            return true;
        }
    }

    /**
     *
     * @return
     */
    public static long getCurrentTimeSeconds() {
        long ct = System.currentTimeMillis();
        return ct / 1000;
    }

    public SortedSet<Quote> getQuoteHistory(long start) {

        Quote s = new Quote();
        s.time = start * 1000;
        s.id = 0;

        TreeSet<Quote> result = new TreeSet<>();
        result.addAll(this.quoteHistory.tailSet(s));

        return result;

    }

    /* public SortedSet<Quote> getQuoteHistory(int seconds) {
        Quote last = quoteHistory.last();
        return this.getQuoteHistory(seconds, last.time);
    }
     */
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
        try {
            sleep(10);
        } catch (InterruptedException e) {
            System.out.println("I was Interrupted");
        }

    }

    // Here we store the list of quote receivers
    private final ArrayList<QuoteReceiver> qrlist;

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

    public TreeSet<Order_old> bid;
    public TreeSet<Order_old> ask;

    private Locker tradelock = new Locker();

    /*
    private final Semaphore available = new Semaphore(1, true);

    private void Lock() {
        try {
            available.acquire();
        } catch (InterruptedException s) {
            System.out.println("Interrupted\n");
        }

    }

    private void Unlock() {
        available.release();
    }
     */
    private TreeSet<Order_old> selectOrderBook(OrderType_old t) {

        switch (t) {
            case bid:
                return this.bid;
            case ask:
                return this.ask;
        }
        return null;

    }

    public class OrderBookItem {

        public long id;
        public double limit;
        public double volume;
    }

    public ArrayList<OrderBookItem> getOrderBook(OrderType type, int depth) {

        TreeSet<Order> book = order_books.get(type);
        if (book == null) {
            return null;
        }

        ArrayList<OrderBookItem> ret = new ArrayList<>();

        Iterator<Order> it = book.iterator();

        for (int i = 0; i < depth && it.hasNext(); i++) {

            Order o = it.next();
            OrderBookItem n = new OrderBookItem();
            n.id = o.id;
            n.limit = o.limit;
            n.volume = o.volume;

            ret.add(n);
            //System.out.print("Order_old" + o.limit);
            //System.out.println();
        }
        return ret;

    }

    public void print_current() {

        Order_old b;
        Order_old a;

        //String BID;
        if (bid.isEmpty()) {
            b = new BuyOrder();
            b.limit = -1;
            b.volume = 0;
        } else {
            b = bid.first();
        }

        if (ask.isEmpty()) {
            a = new SellOrder();
            a.limit = -1;
            a.volume = 0;

        } else {
            a = ask.first();
        }

        Logger.info(String.format("BID: %s(%s)  LAST: %.2f(%d)  ASK: %s(%s)\n",
                b.format_limit(), b.format_volume(),
                lastprice, lastsvolume,
                a.format_limit(), a.format_volume())
        );

    }

    public void transferMoney(Account_old src, Account_old dst, double money) {
        src.money -= money;
        dst.money += money;

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
          TreeSet ob =order_books.get(o.type);
          
          System.out.print("We have the orderbook"+ob.size()+"\n");
          
          System.out.print("Want to remove:"+o.limit+" "+o.volume+" "+o.id+"\n");
          
         
          
          boolean rc = ob.remove(o);
          

            System.out.print("My first rc = :" + rc);
            a.orders.remove(o.id);
            ret = true;
        }

        tradelock.unlock();
        this.updateBookReceivers(OrderType.BID);

        return ret;
    }

    /**
     *
     * @param o
     */
    public void cancelOrder_old(Order_old o) {
        tradelock.lock();
        TreeSet<Order_old> book = this.selectOrderBook(o.type);
        book.remove(o);
        /*        this.updateBookReceivers(o.type);
        o.account.pending.remove(o);
        o.status = OrderStatus.canceled;
        tradelock.unlock();
         */

    }

    /**
     * Transfer shares from one account to another account
     *
     * @param src source account
     * @param dst destination account
     * @param volumen number of shares
     * @param price price
     */
    protected void transferShares(Account_old src, Account_old dst, long volume, double price) {
        dst.shares += volume;
        src.shares -= volume;
        dst.money -= price * volume;
        src.money += price * volume;
    }

    long nextQuoteId = 0;

    private void removeOrderIfExecuted(Order o) {
        if (o.volume != 0) {
            return;
        }
        o.account.orders.remove(o.id);
        order_books.get(o.type).pollFirst();

    }

    /**
     *
     */
    public void executeOrders() {

        TreeSet<Order> bid = order_books.get(OrderType.BID);
        TreeSet<Order> ask = order_books.get(OrderType.ASK);

        double volume_total = 0;
        double money_total = 0;

        while (!bid.isEmpty() && !ask.isEmpty()) {

            Order b = bid.first();
            Order a = ask.first();

            if (b.limit < a.limit) {
                System.out.print("No match\n");
                // no match, nothing to do
                return;
            }

            // There is a match, calculate price and volume
            double price = b.id < a.id ? b.limit : a.limit;
            double volume = b.volume >= a.volume ? a.volume : b.volume;

            // Transfer money and shares
            transferMoneyAndShares(b.account, a.account, volume * price, -volume);

            // Update volume
            b.volume -= volume;
            a.volume -= volume;

            volume_total += volume;
            money_total += price * volume;

            removeOrderIfExecuted(a);
            removeOrderIfExecuted(b);

        }

        Quote q = new Quote();
        q.price = money_total / volume_total;
        q.volume = volume_total;
        q.time = System.currentTimeMillis();

        System.out.print("Price" + q.price + "," + q.volume + "\n");

    }

    private void executeOrders_old() {

        while (!bid.isEmpty() && !ask.isEmpty()) {

            Order_old b = bid.first();
            Order_old a = ask.first();

            if (b.limit < a.limit) {
                // no match, nothing to do
                return;
            }

            if (a.volume == 0) {
                // This order is fully executed, remove 
                a.account.orderpending = false;
                a.status = OrderStatus.executed;

                a.account.pending.remove(a);

                ask.pollFirst();
//                this.updateBookReceivers(OrderType_old.ask);
                continue;
            }

            if (b.volume == 0) {
                // This order is fully executed, remove 
                b.account.orderpending = false;
                b.status = OrderStatus.executed;
                b.account.pending.remove(b);
                bid.pollFirst();
//                this.updateBookReceivers(OrderType_old.bid);
                continue;
            }

            if (b.limit >= a.limit) {
                double price;

                price = b.id < a.id ? b.limit : a.limit;

                /*         if (b.id < a.id) {
                    price = b.limit;
                } else {
                    price = a.limit;
                }
                 */
                long volume;

                if (b.volume >= a.volume) {
                    volume = a.volume;
                } else {
                    volume = b.volume;
                }

                transferShares(a.account, b.account, volume, price);

                //       b.account.Buy(a.account, volume, price);
                b.volume -= volume;
                a.volume -= volume;

                lastprice = price;
                lastsvolume = volume;

                Quote q = new Quote();

                q.volume = volume;
                q.price = price;
                q.time = System.currentTimeMillis();

                q.ask = a.limit;
                q.bid = b.limit;
                q.id = nextQuoteId++;

                this.updateQuoteReceivers(q);
//                this.updateBookReceivers(OrderType_old.bid);
//                this.updateBookReceivers(OrderType_old.ask);

                /*                System.out.print(
                        "Executed: "
                        + q.price
                        + " / "
                        + q.volume
                        + "\n"
                );
                 */
                quoteHistory.add(q);
                continue;

            }

            return;
        }
    }

    public void ExecuteOrder(BuyOrder o) {
        // SellOrder op = ASK.peek();

    }

    private boolean InitOrder(Order_old o) {
        double moneyNeeded = o.volume * o.limit;
        return true;
    }

    // Add an order to the orderbook
    private boolean addOrder(Order_old o) {
        boolean ret = false;
        switch (o.type) {
            case bid:

//                System.out.print("Exchange adding BID order \n");
                ret = bid.add(o);
                break;

            case ask:
//                System.out.print("Exchange adding ASK order \n");                
                ret = ask.add(o);
                break;
        }

        if (ret) {
//            this.updateBookReceivers(o.type);
        }
        return ret;
    }

    public Order_old SendOrder(Order_old o) {

        boolean rc = InitOrder(o);
        if (!rc) {
            return null;
        }

        tradelock.lock();
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        addOrder(o);
        o.account.pending.add(o);
        executeOrders_old();
        tradelock.unlock();

        return o;
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
        addOrderToBook(o);
        a.orders.put(o.id, o);

        tradelock.lock();
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

    public AccountData getAccountData(double account_id) {
        Account a = accounts.get(account_id);
        if (a == null) {
            return null;
        }

        AccountData ad = new AccountData();
        ad.id = account_id;
        ad.money = a.money;
        ad.shares = a.shares;

        ad.orders = new ArrayList<OrderData>();
        ad.orders.iterator();

        a.orders.values();
        Set s = a.orders.keySet();
        Iterator it = s.iterator();
        System.out.print("Keys list" + s.size() + "\n");
        while (it.hasNext()) {
            long x = (long) it.next();
            System.out.print("X" + x + "\n");
            Order o = a.orders.get(x);
            System.out.print("oGot: " + o.limit + " " + o.volume + "\n");
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


    /*
    public void SendOrder(BuyOrder o) {
        //System.out.println("EX Buyorder");
        Lock();
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        BID.add(o);
               
        Unlock();
        Lock();
//        executeOrders_old();
        Unlock();

    }
     */
 /*
	 * public void SendOrder(Order_old o){
	 * 
	 * 
	 * if ( o.getClass() == BuyOrder.class){ BID.add((BuyOrder)o); }
	 * 
	 * if ( o.getClass() == SellOrder.class){ ASK.add((SellOrder)o); }
	 * 
	 * }
     */
    public double getlastprice() {
        /*
		 * SellOrder so = new SellOrder(); so.limit=1000.0; so.volume=500;
		 * SendOrder(so);
		 * 
		 * BuyOrder bo = new BuyOrder(); bo.limit=1001.0; bo.volume=300;
		 * SendOrder(bo);
         */

        return lastprice;
    }

    /*  public double sendOrder(Account_old o) {
        return 0.7;
    }
     */
    /**
     *
     */
    @Override
    public void run() {
        while (true) {
            try {
                sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("I was Interrupted");
            }
            print_current();

        }
    }

}
