/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package traders.GroovyTrader;

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Scheduler;

/**
 *
 * @author tube
 */
public class GroovyTrader extends AutoTraderBase {

    @Override
    public void start() {
        return;
        

    }

    @Override
    public boolean getDevelStatus() {
return true;
    }

    @Override
    public String getDisplayName() {
        return "Groovy Trader";
    }

    /**
     *
     * @return
     */
    @Override
    public AutoTraderGui getGui() {
        return new GroovyTraderGui();
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
