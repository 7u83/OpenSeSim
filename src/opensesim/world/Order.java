/*
 * Copyo (c) 2018, 7u83 <7u83@mail.ru>
 * All os reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyo notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyo notice,
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
package opensesim.world;

import opensesim.util.idgenerator.Id;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Order implements Comparable<Order> {

    @Override
    public int compareTo(Order o) {
        {
            double d;
            switch (this.type) {
                case BUYLIMIT:
                case STOPBUY:
                case BUY:
                    d = o.limit - this.limit;
                    break;
                case SELLLIMIT:
                case STOPLOSS:
                case SELL:
                    d = this.limit - o.limit;
                    break;
                default:
                    d = 0;

            }
            if (d != 0) {
                return d > 0 ? 1 : -1;
            }

            d = o.initial_volume - this.initial_volume;
            if (d != 0) {
                return d > 0 ? 1 : -1;
            }

            return this.id.compareTo(o.id);
        }
    }

    /**
     * Definition of order status
     */
    public static enum Status {
        OPEN, PARTIALLY_EXECUTED, CLOSED, CANCELED, ERROR
    }
    
    String message="";

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    
    
    /**
     * Definition of order types
     */
    public static enum Type {
        BUYLIMIT, SELLLIMIT, STOPLOSS, STOPBUY, BUY, SELL
    }

    public static enum Addition {
        NONE, MARGIN_CALL, FILL_OR_KILL
    }

    Addition addition;
    protected Status status;
    protected Type type;

    protected double limit;
    protected double volume;

    protected final double initial_volume;
    protected final Id id;
    protected final long created;

    protected final AccountImpl account;

    double cost;
    GodWorld world;

    Order(TradingEngine engine, AccountImpl account, Type type,
            double volume, double limit, Addition addition) {
 
        // Assign volume and initial volume
        this.volume = volume;
        this.initial_volume = volume;
        
        // limit
        this.limit = limit;

        this.account = account;
        this.type = type;

        this.created = 0;
        this.status = Status.OPEN;
        this.cost = 0;

        id = engine.id_generator.getNext();
        this.addition = addition;
    }

    Order(opensesim.world.TradingEngine engine, AccountImpl account, Type type,
            double volume, double limit) {
        this(engine, account, type, volume, limit, Addition.NONE);
    }

    public Id getID() {
        return id;
    }

    public double getVolume() {
        return volume;
    }

    public double getLimit() {
        return limit;
    }

    public Type getType() {
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

    public AccountImpl getAccount() {
        return account;
    }

    public Status getOrderStatus() {
        return status;
    }

    public long getCreated() {
        return created;
    }

}
