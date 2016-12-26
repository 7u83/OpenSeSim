package SeSim;

public abstract class ThreadedTrader extends Thread  {

    protected long sleeptime = 100;

    public void RandomTrader(Exchange ex, long shares, double money) {
        //	this.ex=ex;
        

    }

    public void run() {
        while (true) {
            try {
                sleep(sleeptime);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                return;
            }
//            trade();
        }
    }

}
