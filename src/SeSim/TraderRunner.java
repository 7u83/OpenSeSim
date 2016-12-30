package SeSim;

public class TraderRunner extends Thread  {

    protected long sleeptime = 1000;
    
    AutoTrader trader;
    
    public TraderRunner(AutoTrader trader){
        this.trader=trader;
    }

    public void run() {
        trader.run();
        
        
    }
}
