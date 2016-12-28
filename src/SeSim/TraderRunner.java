package SeSim;

public class TraderRunner extends Thread  {

    protected long sleeptime = 1000;
    
    Trader trader;
    
    public TraderRunner(Trader trader){
        this.trader=trader;
    }

    public void run() {
        while (true) {
            try {
                sleep(sleeptime);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                return;
            }
            trader.trade();
        }
    }
}
