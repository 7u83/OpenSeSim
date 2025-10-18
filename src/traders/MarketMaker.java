/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package traders;

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Exchange.Order;
import sesim.Scheduler;

/**
 *
 * @author tube
 */
public class MarketMaker extends AutoTraderBase {

    int numPosition = 10;
    float centerPrice = 100.0f;
    float lowestPrice = 0.0f;

    Order orders[] = null;
    float cashPerBuyOrder = 0f;

    @Override
    public void start() {
        centerPrice = se.getBestPrice();
        float dist = (centerPrice - lowestPrice) / numPosition;
        float cashPerOrder = account_id.getMoney();
        cashPerBuyOrder=account_id.getMoney()/numPosition;

        float price = lowestPrice;
        while (price < centerPrice) {
         
            price += dist;
            float vol = se.roundShares(cashPerBuyOrder/price);
            System.out.printf("Buy Order at price: %f with vol: %f\n", price,vol);
            se.createOrder(account_id, Exchange.OrderType.BUYLIMIT, vol, price);
            
        }

        this.setStatus("Price: %f", dist);

    }

    @Override
    public boolean getDevelStatus() {
        return true;

    }

    @Override
    public String getDisplayName() {
        return "MarketMaker";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;

    }

    @Override
    public JSONObject getConfig() {
        return new JSONObject();
    }

    @Override
    public void putConfig(JSONObject cfg) {

    }

    @Override
    public long processEvent(long time, Scheduler.Event e) {
        return 0;
    }

}
