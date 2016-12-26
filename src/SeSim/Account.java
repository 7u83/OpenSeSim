package SeSim;

public class Account {

    /**
     * Number of shares in this account
     */
    public long shares = 0;

    /**
     * Ammount of money in this account
     */
    public double money = 0;

    /**
     * Name of this account
     */
    public String name = "";

    public boolean orderpending = false;

    private double bound_money;

    public void print_current() {
        System.out.printf("%s shares: %d credit: %.2f\n",
                name, shares, money
        );
    }

    public SellOrder Sell(long size, double limit, Exchange ex) {
        SellOrder o = new SellOrder();
        o.account = this;
        o.limit = limit;
        o.volume = size;

        orderpending = true;
        ex.SendOrder(o);
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

    public void Buy(Account a, long size, double price) {
        shares += size;
        money -= price * size;
        a.shares -= size;
        a.money += price * size;
    }

}
