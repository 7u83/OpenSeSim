package Traders;

import SeSim.Account;
import SeSim.Exchange;
import SeSim.Order;
import java.util.Random;
import SeSim.Trader;

public class RandomTrader extends Trader {

    Random rand = new Random();

    // my current order
    //private Order myorder = null;
    public RandomTrader(Account account) {
        super(account);
    }

    public void doBuy() {

  
  //      if (account.money <= 0) {
  //          return;
  //      }

        double perc = rand.nextDouble() * 1.0;
        double lp = account.se.getlastprice();
        double limit = lp / 100 * perc + lp;

        long size = (int) (account.money / (limit * 1));

        buy(size, limit);
        return;
    }

    public void doSell() {
    /*    if (myorder != null) {
            return;
        }

        if (account.shares <= 0) {
            return;
        }
*/
    
        double perc = rand.nextDouble() * 1.0;
        double lp = account.se.getlastprice();
        double limit = lp - lp / 100 * perc;

        long size = (int) (account.shares);

        sell(size, limit);
    }

    private boolean monitorTrades() {
        int numpending = account.pending.size();
        
        System.out.print("RT: Monitoring trades - Pending: "+numpending+"\n");
        if (numpending == 0) {
            System.out.print("RT: pending = 0 - return false\n");
            return false;
        }

        Order o = account.pending.get(0);
        long age = o.getAge();
        
        System.out.print("RT: age is: "+age+"\n");

        if (age > 10000) {
            account.se.CancelOrder(o);
            System.out.print("Age reached - canel return false\n");
            return false;
        }
        
        System.out.print("RT: monitor return true\n");
        return true;
    }

    public void trade() {
        
        
        System.out.print("RT: Now trading\n");

        if (monitorTrades()) {
            return;
        }
        

        // What next to do?
        int action = rand.nextInt(3);
        
        if (action == 1) {
            doBuy();
            return;
        }

        if (action == 2) {
            doSell();
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
