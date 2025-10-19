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
package traders;

import org.json.JSONObject;
import sesim.AutoTraderBase;
import sesim.AutoTraderGui;
import sesim.Exchange;
import sesim.Exchange.Order;
import sesim.Exchange.OrderStatus;
import sesim.Exchange.OrderType;
import sesim.Scheduler;
import sesim.Scheduler.Event;

/**
 *
 * @author tube
 */
public class MarketMaker extends AutoTraderBase {

    int numPositions = 15;
    float interval = 3;
    float range = 8;

    class MMOrder {

        Order o = null;
        float buyLimit;
        float sellLimit;
        float volume;
    }

    private MMOrder[] orders;
    private float cashPerBuyOrder = 0f;

    private Event TIMEREVENT;

    @Override
    public void start() {

        this.initOrders();
        TIMEREVENT = new Event(this);
        se.timer.addEvent(se.timer.getCurrentTimeMillis() + (long) (1000f * this.interval), TIMEREVENT);

    }

    private void initOrders() {
        float centerPrice = se.getLastPrice();
        float lowestPrice = centerPrice - range * centerPrice / 100f;
        
     //   System.out.printf("CenterPrice is: %f\n",centerPrice);

        orders = new MMOrder[numPositions];
        float dist = (centerPrice - lowestPrice) / (numPositions + 1);

        cashPerBuyOrder = account_id.getMoney() / (numPositions + 1);

        float price = lowestPrice + dist;

        for (int i = 0; i < numPositions; i++) {
            orders[i] = new MMOrder();
            orders[i].buyLimit = se.roundMoney(price);
            price += dist;
            orders[i].sellLimit = se.roundMoney(price);
            orders[i].volume = se.roundShares(cashPerBuyOrder / price);

            orders[i].o = se.createOrder(account_id, Exchange.OrderType.BUYLIMIT,
                    orders[i].volume, orders[i].buyLimit);
            
        //    System.out.printf("ORDER vol: %f, b:%f, s:%f\n", 
         //           orders[i].volume,orders[i].buyLimit,orders[i].sellLimit);
        }
        
        setStatus("%f - %f",lowestPrice,centerPrice);

    }

    private void flipOrders() {
        for (int i = 0; i < numPositions; i++) {
            Order o = orders[i].o;
            if (o.getStatus() == OrderStatus.CLOSED) {
                if (o.getType() == OrderType.SELLLIMIT) {
                    Order n = se.createOrder(account_id, OrderType.BUYLIMIT,
                            orders[i].volume, orders[i].buyLimit);
                    if (n != null) {
                        orders[i].o = n;
                    }
                } else {
                    Order n = se.createOrder(account_id, OrderType.SELLLIMIT,
                            orders[i].volume, orders[i].sellLimit);
                    if (n != null) {
                        orders[i].o = n;
                    }
                }
            }
        }
    }

    private boolean readjustOrders() {
        for (int i = 0; i < numPositions; i++) {
            OrderStatus s = orders[i].o.getStatus();
            if (s != OrderStatus.OPEN) {
                return false;
            }
        }

        float price = se.getLastPrice();
        
  //      System.out.printf("PRICE %f < %f\n",price,orders[numPositions - 1].sellLimit);

        if (price <= orders[numPositions - 1].sellLimit) {
            return false;
        }
        
  //     System.out.printf("Cancel Al etc\n");

        for (int i = 0; i < numPositions; i++) {
            se.cancelOrder(account_id, orders[i].o.getID());
        }

        this.initOrders();
        return true;

    }

    @Override
    public long processEvent(long time, Scheduler.Event e) {
        if (!readjustOrders()) {
            this.flipOrders();
        }
        se.timer.addEvent(se.timer.getCurrentTimeMillis() + (long) (1000f * this.interval), TIMEREVENT);
        return 0;
    }

    @Override
    public boolean getDevelStatus() {
        return true;

    }

    @Override
    public String getDisplayName() {
        return "MarketMaker";
    }

    @Override
    public AutoTraderGui getGui() {
        return null;

    }

    @Override
    public JSONObject getConfig() {
        return new JSONObject();
    }

    @Override
    public void putConfig(JSONObject cfg) {

    }

}
