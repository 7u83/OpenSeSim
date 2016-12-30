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
package Traders;

import SeSim.Account;
import SeSim.TraderConfig;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SwitchingTrader extends RandomTrader{
    
    
    private Action mode;
    
    public SwitchingTrader(Account account, TraderConfig config) {
               
        super(account, config);
        System.out.print("SWTrader Created\n");
        
        if (account.shares>0)
            mode=Action.sell;
        else
            mode=Action.buy;
        printstartus();
                
    }
    
    
    private void printstartus(){
        
        System.out.print("SWTrader:");        
        switch (mode){
            case buy:
                System.out.print("buy"
                        +account.shares
                        +" "
                        +account.money
                );
                break;
            case sell:
                System.out.print("sell"
                        +account.shares
                        +" "
                        +account.money
                );
                break;
                
                
        }
        System.out.print("\n");
                
    }
    
    @Override
    protected Action getAction(){

        
        
        
        if ( (account.shares>0) && (mode==Action.sell)){
            printstartus();
            return mode;
        }
        if ( (account.shares<=0 && mode==Action.sell)){
            mode=Action.buy;
            printstartus();
            return mode;
        }
        if (account.money>100.0 && mode==Action.buy){
            printstartus();
            return mode;
        }        
        if (account.money<=100.0 && mode==Action.buy){
            mode=Action.sell;
            printstartus();
            return mode;
        }
        printstartus();
        return mode;
    }
    
}
