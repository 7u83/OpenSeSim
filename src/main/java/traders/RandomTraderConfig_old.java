/*
 * Copyright (c) 2016, 7u83 <7u83@mail.ru>
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

import sesim.Account_old;
import sesim.TraderConfig_old;
import sesim.Exchange;
import sesim.AutoTrader_old;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderConfig_old extends TraderConfig_old {

    //public long maxage = 1000 * 10 * 1;
    
    
    /*public long hold_shares_min = 10;
    
    public long hold_shares_max = 30;

    public float buy_volume_min = 10;
    */
    
    /**
     * If shares are selled, this specifies
     * the minimum and maximum volume to be selled
     */
    public float[] sell_volume= {100,100};
    public float[] sell_limit = {-15,15};
    public int[] sell_order_wait = {15,33};
    public int[] wait_after_sell = {10,30};

    
    public float[] buy_volume={100,100};
    public float[] buy_limit = {-15,15};
    public int[] buy_order_wait = {15,33};    
    public int[] wait_after_buy = {10,30};

    
    @Override
    public AutoTrader_old createTrader(Exchange se, long shares, double money) {
        Account_old a = new Account_old(se, shares, money);
        return new RandomTrader_old(a, this);
    }
  
    

}