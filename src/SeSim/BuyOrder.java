package SeSim;

public class BuyOrder extends Order implements Comparable<Order> {

 /*   @Override
    public int compareLimit(Order o) {
                           
        if (o.limit < limit) {
            return -1;
        }
        if (o.limit > limit) {
            return +1;
        }
        
        return 0;
    }
*/
    public BuyOrder(){
        type=OrderType.buy;
    }

}
