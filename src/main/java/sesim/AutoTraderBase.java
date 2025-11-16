/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
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
package sesim;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.json.JSONObject;
import sesim.Scheduler.EventProcessor;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AutoTraderBase implements AutoTraderInterface, EventProcessor {

    //  protected float account_id;
    protected Account account_id;
    protected Market se;
    protected Sim sim;
    // protected AutoTraderConfig config;

    protected String name;
    private String strategyName="default";
    
    int[] color=null;

    /*    public AutoTraderBase(Exchange se, long id, String name, float money, float shares, AutoTraderConfig config) {
        account_id = se.createAccount(money, shares);
        Exchange.Account a = se.getAccount(account_id);

        //   a.owner=this;
        this.se = se;
        this.config = config;
        this.name = name;
        this.id = id;

    }
     */
    public AutoTraderBase() {
        se = null;
        id = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public int[]  getColor(){
        return color;
    }

//    @Override
    public long getID() {
        return id;
    }
    private long id;

    @Override
    public Account getAccount() {
        return account_id;
    }

    @Override
    public void init(Sim sim, long id, String name, float money, float shares, String strat, JSONObject cfg) {
        this.account_id = new Account(sim.getExchange(), money, shares); // se.createAccount(money, shares);
        //       se.getAccount(account_id).owner = this;

        this.sim = sim;
        this.se = sim.getExchange();
        this.account_id.owner = this;
        this.se = se;
        this.name = name;
        this.id = id;
        this.strategyName=strat;

    }

    public Market getSE() {
        return se;
    }

    @Override
    public abstract void start();

    String status = "";

    protected void setStatus(String format, Object... arguments) {

        status = String.format(format, arguments);
        //  System.out.printf("%s: %s\n", this.getName(), status);
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public JDialog getGuiConsole(Frame parent) {
        return null;
    }
    
    

    public String getStrategyName(){
        return strategyName;
    }
    
    public void setStrategyName(String s){
        strategyName=s;
    }
    
    @Override
    public void reset(){
        
    }
    
    @Override
    public void stop(){
        
    }
    
    @Override
    public Object initGlobal(Sim sim , Object global, JSONObject cfg){
        return null;
    }
    
}
