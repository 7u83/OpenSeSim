/*
 * Copyright (c) 2018, tube
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
package opensesim.world;

import java.util.Set;
import opensesim.util.scheduler.EventListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tube
 */
public class TradingAPITest {
    
    public TradingAPITest() {
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
     * Test of addOrderBookListener method, of class TradingAPI.
     */
    @Test
    public void testAddOrderBookListener() {
        System.out.println("addOrderBookListener");
        EventListener listener = null;
        TradingAPI instance = new TradingAPIImpl();
        instance.addOrderBookListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createOrder method, of class TradingAPI.
     */
    @Test
    public void testCreateOrder() {
        System.out.println("createOrder");
        Account account = null;
        Order.Type type = null;
        double volume = 0.0;
        double limit = 0.0;
        TradingAPI instance = new TradingAPIImpl();
        Order expResult = null;
        Order result = instance.createOrder(account, type, volume, limit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBidBook method, of class TradingAPI.
     */
    @Test
    public void testGetBidBook() {
        System.out.println("getBidBook");
        TradingAPI instance = new TradingAPIImpl();
        Set expResult = null;
        Set result = instance.getBidBook();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAskBook method, of class TradingAPI.
     */
    @Test
    public void testGetAskBook() {
        System.out.println("getAskBook");
        TradingAPI instance = new TradingAPIImpl();
        Set expResult = null;
        Set result = instance.getAskBook();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderBook method, of class TradingAPI.
     */
    @Test
    public void testGetOrderBook() {
        System.out.println("getOrderBook");
        Order.Type type = null;
        TradingAPI instance = new TradingAPIImpl();
        Set expResult = null;
        Set result = instance.getOrderBook(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAssetPair method, of class TradingAPI.
     */
    @Test
    public void testGetAssetPair() {
        System.out.println("getAssetPair");
        TradingAPI instance = new TradingAPIImpl();
        AssetPair expResult = null;
        AssetPair result = instance.getAssetPair();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class TradingAPIImpl implements TradingAPI {

        public void addOrderBookListener(EventListener listener) {
        }

        public Order createOrder(Account account, Order.Type type, double volume, double limit) {
            return null;
        }

        public Set getBidBook() {
            return null;
        }

        public Set getAskBook() {
            return null;
        }

        public Set getOrderBook(Order.Type type) {
            return null;
        }

        public AssetPair getAssetPair() {
            return null;
        }
    }
    
}
