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

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Order {

    public enum OrderStatus {
        OPEN,
        PARTIALLY_EXECUTED,
        CLOSED,
        CANCELED
    }

    Stock stock;
    OrderStatus status;
    Exchange.OrderType type;
    protected double limit;
    protected double volume;

    protected final double initial_volume;
    protected final long id;
    protected final long created;

    protected final Exchange.Account account;

    double cost;

    Order(long id, long created, Exchange.Account account, Exchange.OrderType type, double volume, double limit) {
        //id = order_id_generator.getNext();
        this.id = id;
        this.account = account;
        this.type = type;
        this.limit = limit;
        this.volume = volume;
        this.initial_volume = this.volume;
        this.created = created;
        this.status = OrderStatus.OPEN;
        this.cost = 0;
    }

    public long getID() {
        return id;
    }

    public double getVolume() {
        return volume;
    }

    public double getLimit() {
        return limit;
    }

    public Exchange.OrderType getType() {
        return type;
    }

    public double getExecuted() {
        return initial_volume - volume;
    }

    public double getInitialVolume() {
        return initial_volume;
    }

    public double getCost() {
        return cost;
    }

    public double getAvaragePrice() {
        double e = getExecuted();
        if (e <= 0) {
            return -1;
        }
        return cost / e;
    }

    public Exchange.Account getAccount() {
        return account;
    }

    public OrderStatus getOrderStatus() {
        return status;
    }

    public long getCreated() {
        return created;
    }
}
