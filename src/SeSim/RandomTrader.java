package SeSim;

import java.util.Random;
import SeSim.Order.OrderStatus;

public class RandomTrader extends Trader {

//	public Account account=new Account();
    Exchange ex = null;
    Random rand = new Random();

    
    // my current order
    private Order myorder = null;

    public RandomTrader(Account account) {
        super(account);
    }

 /*   public RandomTrader(Exchange ex, long shares, double money) {
        account.money = money;
        account.shares = shares;
        this.ex = ex;
    }
*/
    public void DoBuy() {

        if (myorder != null) {
            return;
        }

        if (account.money <= 0) {
            return;
        }

        double perc = rand.nextDouble() * 1.0;
        double lp = ex.lastprice;
        double limit = lp / 100 * perc + lp;

        long size = (int) (account.money / (limit * 1));

        myorder = account.Buy(size, limit, ex);
        return;
    }

    public void DoSell() {
        if (myorder != null) {
            return;
        }

        if (account.shares <= 0) {
            return;
        }

        double perc = rand.nextDouble() * 1.0;
        double lp = ex.lastprice;
        double limit = lp - lp / 100 * perc;

        long size = (int) (account.shares);

        myorder = account.sell(size, limit);
    }

    public void trade() {

        if (myorder != null) {
            long age = myorder.getAge();
            if (myorder.status == OrderStatus.executed) {
                myorder = null;
                //		System.out.println(name);
                //		System.out.println("----------------------");
                //		account.print_current();
                return;
            }

            if (myorder.getAge() > 10) {
                //System.out.println("Shall cancel now");
                //System.out.println(myorder.status);
                ex.CancelOrder(myorder);
                myorder = null;
                return;
            }

            return;
        }

        // What to do?
        int action = rand.nextInt(3);
        /*		System.out.print(name);
		System.out.println("---------------------------");
		System.out.print("Action:");
		System.out.println(action);
         */
 /*		if (action==0)
		{
			DoSell();
			return;
		}
         */

        if (action == 1) {
            DoBuy();
            return;
        }

        if (action == 2) {
            DoSell();
            return;
        }

    }

    /*	public void run(){
		while (true)
		{
			try{
				sleep(200);
			}
			catch(InterruptedException e) {
				System.out.println("Interrupted");
			}
		//	System.out.println("Trader has slept");
			trade();
			
			
			
		}
	}
	
     */
}
