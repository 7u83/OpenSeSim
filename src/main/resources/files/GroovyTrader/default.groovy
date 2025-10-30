/*
 * This is a simple groovy trader example
 *  
 * After an initial delay, the trader will buy as many shares as possible, 
 * place a "Take Profit" 10% above the buy price, and a Stop Loss order 10% 
 * below the buy price. Once the Take Profit or Stop Loss is 
 * triggered (or executed), the trader will start over, beginning with 
 * the initial delay.
 *  
 */


import groovy.transform.Field

@Field INITIAL_DELAY_MIN = 60000
@Field INITIAL_DELAY_MAX = 240000


def start(){
    // deactivate acount update notifications
    sesim.onAccountUpdate(null);

    // start with an initial delay
    def delay = sesim.getRandom(INITIAL_DELAY_MIN,INITIAL_DELAY_MAX);
    sesim.setStatus("Initial delay %d",delay);
    sesim.scheduleOnce("runTrader",delay);
}

def myEvent;
def myOrder;

def runTrader(){
    // buy 100000 shares (or as many as cash is available)
    sesim.createOrder(sesim.BUY,100000,0.0d,0.0d);
    // enable account update notifications
    sesim.onAccountUpdate("accountUpdated");

    // calculate stop loss and take profit
    lastPrice = sesim.getLastPrice();
    takeProfitPrice = lastPrice+lastPrice/100*10;
    stopLoss = lastPrice-lastPrice/100*10;

    // create a stop loss order
    myOrder = sesim.createOrder(sesim.STOPLOSS,account.getShares(),0,stopLoss);

    // register a notificate when price hits take profit price
    myEvent = sesim.scheduleOnPriceAbove("takeProfit",takeProfitPrice);

	
}

def takeProfit(){
    // take profit price hit, cancel the stop loss order
    sesim.cancleOrder(myOrder);
    // sell all shares
    sesim.createOrder(sesim.SELL,account.getShares(),0,0);
    // start over
    start();
}

def accountUpdated(def o){
    if (o.isClosed() && o.hasStop() ){
        // our stop loss order was executed
        // cancel the take profit event
        sesim.cancelSchedulePriceAboce(myEvent);

        // start over
        start();
    }
	
}

