package sesim;

import java.util.*;
import java.util.concurrent.*;

import sesim.Order.OrderStatus;
import sesim.Order.OrderType;

/**
 *
 * @author tube
 */
public class Exchange extends Thread {

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
            case ask:
                return ask_bookreceivers;
            case bid:
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

    public TreeSet<Order> bid;
    public TreeSet<Order> ask;

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
    private TreeSet<Order> selectOrderBook(OrderType t) {

        switch (t) {
            case bid:
                return this.bid;
            case ask:
                return this.ask;
        }
        return null;

    }

    public ArrayList<Order> getOrderBook(OrderType t, int depth) {

        TreeSet<Order> book = selectOrderBook(t);
        if (book == null) {
            return null;
        }

        ArrayList<Order> ret = new ArrayList<>();
        Iterator<Order> it = book.iterator();
        for (int i = 0; i < depth && it.hasNext(); i++) {
            Order o;
            o = it.next();
            ret.add(o);
            //System.out.print("Order" + o.limit);
            //System.out.println();
        }
        return ret;

    }

    public void print_current() {

        Order b;
        Order a;

        //String bid;
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

    public void transferMoney(Account src, Account dst, double money) {
        src.money -= money;
        dst.money += money;

    }

    /**
     *
     * @param o
     */
    public void cancelOrder(Order o) {
        tradelock.lock();
        TreeSet<Order> book = this.selectOrderBook(o.type);
        book.remove(o);
        this.updateBookReceivers(o.type);
        o.account.pending.remove(o);
        o.status = OrderStatus.canceled;
        tradelock.unlock();

    }

    /**
     * Transfer shares from one account to another account
     *
     * @param src source account
     * @param dst destination account
     * @param volumen number of shares
     * @param price price
     */
    protected void transferShares(Account src, Account dst, long volume, double price) {
        dst.shares += volume;
        src.shares -= volume;
        dst.money -= price * volume;
        src.money += price * volume;
    }

    long nextQuoteId = 0;

    /**
     *
     */
    protected void executeOrders() {

        while (!bid.isEmpty() && !ask.isEmpty()) {

            Order b = bid.first();
            Order a = ask.first();

            if (b.limit < a.limit) {
                // no match, nothing to do
                return;
            }

            double price = b.id < a.id ? b.limit : a.limit;
            long volume = b.volume>=a.volume ? a.volume : b.volume;
               


            if (a.volume == 0) {
                // This order is fully executed, remove 
                a.account.orderpending = false;
                a.status = OrderStatus.executed;

                a.account.pending.remove(a);

                ask.pollFirst();
                this.updateBookReceivers(OrderType.ask);
                continue;
            }

            if (b.volume == 0) {
                // This order is fully executed, remove 
                b.account.orderpending = false;
                b.status = OrderStatus.executed;
                b.account.pending.remove(b);
                bid.pollFirst();
                this.updateBookReceivers(OrderType.bid);
                continue;
            }

            if (b.limit >= a.limit) {

                price = b.id < a.id ? b.limit : a.limit;

                /*         if (b.id < a.id) {
                    price = b.limit;
                } else {
                    price = a.limit;
                }
                 */
               

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
                this.updateBookReceivers(OrderType.bid);
                this.updateBookReceivers(OrderType.ask);

  
                quoteHistory.add(q);
                continue;

            }

            return;
        }
    }

    private void executeOrdersX() {

        while (!bid.isEmpty() && !ask.isEmpty()) {

            Order b = bid.first();
            Order a = ask.first();

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
                this.updateBookReceivers(OrderType.ask);
                continue;
            }

            if (b.volume == 0) {
                // This order is fully executed, remove 
                b.account.orderpending = false;
                b.status = OrderStatus.executed;
                b.account.pending.remove(b);
                bid.pollFirst();
                this.updateBookReceivers(OrderType.bid);
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
                this.updateBookReceivers(OrderType.bid);
                this.updateBookReceivers(OrderType.ask);

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
        // SellOrder op = ask.peek();

    }

    private boolean InitOrder(Order o) {
        double moneyNeeded = o.volume * o.limit;
        return true;
    }

    // Add an order to the orderbook
    private boolean addOrder(Order o) {
        boolean ret = false;
        switch (o.type) {
            case bid:

//                System.out.print("Exchange adding bid order \n");
                ret = bid.add(o);
                break;

            case ask:
//                System.out.print("Exchange adding ask order \n");                
                ret = ask.add(o);
                break;
        }

        if (ret) {
            this.updateBookReceivers(o.type);
        }
        return ret;
    }

    public Order SendOrder(Order o) {

        boolean rc = InitOrder(o);
        if (!rc) {
            return null;
        }

        tradelock.lock();
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        addOrder(o);
        o.account.pending.add(o);
        executeOrders();
        tradelock.unlock();

        return o;
    }

    /*
    public void SendOrder(BuyOrder o) {
        //System.out.println("EX Buyorder");
        Lock();
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        bid.add(o);
               
        Unlock();
        Lock();
//        executeOrders();
        Unlock();

    }
     */
 /*
	 * public void SendOrder(Order o){
	 * 
	 * 
	 * if ( o.getClass() == BuyOrder.class){ bid.add((BuyOrder)o); }
	 * 
	 * if ( o.getClass() == SellOrder.class){ ask.add((SellOrder)o); }
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

    /*  public double sendOrder(Account o) {
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
