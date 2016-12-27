package SeSim;

public class SellOrder extends Order {
/*
    @Override
    public int compareTo(Order o) {
        
        return super.compareTo(o);
        
    }
    
    public SellOrder(){
        type=OrderType.buy;
    }
*/
    
     public SellOrder(){
        type=OrderType.sell;
    }
}
