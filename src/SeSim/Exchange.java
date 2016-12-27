package SeSim;

import java.util.*;
import java.util.concurrent.*;


import SeSim.Order.OrderStatus;

/**
 *
 * @author tube
 */
public class Exchange extends Thread {

    
    
    
    
    
    // Here we store the list of quote receivers
    private final TreeSet<QuoteReceiver> qrlist;

    /**
     * Histrory of quotes
     */
    public ArrayList<Quote> quoteHistory;

    /**
     * Constructor
     */
    public Exchange() {
        this.ask = new TreeSet<>();
        this.bid = new TreeSet<>();
        this.qrlist = new TreeSet<>();
        this.quoteHistory = new ArrayList<>();
    }

    // Class to describe an executed order
    public class Quote {

        double bid;
        double bid_size;
        double ask;
        double ask_size;

        public double price;
        public long size;
        public long time;
    }

    // QuoteReceiver has to be implemented by objects that wants 
    // to receive quote updates  	
    public interface QuoteReceiver {

        void UpdateQuote(Quote q);
    }

    public void AddQuoteReceiver(QuoteReceiver qr) {
        qrlist.add(qr);
    }

    // send updated quotes to all quote receivers
    void UpdateQuoteReceivers(Quote q) {
        Iterator<QuoteReceiver> i = qrlist.iterator();
        while (i.hasNext()) {
            i.next().UpdateQuote(q);
        }
    }

    // long time = 0;
    double price = 12.9;
    long orderid = 1;

    double lastprice = 300.0;
    long lastsize;
    
    public TreeSet<BuyOrder> bid;
    public TreeSet<SellOrder> ask;

    private final Semaphore available = new Semaphore(1, true);

    private void Lock() {
        try {
            available.acquire();
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

    }

    private void Unlock() {
        available.release();
    }
    
    public ArrayList geAskBook(int n){
        ArrayList ret = new ArrayList();
        Iterator it=ask.iterator();
        for(int i=0;i<n && it.hasNext();i++){
            SellOrder o;
            o = (SellOrder)it.next();
            ret.add(o);
            System.out.print("Order"+o.limit);
            System.out.println();
        }
        return ret;
    }
    
       public ArrayList geBidBook(int n){
        ArrayList ret = new ArrayList();
        Iterator it=bid.iterator();
        for(int i=0;i<n && it.hasNext();i++){
            BuyOrder o;
            o = (BuyOrder)it.next();
            ret.add(o);
            System.out.print("Order"+o.limit);
            System.out.println();
        }
        return ret;
    }
    
    

    public void print_current() {

        BuyOrder b;
        SellOrder a;

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

        Logger.info(
                String.format("BID: %s(%s)  LAST: %.2f(%d)  ASK: %s(%s)\n",
                        b.format_limit(), b.format_volume(),
                        lastprice, lastsize,
                        a.format_limit(), a.format_volume())
        );

    }

    public void TransferMoney(Account src, Account dst, double money) {
        src.money -= money;
        dst.money += money;
              
    }

    public void CancelOrder(Order o) {
        Lock();
//		System.out.println("Cancel BuyOrder");
        bid.remove((BuyOrder) o);
        ask.remove((SellOrder) o);
        o.status = OrderStatus.canceled;
        Unlock();

    }

    public void OrderMatching() {

        while (true) {
            if (bid.isEmpty() || ask.isEmpty()) {
                // nothing to do
                return;
            }

            BuyOrder b = bid.first();
            SellOrder a = ask.first();

            if (a.volume == 0) {
                // This order is fully executed, remove 
                a.account.orderpending = false;
                a.status = OrderStatus.executed;
                ask.pollFirst();
                continue;
            }

            if (b.volume == 0) {
                // 
                b.account.orderpending = false;
                b.status = OrderStatus.executed;
                bid.pollFirst();
                continue;
            }

            if (b.limit < a.limit) {
                // no match, nothing to do
                return;
            }

            if (b.limit >= a.limit) {
                double price;

                if (b.id < a.id) {
                    price = b.limit;
                } else {
                    price = a.limit;
                }

                long size = 0;

                if (b.volume >= a.volume) {
                    size = a.volume;
                } else {
                    size = b.volume;
                }

                b.account.Buy(a.account, size, price);
                b.volume -= size;
                a.volume -= size;

                lastprice = price;
                lastsize = size;

                Quote q = new Quote();

                q.size = size;
                q.price = price;
                q.time = System.currentTimeMillis();

                this.UpdateQuoteReceivers(q);

                //quoteHistory.add(q);
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

    public void SendOrder(SellOrder o) {
//		System.out.println("EX Sellorder");
        Lock();
        boolean rc = InitOrder(o);
        
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        ask.add(o);
               
        Unlock();
        
        Lock();
        OrderMatching();
        Unlock();

    }

    public void SendOrder(BuyOrder o) {
        //System.out.println("EX Buyorder");
        Lock();
        o.timestamp = System.currentTimeMillis();
        o.id = orderid++;
        bid.add(o);
               
        Unlock();
        Lock();
        OrderMatching();
        Unlock();

    }

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

        return price;
    }

    public double sendOrder(Account o) {
        return 0.7;
    }

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
