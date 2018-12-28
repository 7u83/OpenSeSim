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
import opensesim.sesim.Assets.CurrencyAsset;
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
public class TradingEngineTest {
    
    public TradingEngineTest() {
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
     * Test of getAssetPair method, of class TradingEngine.
     */
    @Test
    public void testGetAssetPair() {
        System.out.println("getAssetPair");
        TradingEngine instance = null;
        AssetPair expResult = null;
        AssetPair result = instance.getAssetPair();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class TradingEngine.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        TradingEngine instance = null;
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBestPrice method, of class TradingEngine.
     */
    @Test
    public void testGetBestPrice() {
        System.out.println("getBestPrice");
        TradingEngine instance = null;
        Double expResult = null;
        Double result = instance.getBestPrice();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createOrder method, of class TradingEngine.
     */
    @Test
    public void testCreateOrder() {
        System.out.println("createOrder");
        GodWorld gdworld = new GodWorld();
        
     //   AbstractAsset currency =  gdworld.createAsset(cfg);
        
        Account account = new Account();
        
        
        Order.Type type = null;
        double volume = 0.0;
        double limit = 0.0;
        TradingEngine instance = null;
        Order expResult = null;
        Order result = instance.createOrder(account, type, volume, limit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addOrderBookListener method, of class TradingEngine.
     */
    @Test
    public void testAddOrderBookListener() {
        System.out.println("addOrderBookListener");
        EventListener listener = null;
        TradingEngine instance = null;
        instance.addOrderBookListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderBook method, of class TradingEngine.
     */
    @Test
    public void testGetOrderBook() {
        System.out.println("getOrderBook");
        Order.Type type = null;
        TradingEngine instance = null;
        Set expResult = null;
        Set result = instance.getOrderBook(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBidBook method, of class TradingEngine.
     */
    @Test
    public void testGetBidBook() {
        System.out.println("getBidBook");
        TradingEngine instance = null;
        Set expResult = null;
        Set result = instance.getBidBook();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAskBook method, of class TradingEngine.
     */
    @Test
    public void testGetAskBook() {
        System.out.println("getAskBook");
        TradingEngine instance = null;
        Set expResult = null;
        Set result = instance.getAskBook();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
