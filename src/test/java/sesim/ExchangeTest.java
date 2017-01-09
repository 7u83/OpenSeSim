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

import java.util.ArrayList;
import java.util.SortedSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tobias
 */
public class ExchangeTest {
    
    public ExchangeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createAccount method, of class Exchange.
     */
    @org.junit.Test
    public void testCreateAccount() {
        System.out.println("createAccount");
        double money = 0.0;
        double shares = 0.0;
        Exchange instance = new Exchange();
        double expResult = 0.0;
        double result = instance.createAccount(money, shares);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentTimeSeconds method, of class Exchange.
     */
    @org.junit.Test
    public void testGetCurrentTimeSeconds() {
        System.out.println("getCurrentTimeSeconds");
        long expResult = 0L;
        long result = Exchange.getCurrentTimeSeconds();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuoteHistory method, of class Exchange.
     */
    @org.junit.Test
    public void testGetQuoteHistory() {
        System.out.println("getQuoteHistory");
        long start = 0L;
        Exchange instance = new Exchange();
        SortedSet<Quote> expResult = null;
        SortedSet<Quote> result = instance.getQuoteHistory(start);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addBookReceiver method, of class Exchange.
     */
    @org.junit.Test
    public void testAddBookReceiver() {
        System.out.println("addBookReceiver");
        Order.OrderType t = null;
        Exchange.BookReceiver br = null;
        Exchange instance = new Exchange();
        instance.addBookReceiver(t, br);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateBookReceivers method, of class Exchange.
     */
    @org.junit.Test
    public void testUpdateBookReceivers() {
        System.out.println("updateBookReceivers");
        Order.OrderType t = null;
        Exchange instance = new Exchange();
        instance.updateBookReceivers(t);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addQuoteReceiver method, of class Exchange.
     */
    @org.junit.Test
    public void testAddQuoteReceiver() {
        System.out.println("addQuoteReceiver");
        Exchange.QuoteReceiver qr = null;
        Exchange instance = new Exchange();
        instance.addQuoteReceiver(qr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderBook method, of class Exchange.
     */
    @org.junit.Test
    public void testGetOrderBook() {
        System.out.println("getOrderBook");
        Order.OrderType t = null;
        int depth = 0;
        Exchange instance = new Exchange();
        ArrayList<Order> expResult = null;
        ArrayList<Order> result = instance.getOrderBook(t, depth);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print_current method, of class Exchange.
     */
    @org.junit.Test
    public void testPrint_current() {
        System.out.println("print_current");
        Exchange instance = new Exchange();
        instance.print_current();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transferMoney method, of class Exchange.
     */
    @org.junit.Test
    public void testTransferMoney() {
        System.out.println("transferMoney");
        Account src = null;
        Account dst = null;
        double money = 0.0;
        Exchange instance = new Exchange();
        instance.transferMoney(src, dst, money);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transferMoneyAndShares method, of class Exchange.
     */
    @org.junit.Test
    public void testTransferMoneyAndShares() {
        System.out.println("transferMoneyAndShares");
        Account src = null;
        Account dst = null;
        double money = 0.0;
        long shares = 0L;
        Exchange instance = new Exchange();
        instance.transferMoneyAndShares(src, dst, money, shares);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cancelOrder method, of class Exchange.
     */
    @org.junit.Test
    public void testCancelOrder() {
        System.out.println("cancelOrder");
        Order o = null;
        Exchange instance = new Exchange();
        instance.cancelOrder(o);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transferShares method, of class Exchange.
     */
    @org.junit.Test
    public void testTransferShares() {
        System.out.println("transferShares");
        Account src = null;
        Account dst = null;
        long volume = 0L;
        double price = 0.0;
        Exchange instance = new Exchange();
        instance.transferShares(src, dst, volume, price);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeOrders method, of class Exchange.
     */
    @org.junit.Test
    public void testExecuteOrders() {
        System.out.println("executeOrders");
        Exchange instance = new Exchange();
        instance.executeOrders();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ExecuteOrder method, of class Exchange.
     */
    @org.junit.Test
    public void testExecuteOrder() {
        System.out.println("ExecuteOrder");
        BuyOrder o = null;
        Exchange instance = new Exchange();
        instance.ExecuteOrder(o);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SendOrder method, of class Exchange.
     */
    @org.junit.Test
    public void testSendOrder() {
        System.out.println("SendOrder");
        Order o = null;
        Exchange instance = new Exchange();
        Order expResult = null;
        Order result = instance.SendOrder(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getlastprice method, of class Exchange.
     */
    @org.junit.Test
    public void testGetlastprice() {
        System.out.println("getlastprice");
        Exchange instance = new Exchange();
        double expResult = 0.0;
        double result = instance.getlastprice();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class Exchange.
     */
    @org.junit.Test
    public void testRun() {
        System.out.println("run");
        Exchange instance = new Exchange();
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
