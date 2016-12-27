package SeSim;

import java.util.*;

final public class Account {
    
    /**
     * Exchange this account belongs to
     */
    protected Exchange se;
    
    /**
     * Number of shares in this account
     */
    protected long shares = 0;

    /**
     * Ammount of money in this account
     */
    protected double money = 0;

    /**
     * Name of this account
     */
    public String name = "";

    public boolean orderpending = false;
    
       
    public Account(Exchange se, long shares, double money ) {
        this.shares=shares;
        this.money=money;
        this.se=se;
        pending = new TreeSet();
    }
    
    public Account(){
       //this(,0.0);
    }

   // private double bound_money;
    
    

    public void print_current() {
        System.out.printf("%s shares: %d credit: %.2f\n",
                name, shares, money
        );
    }
    
    TreeSet pending;

    public SellOrder sell(long volume, double limit) {
        SellOrder o = new SellOrder();
        o.account = this;
        o.limit = limit;
        o.volume = volume;
        orderpending = true;
        se.SendOrder(o);
        return o;

    }

    public BuyOrder Buy(long size, double limit, Exchange ex) {
        if (size * limit > money) {
            return null;
        }

        BuyOrder o = new BuyOrder();
        o.limit = limit;
        o.volume = size;
        o.account = this;
        orderpending = true;
        ex.SendOrder(o);
        return o;

    }

    /*
    public void Buy(Account a, long size, double price) {
        shares += size;
        money -= price * size;
        a.shares -= size;
        a.money += price * size;
    }
*/

}
