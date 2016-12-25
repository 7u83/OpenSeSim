package StockExchange;

public abstract class Order implements Comparable<Order> {

    /**
     * when
     */
    public long timestamp = 0;
    public long size;
    public double limit;
//	long time;
    double money = 0;
//	public long shares=0;
    public long id = 0;
    public Account account = null;

    enum OrderStatus {
        open, executed, canceled
    }

    OrderStatus status = OrderStatus.open;

    public long getAge() {
        if (timestamp == 0) {
            return 0;
        }
        return System.currentTimeMillis() - timestamp;
    }

    String format_limit() {
        if (limit < 0.0) {
            return "n.a.";
        }
        return String.format("%.2f", limit);
    }

    String format_size() {
        return String.format("%d", size);
    }

    Order() {

    }
}
