/*
 * Copyright (c) 2025, 7u83
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

/**
 *
 * @author tube
 */
public class Order implements OrderBookEntry {

    // Order types
    public final static byte BUY = 0x00;
    public final static byte SELL = 0x01;
    public final static byte LIMIT = 0x02;
    public final static byte STOP = 0x04;
    public final static byte TAKEPROFIT = 0x08;
    public final static byte FOK = 0x10;

    public final static byte SELLLIMIT = SELL | LIMIT;
    public final static byte BUYLIMIT = BUY | LIMIT;
    public final static byte STOPBUY = BUY | STOP;
    public final static byte STOPLOSS = SELL | STOP;

    public final static byte BUYSTOP = BUY | STOP;
    public final static byte SELLSTOP = SELL | STOP;

    // Order states
    public final static byte OPEN = 0x01;
    public final static byte PARTIALLY_EXECUTED = 0x3;
    public final static byte CLOSED = 0x04;
    public final static byte CANCELED = 0x08;

    float volume;
    float limit;
    float stop;
    float profit;

    //Exchange.OrderStatus status;
    byte status;

    // Order type;
    byte type;

    public final float initial_volume;
    public final long id;
    public final long created;
    public final Account account;
    float cost;
    Exchange se;

    Order(Exchange se, Account account, byte type, float volume, float limit) {
        this.account = account;
        this.se = se;
        id = se.order_id.getNext();
        this.type = type;
        this.limit = se.roundMoney(limit);
        this.volume = se.roundShares(volume);
        this.initial_volume = this.volume;
        this.created = se.timer.getCurrentTimeMillis();
        this.status = OPEN; //Exchange.OrderStatus.OPEN;
        this.cost = 0;

    }

    Order(Order o) {
        this.se = o.se;
        this.account = o.account;
        id = o.id;
        type = o.type;
        limit = o.limit;
        volume = o.volume;
        initial_volume = o.initial_volume;
        created = o.created;
        status = o.status;
        cost = o.cost;
        stop = o.stop;
    }

    public String getOwnerName() {
        return account.owner.getName();
    }

    public Account getAccount() {
        return account;
    }

    public long getID() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public String getTypeAsString() {
        String s;
        if (0 != (type & Order.SELL)) {
            s = "SELL";
        } else {
            s = "BUY";
        }
        return s;
    }

    public float getExecuted() {
        return initial_volume - volume;
    }

    public float getInitialVolume() {
        return initial_volume;
    }

    public float getCost() {
        return cost;
    }

    public boolean isSell() {
        return (type & SELL) != 0;
    }

    public float getAvaragePrice() {
        float e = getExecuted();
        if (e <= 0) {
            return -1;
        }
        return cost / e;
    }

    public byte getStatus() {
        return status;
    }

    public String getStatusString() {
        switch (status) {
            case OPEN:
                return "OPEN";
            case CLOSED:
                return "CLOSED";
            case PARTIALLY_EXECUTED:
                return "PARTIALLY_EXECUTED";
            case CANCELED:
                return "CANCELED";
            default:
                return "UNKNOW";
        }
    }

    public long getCreated() {
        return created;
    }

    public boolean isOpen() {
        return this.status == OPEN
                || this.status == PARTIALLY_EXECUTED;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getLimit() {
        return limit;
    }

    public boolean hasLimit() {
        return (type & LIMIT) != 0;
    }

    public boolean hasStop() {
        return (type & STOP) != 0;
    }

    @Override
    public void addVolume(float volume) {

    }

    @Override
    public float getStop() {
        System.out.printf("Get stop %f\n", stop);
        return stop;
    }
}
