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
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
import static gui.Globals.sim;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.swing.JDialog;
import org.codehaus.groovy.control.CompilationFailedException;
import org.json.JSONObject;
import sesim.Account;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Exchange.AccountListener;
import sesim.Exchange.PriceEvent;
import sesim.Order;
import sesim.Quote;
import sesim.Scheduler;
import sesim.Scheduler.Event;
import sesim.Scheduler.EventProcessor;
import sesim.Sim;

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

    public GroovyTrader() {
        //"/resources/files/GroovyTrader/"
        try (InputStream is = getClass().getResourceAsStream("/files/GroovyTrader/default.groovy")) {

            if (is == null) {
                // Dies tritt ein, wenn die Datei im JAR nicht gefunden wird.
                throw new IOException("SQL-Resource nicht im JAR gefunden: ");
            }

            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                content = sb.toString();
            }
            
            this.groovySourceCode = content;

        } catch (IOException ex) {

            // System.getLogger(GroovyTrader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public void start() {
        accountApi = new AccountApi();
        sesimApi = new SeSimApi();

        Binding binding = new Binding();
        binding.setVariable("account", accountApi);
        binding.setVariable("sesim", sesimApi);

        GroovyShell shell = new GroovyShell(binding);

        try {
            groovyScript = shell.parse(this.groovySourceCode, "trader.groovy");
        } catch (CompilationFailedException e) {
            sesim.Logger.error("Starting %s:\n%s ", getName(), e.getMessage());
            return;
        }

        try {

            Object result = groovyScript.invokeMethod("start", new Object[]{});

            //     System.out.println("Ergebnis aus Groovy: " + result);
        } catch (Exception e) {
            sesim.Logger.error("Executing %s\n,%s", getName(), e.getMessage());
            // throw (e);
        }

    }

    @Override
    public JDialog getGuiConsole(Frame parent) {
        return null;
    }

    public class AccountApi {

        public float getCashBalance() {
            return account_id.getMoney();
        }

        public float getShares() {
            return account_id.getShares();
        }

    }

    public class SeSimApi implements AccountListener {

        public final byte BUYLIMIT = Order.BUYLIMIT;
        public final byte SELLIMIT = Order.SELLLIMIT;
        public final byte SELL = Order.SELL;
        public final byte BUY = Order.BUY;
        public final byte STOPLOSS = Order.STOPLOSS;

        SeSimApi() {
            account_id.setListener(this);
        }

        private class GroovyTimerEvent extends Event implements EventProcessor {

            final String groovyFun;

            public GroovyTimerEvent(String fun, long t) {
                this.eventProcessor = this;
                this.groovyFun = fun;
            }

            @Override
            public long processEvent(long time, Event e) {
                Object result = groovyScript.invokeMethod(this.groovyFun, new Object[]{});
                return 0;
            }

        }

        public Order createOrder(byte type, double vol, double limit, double stop) {
            limit = se.roundMoney(limit);
            vol = se.roundShares(vol);
            return se.createOrder(account_id, type, (float) vol, (float) limit, (float) stop);
        }

        public void logError(String msg, Object... args) {
            sesim.Logger.error(msg, args);
        }

        public void logInfo(String msg, Object... args) {
            sesim.Logger.info(msg, args);
        }

        /*   public Order createOrder(Order type, double vol, double limit){
            return createOrder(type, (float)vol, (float)limit);
        }*/
        public boolean cancleOrder(Order o) {
            if (o == null) {
                return false;
            }
            return se.cancelOrder(account_id, o.getID());
        }

        public Quote getLastQuote() {
            return se.getLastQuoete();
        }

        public float getLastPrice() {
            return getLastQuote().getPrice();
        }

        public boolean scheduleOnce(String groovyFun, long timer) {
            GroovyTimerEvent g = new GroovyTimerEvent(groovyFun, timer);
            sim.addEvent(sim.getCurrentTimeMillis()
                    + timer, g);

            return true;
        }

        public long getRandom(long a, long b) {
            return Sim.random.nextLong(a, b);
        }

        public double getRandom(double a, double b) {
            return Sim.random.nextDouble(a, b);
        }

        public void setStatus(String s, Object... args) {
            GroovyTrader.this.setStatus(s, args);
        }

        public class GroovyPriceEvent extends PriceEvent implements EventProcessor {

            final String groovyFun;

            public GroovyPriceEvent(String fun, Exchange se, double price) {
                super(se, price);
                this.eventProcessor = this;
                this.groovyFun = fun;
            }

            @Override
            public long processEvent(long time, Event e) {
                Object result = groovyScript.invokeMethod(this.groovyFun, new Object[]{});
                return 0;
            }

        }

        public GroovyPriceEvent scheduleOnPriceAbove(String groovyFun, double price) {
            GroovyPriceEvent e = new GroovyPriceEvent(groovyFun, sim.se, price);
            sim.se.sheduleOnPriceAbove(e);
            return e;
        }

        public void cancelSchedulePriceAboce(GroovyPriceEvent e) {
            sim.se.cancelScheduleOnPriceAbove(e);
        }

        String groovyAccountUpdateFun = null;

        @Override
        public void accountUpdated(Account a, Order o) {
            if (groovyAccountUpdateFun != null) {
                Object result = groovyScript.invokeMethod(this.groovyAccountUpdateFun, new Object[]{o});
            }

        }

        public void onAccountUpdate(String groovyFun) {
            groovyAccountUpdateFun = groovyFun;
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
