package sesim;

public class BuyOrder extends Order implements Comparable<Order> {

    public BuyOrder(){
        super(OrderType.bid);
    }

}
