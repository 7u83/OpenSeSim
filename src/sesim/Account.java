/*
 * Copyright (c) 2017, tobias
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
     * Implements a trading account
     */
    public class Account implements Comparable {
                
        private Exchange.AccountListener listener = null;

        protected final double id;
        private double shares;
        private double money;
        protected AutoTraderInterface owner;

        protected final ConcurrentHashMap<Long , Order> orders;
        protected ConcurrentHashMap<String,Double> sharesm;

        @Override
        public int compareTo(Object a) {
            Account account = (Account) a;
            return this.id - account.id < 0 ? -1 : 1;
        }

        Account(double id, double money, double shares) {
            this.id=id;
            //id = (random.nextDouble() + (account_id_generator.getNext()));
            orders = new ConcurrentHashMap();
            this.money = money;
            this.shares = shares;
        }

        public double getID() {
            return id;
        }

        public double getShares() {
            return shares;
        }
        
        protected void setShares(double shares){
            this.shares = shares;
        }
        
        protected void addShares(double shares){
            this.shares = this.shares+shares;
        }
        
        protected void addShares(String symbol, double shares){
            Double d = this.sharesm.get(symbol);
            d+=shares;
            this.sharesm.put(symbol, d);
    //    (this.sharesm.get(symbol))+=shares;
        
        }
        
        
        protected void addMoney(double money){
            this.money+=money;
        }
        
        protected void setMoney(double money){
            this.money=money;
        }
        
        public double getShares(String symbol){
            return sharesm.get(symbol);
        }

        public double getMoney() {
            return money;
        }

        public AutoTraderInterface getOwner() {
            return owner;
        }

        public ConcurrentHashMap<Long, Order> getOrders() {
            return orders;
        }

        public void setListener(Exchange.AccountListener al) {
            this.listener = al;
        }

        public void update(Order o) {
            if (listener == null) {
                return;
            }
            listener.accountUpdated(this, o);
        }

    }
