/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package traders;

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange.Order;
import sesim.Scheduler;


/**
 *
 * @author tube
 */
public class MarketMaker extends AutoTraderBase {
    
    int numPosition=10;
    float centerPrice=100.0f;
    float lowestPrice=0.0f;
    
    Order orders[]=null;

    @Override
    public void start() {
        float dist=(centerPrice-lowestPrice)/this.numPosition;
        
        centerPrice = se.getBestPrice();
        
       /* while(false){
        
        }*/
        
        this.setStatus("Price: %f",p);

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
