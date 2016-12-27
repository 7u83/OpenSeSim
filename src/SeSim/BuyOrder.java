package SeSim;

public class BuyOrder extends Order implements Comparable<Order> {

    public BuyOrder(){
        type=OrderType.buy;
    }

}
