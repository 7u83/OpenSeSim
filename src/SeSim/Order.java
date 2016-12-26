package SeSim;

public abstract class Order implements Comparable<Order> {

    /**
     * When the order was created
     */
    public long timestamp = 0;

    /**
     * Number of shares
     */
    public long volume;

    /**
     * Limit price
     */
    public double limit;

 //   double money = 0;

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

    String format_volume() {
        return String.format("%d", volume);
    }

    Order() {

    }
}
