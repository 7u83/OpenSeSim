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
package opensesim.old_sesim;

import org.json.JSONObject;
import opensesim.old_sesim.Scheduler.TimerTaskRunner;

/**
 * 
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AutoTraderBase implements AutoTraderInterface, TimerTaskRunner {

    protected double account_id;
    protected Exchange se;
   // protected AutoTraderConfig config;

    protected String name;

/*    public AutoTraderBase(Exchange se, long id, String name, double money, double shares, AutoTraderConfig config) {
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

    public String getName() {
        return name;
    }

   
    @Override
    public long getID() {
        return id;
    }
    private long id;

    public Account getAccount() {
        return se.getAccount(account_id);
    }

    @Override
    public void init(Exchange se, long id, String name, double money, double shares, JSONObject cfg) {
        this.account_id = se.createAccount(money, shares);
        se.getAccount(account_id).owner = this;
        this.se = se;
        this.name = name;
        this.id = id;

    }

    public Exchange getSE() {
        return se;
    }

    public abstract void start();

}
