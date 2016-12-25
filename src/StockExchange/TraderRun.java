package StockExchange;

public class TraderRun extends Thread{
	public String tname = "";
	public Exchange ex;
	
	public void run(){
		while (true)
		{
			try{
				sleep(100);
			}
			catch(InterruptedException e) {
				System.out.println("Interrupted");
			}
			
/*			
			System.out.printf("%s locking\n", tname);
			ex.Lock();
			System.out.printf("%s locked\n", tname);

			try{
				sleep(1000);
			}
			catch(InterruptedException e) {
				System.out.println("Interrupted");
			}

			System.out.printf("%s unlocking\n", tname);
//			ex.Free();
			System.out.printf("%s unlocked\n", tname);
*/
			
		}
	}
	
}
