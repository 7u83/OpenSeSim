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

/**
 *
 * @author tobias
 */
public class Test {

    static void print_account(AccountData ad) {
        System.out.print(
                "Account ID:"
                + ad.id
                + " Ballance:"
                + ad.money
                + " Shares:"
                + ad.shares
                + "\n"
        );
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Exchange se = new Exchange();

        double aid1 = se.createAccount(100, 100);
        double aid2 = se.createAccount(100, 100);

        AccountData a1 = se.getAccountData(aid1);
        AccountData a2 = se.getAccountData(aid2);
        Test.print_account(a1);
        Test.print_account(a2);

        se.createOrder(aid2, Exchange.OrderType.ASK, 20, 11);
        se.createOrder(aid2, Exchange.OrderType.ASK, 10, 10);
        se.createOrder(aid2, Exchange.OrderType.ASK, 10, 9);
        se.createOrder(aid1, Exchange.OrderType.BID, 50, 11);

        System.out.print("Exec Orders\n");
        se.executeOrders();
        System.out.print("Executed Orders\n");

        a1 = se.getAccountData(aid1);
        a2 = se.getAccountData(aid2);
        Test.print_account(a1);
        Test.print_account(a2);
        
        
        

        //S/ystem.out.print(aid);
        //System.out.print("\n");
    }

}
