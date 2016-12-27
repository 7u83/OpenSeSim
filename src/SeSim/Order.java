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

    /**
     * Order ID
     */
    public long id = 0;
    
    /**
     * Type of order
     */
    public OrderType type;
    
    public Account account = null;

    
    protected int compareLimit(Order o){
        int r=0;
        if (o.limit < limit) {
            r=-1;
        }
        if (o.limit > limit) {
            r=1;
        }
        if (r==0)
            return 0;
        
        if (type==OrderType.sell)
            return 1-r;
        
        return r;
        
    };
    
    @Override
    public int compareTo(Order o) {
        
        if (o.type!=type){
            System.out.print("OrderType Missmatch\n");
            return -1;
        }
        
        int r = compareLimit(o);
        if (r!=0)
            return r;
       
        if (o.timestamp< timestamp)
            return -1;
        
        if (o.timestamp>timestamp)
            return 1;

        
        if (o.id>id)
            return -1;
        
        if (o.id<id)
            return 1;
        
        return 0;   
    }
    
    

    enum OrderStatus {
        open, executed, canceled
    }
    
    enum OrderType {
        buy,sell
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
