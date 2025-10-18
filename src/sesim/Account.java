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
package sesim;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tube
 */
/**
 * Implements a trading account
 */
public class Account {
    //   private Exchange se;

    private Exchange.AccountListener listener = null;

    float shares;
    float money;
    protected AutoTraderInterface owner;

    final ConcurrentHashMap<Long, Exchange.Order> orders;

    Account(float money, float shares) {
        //  this.se = se;

        orders = new ConcurrentHashMap();
        this.money = money;
        this.shares = shares;
    }

    public float getShares() {
        return shares;
    }

    public float getMoney() {
        return money;
    }

    public AutoTraderInterface getOwner() {
        return owner;
    }

    public ConcurrentHashMap<Long, Exchange.Order> getOrders() {
        return orders;
    }

    public void setListener(Exchange.AccountListener al) {
        this.listener = al;
    }

    public void update(Exchange.Order o) {
        if (listener == null) {
            return;
        }
        listener.accountUpdated(this, o);
    }

    public int getNumberOfOpenOrders() {

        return orders.size();
    }

}
