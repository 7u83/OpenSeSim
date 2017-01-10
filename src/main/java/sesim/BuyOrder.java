package sesim;

public class BuyOrder extends Order_old implements Comparable<Order_old> {

    public BuyOrder(){
        super(OrderType_old.bid);
    }

}
