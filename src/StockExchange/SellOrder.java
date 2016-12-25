package StockExchange;

public class SellOrder extends Order {

        @Override
	public int compareTo(Order o) {
	
		if (o.limit < limit) {
			return 1;
		}
		if (o.limit > limit) {
			return -1;
		}
		return 0;
	}
}
