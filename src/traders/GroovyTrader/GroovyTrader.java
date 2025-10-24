/*
 * Copyright (c) 2025, 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package traders.GroovyTrader;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import static gui.Globals.sim;
import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Order;
import sesim.Quote;
import sesim.Scheduler;
import sesim.Scheduler.Event;
import sesim.Scheduler.EventProcessor;

/**
 *
 * @author tube
 */
public class GroovyTrader extends AutoTraderBase {

    String groovySourceCode = "";
    Script groovyScript;

    final String CFG_SRC = "src";
    AccountApi accountApi;
    SeSimApi sesimApi;

    @Override
    public void start() {
        accountApi = new AccountApi();
        sesimApi = new SeSimApi();

        Binding binding = new Binding();
        binding.setVariable("account", accountApi);
        binding.setVariable("sesim", sesimApi);
        
        GroovyShell shell = new GroovyShell(binding);

        try {
            // 1. Skript parsen, um die Script-Klasse zu erhalten

            groovyScript = shell.parse(this.groovySourceCode);

            // 2. Die Methode (Funktion) mit Argumenten aufrufen
            //    Der erste Parameter ist der Name der Funktion.
            //    Der zweite Parameter ist ein Array von Argumenten (Object...).
            Object result = groovyScript.invokeMethod("start", new Object[]{});

            System.out.println("Ergebnis aus Groovy: " + result);
            return;

        } catch (Exception e) {
            System.err.println("Fehler beim Ausführen der Groovy-Funktion: " + e.getMessage());
            e.printStackTrace();
            return;
        }

    }

    public class AccountApi {

        public float getCashBalance() {
            return account_id.getMoney();
        }
        public float getShares() {
            return account_id.getShares();
        }
        
    }
    
    public class SeSimApi{
        
        private class GroovyEvent extends Event implements EventProcessor{
            final String groovyFun;

            public GroovyEvent(String fun,long t){
               this.eventProcessor=this;
               this.groovyFun=fun;
            }

            @Override
            public long processEvent(long time, Event e) {
                Object result = groovyScript.invokeMethod(this.groovyFun, new Object[]{});
                return 0;
            }
            
        }
        
        public final byte BUYLIMIT=Order.BUYLIMIT;
        public final byte SELLIMIT=Order.SELLLIMIT;
        public final byte SELL=Order.SELL;
        public final byte BUY=Order.BUY;
        
        
        public Order createOrder(byte type,double vol, double limit){
            limit = se.roundMoney(limit);
            vol = se.roundShares(vol);
            return  se.createOrder(account_id, type, (float)vol, (float)limit,0f);
        }
        
     /*   public Order createOrder(Order type, double vol, double limit){
            return createOrder(type, (float)vol, (float)limit);
        }*/
        
        public boolean cancleOrder(Order o){
            return se.cancelOrder(account_id, o.getID());
        }
        
        public Quote getLastQuote(){
            return se.getLastQuoete();
        }
        
        public float getLastPrice(){
            return getLastQuote().getPrice();
        }
        
        public boolean scheduleOnce(String groovyFun, long timer){
            GroovyEvent g = new GroovyEvent(groovyFun,timer);
            sim.addEvent(sim.getCurrentTimeMillis()
                + timer, g);
            
            return true;
        }
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
        return new GroovyTraderGui(this);
    }

    @Override
    public JSONObject getConfig() {
        JSONObject r = new JSONObject();
        r.put(CFG_SRC, this.groovySourceCode);
        return r;
    }

    @Override
    public void setConfig(JSONObject cfg) {
        this.groovySourceCode = cfg.optString(CFG_SRC, "");
    }

    @Override
    public long processEvent(long time, Scheduler.Event e) {
        return 0;
    }

}
