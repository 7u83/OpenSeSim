package StockExchange;

public class Account {
	public long shares=0;	// number of shares 
	public double money=0;	// amount of money
	public String name="";	
	public boolean orderpending=false;
	
	private double bound_money;
	
	
	
	
	public void print_current(){
		System.out.printf("%s shares: %d credit: %.2f\n",
				name, shares,money
				);
			
		
	}
	

	public SellOrder Sell (long size, double limit, Exchange ex){
		SellOrder o = new SellOrder();
		o.account=this;
		o.limit=limit;
		o.size=size;
		
		orderpending=true;
		ex.SendOrder(o);
		return o;
	
	}
	
	public BuyOrder Buy (long size, double limit, Exchange ex ){
		if (size * limit > money)
			return null;
	
		BuyOrder o = new BuyOrder();
		o.limit=limit;
		o.size=size;
		o.account=this;
		orderpending=true;
		ex.SendOrder(o);
		return o;
				
	}
	
	public void Buy( Account a,long size, double price)
	{
		shares+=size;
		money-= price * size;
		a.shares-=size;
		a.money+=price * size;
	}

	
}
