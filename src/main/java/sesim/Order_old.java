package sesim;

public abstract class Order_old implements Comparable<Order_old> {

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
     * Order_old ID
     */
    public long id = 0;
    
    /**
     * Type of order
     */
    public final OrderType_old type;
    
    public Account_old account = null;

    
    protected int compareLimit(Order_old o){
        
        int r=0;
        if (o.limit < limit) {
            r=-1;
        }
        if (o.limit > limit) {
            r=1;
        }
        
        if (type==OrderType_old.ask)
            return -r;
        
        return r;
        
    };
    
    @Override
    public int compareTo(Order_old o) {
        
        if (o.type!=type){
            System.out.print("OrderType Missmatch\n");
            return -1;
        }
        
        int r = compareLimit(o);
        if (r!=0)
            return r;
       
                
        if (o.id>id)
            return -1;
        
        if (o.id<id)
            return 1;
        
        return 0;   
    }
    
    

    enum OrderStatus {
        open, executed, canceled
    }
    
    public enum OrderType_old {
        bid,ask
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

    Order_old(OrderType_old type) {
        this.type=type;
    }
}
