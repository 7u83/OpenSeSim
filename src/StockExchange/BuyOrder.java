package StockExchange;

public class BuyOrder extends Order implements Comparable<Order> {

    @Override
    public int compareTo(Order o) {

        if (o.limit < limit) {
            //System.out.println("return 1");
            return -1;
        }
        if (o.limit > limit) {
            //System.out.println("return -1");
            return +1;
        }
//		System.out.println("0000000000000000000000");
        return 0;
    }
}
