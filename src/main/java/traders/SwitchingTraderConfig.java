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
import sesim.AutoTrader_old;
import sesim.Exchange;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SwitchingTraderConfig extends RandomTraderConfig_old {

    @Override
    public AutoTrader_old createTrader(Exchange se, long shares, double money) {
        Account_old a = new Account_old(se, shares, money);
        System.out.print("Returning a new sw trader\n");
        return new SwitchingTrader(a, this);
    }

    public SwitchingTraderConfig() {

        sell_volume = new float[]{100, 100};
        sell_limit = new float[]{-30, 1};
        sell_order_wait = new int[]{1, 5};
        wait_after_sell = new int[]{1, 5};

        buy_volume = new float[]{100, 100};
        buy_limit = new float[]{-1, 30};
        buy_order_wait = new int[]{1, 5};
        wait_after_buy = new int[]{1, 5};
    }
}
