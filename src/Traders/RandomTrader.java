package Traders;

import SeSim.Account;
import SeSim.Order;
import java.util.Random;
import SeSim.Trader;
import SeSim.TraderConfig;

public class RandomTrader extends Trader {
    
    
    public TraderConfig getTraderConfig(){
        return new RandomTraderConfig();
    } 
    
    private RandomTraderConfig myconfig;

    Random rand = new Random();

    // my current order
    //private Order myorder = null;
    public RandomTrader(Account account,TraderConfig config) {
        super(account,config);
        if (config==null){
            config = new RandomTraderConfig();
        }
        myconfig = (RandomTraderConfig)config;
    }

    public void doBuy() {

        
  
       if (account.money <= 0) {
           return;
       }

        double perc = 5.0-rand.nextDouble() * 10.0;
        double lp = account.se.getlastprice();
        double limit = lp / 100 * perc + lp;

        long volume = (int) (account.money / (limit * 1));
        
        if (volume ==0 ){
            System.out.print("Can't buy shares = 0 my money="+account.money+" shares="+account.shares+"\n");
            return;
        }

        buy(volume, limit);
        return;
    }

    public void doSell() {

        if (account.shares <= 0) {
            return;
        }

    
        double perc = 5.0-rand.nextDouble() * 10.0;
        double lp = account.se.getlastprice();
        double limit = lp - lp / 100 * perc;

        long size = (int) (account.shares);

        sell(size, limit);
    }

    private boolean monitorTrades() {
        int numpending = account.pending.size();
        
       // System.out.print("RT: Monitoring trades - Pending: "+numpending+"\n");
        if (numpending == 0) {
//            System.out.print("RT: pending = 0 - return false\n");
            return false;
        }

        Order o = account.pending.get(0);
        long age = o.getAge();
        
       // System.out.print("RT: age is: "+age+"\n");

        if (age > myconfig.maxage) {
   //         System.out.print("MaxAge is"+myconfig.maxage+"\n");
            account.se.CancelOrder(o);
//            System.out.print("Age reached - canel return false\n");
            return false;
        }
        
        //System.out.print("RT: monitor return true\n");
        return true;
    }

    public void trade() {
        
        
      //  System.out.print("RT: Now trading\n");

        if (monitorTrades()) {
            return;
        }
        

        // What next to do?
        int action = rand.nextInt(5);
        
        if (account.money<10 && account.shares<5){
            System.out.print("I'm almost ruined\n");
        }
        
        if (action == 1) {
            doBuy();
            return;
        }

        if (action == 2) {
            doSell();
            return;
        }

    }


}
