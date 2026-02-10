/*
 * Copyright (currency) 2017, 2025 7u83 <7u83@mail.ru>
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

import java.util.Map;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tube
 */
public class AccountTest {

    Sim sim;
    Market market;
    Account account;
    double initialCash = 10000.0;

    public AccountTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }
    AssetBase currency;
    AssetBase asset;

    @BeforeEach
    public void setUp() {
        // Create sim
        sim = new sesim.Sim();

        // Create assets
        currency = new AssetBase("TLR", "Taler", 2);
        asset = new AssetBase("RBTN", "RBTN", 0);

        // Create market and an account
        market = new Market(sim, currency, asset, new JSONObject());
        account = new sesim.Account(currency, initialCash);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of makeSnapShot method, of class Account.
     */
    @Test
    public void testMakeSnapShot() {
        System.out.println("makeSnapShot");
        Account instance = null;
        instance.makeSnapShot();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPositions method, of class Account.
     */
    @Test
    public void testGetPositions() {
        System.out.println("getPositions");
        Account instance = null;
        Map<Market, Position> expResult = null;
        Map<Market, Position> result = instance.getPositions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cancelAllOrders method, of class Account.
     */
    @Test
    public void testCancelAllOrders() {
        System.out.println("cancelAllOrders");
        Account instance = null;
        instance.cancelAllOrders();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMarginUsed_Long method, of class Account.
     */
    @Test
    public void testGetMarginUsed_Long() {
        System.out.println("getMarginUsed_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getMarginUsed_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMarginUsed method, of class Account.
     */
    @Test
    public void testGetMarginUsed() {
        System.out.println("getMarginUsed");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getMarginUsed();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShares method, of class Account.
     */
    @Test
    public void testGetShares() {
        System.out.println("getShares");
        Market m = market;
        Account instance = account;
        double expResult = 0.0;
        double result = instance.getShares(m);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShares_Long method, of class Account.
     */
    @Test
    public void testGetShares_Long() {
        System.out.println("getShares_Long");
        Market m = market;
        Account instance = account;
        long expResult = 0L;
        long result = instance.getShares_Long(m);
        assertEquals(expResult, result);

        account.getPosition(market).addShares_Long(1000, 100, 1);
        
        result = instance.getShares_Long(m);
        assertEquals(1000, result);
    }

    /**
     * Test of getMoney method, of class Account.
     */
    @Test
    public void testGetMoney() {
        System.out.println("getMoney");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getMoney();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMoney_Long method, of class Account.
     */
    @Test
    public void testGetMoney_Long() {
        System.out.println("getMoney_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getMoney_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOwner method, of class Account.
     */
    @Test
    public void testGetOwner() {
        System.out.println("getOwner");
        Account instance = null;
        AutoTrader expResult = null;
        AutoTrader result = instance.getOwner();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrders method, of class Account.
     */
    @Test
    public void testGetOrders() {
        System.out.println("getOrders");
        Account instance = null;
        Map<Long, Order> expResult = null;
        Map<Long, Order> result = instance.getOrders();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setListener method, of class Account.
     */
    @Test
    public void testSetListener() {
        System.out.println("setListener");
        Market.AccountListener al = null;
        Account instance = null;
        instance.setListener(al);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class Account.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Order o = null;
        Account instance = null;
        instance.update(o);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfOpenOrders method, of class Account.
     */
    @Test
    public void testGetNumberOfOpenOrders() {
        System.out.println("getNumberOfOpenOrders");
        Account instance = null;
        int expResult = 0;
        int result = instance.getNumberOfOpenOrders();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCashInOpenOrders_Long method, of class Account.
     */
    @Test
    public void testGetCashInOpenOrders_Long_long() {
        System.out.println("getCashInOpenOrders_Long");
        long exclude = 0L;
        Account instance = null;
        long expResult = 0L;
        long result = instance.getCashInOpenOrders_Long(exclude);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCashInOpenOrders_Long method, of class Account.
     */
    @Test
    public void testGetCashInOpenOrders_Long_0args() {
        System.out.println("getCashInOpenOrders_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getCashInOpenOrders_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCashInOpenOrders method, of class Account.
     */
    @Test
    public void testGetCashInOpenOrders() {
        System.out.println("getCashInOpenOrders");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getCashInOpenOrders();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSharesInOpenOrders_Long method, of class Account.
     */
    @Test
    public void testGetSharesInOpenOrders_Long_long() {
        System.out.println("getSharesInOpenOrders_Long");
        long exclude = 0L;
        Account instance = null;
        long expResult = 0L;
        long result = instance.getSharesInOpenOrders_Long(exclude);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSharesInOpenOrders_Long method, of class Account.
     */
    @Test
    public void testGetSharesInOpenOrders_Long_0args() {
        System.out.println("getSharesInOpenOrders_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getSharesInOpenOrders_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSharesInOpenOrders method, of class Account.
     */
    @Test
    public void testGetSharesInOpenOrders() {
        System.out.println("getSharesInOpenOrders");
        Account instance = null;
        float expResult = 0.0F;
        float result = instance.getSharesInOpenOrders();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCashAvailabale_Long method, of class Account.
     */
    @Test
    public void testGetCashAvailabale_Long() {
        System.out.println("getCashAvailabale_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getCashAvailabale_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCashAvailable method, of class Account.
     */
    @Test
    public void testGetCashAvailable() {
        System.out.println("getCashAvailable");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getCashAvailable();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderByID method, of class Account.
     */
    @Test
    public void testGetOrderByID() {
        System.out.println("getOrderByID");
        long oid = 0L;
        Account instance = null;
        Order expResult = null;
        Order result = instance.getOrderByID(oid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isOrderCovered_Long method, of class Account.
     */
    @Test
    public void testIsOrderCovered_Long_4args_1() {
        System.out.println("isOrderCovered_Long");
        Position p = null;
        long volume = 0L;
        long price = 0L;
        int leverage = 0;
        Account instance = null;
        boolean expResult = false;
        boolean result = instance.isOrderCovered_Long(p, volume, price, leverage);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isOrderCovered_Long method, of class Account.
     */
    @Test
    public void testIsOrderCovered_Long_4args_2() {
        System.out.println("isOrderCovered_Long");
        Market market = null;
        long volume = 0L;
        long price = 0L;
        int leverage = 0;
        Account instance = null;
        boolean expResult = false;
        boolean result = instance.isOrderCovered_Long(market, volume, price, leverage);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isOrderCovered method, of class Account.
     */
    @Test
    public void testIsOrderCovered() {
        System.out.println("isOrderCovered");
        Market market = null;
        double volume = 0.0;
        double price = 0.0;
        int leverage = 0;
        Account instance = null;
        boolean expResult = false;
        boolean result = instance.isOrderCovered(market, 0, volume, price, leverage);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRequiredCashForOrder method, of class Account.
     */
    @Test
    public void testGetRequiredCashForOrder() {
        System.out.println("getRequiredCashForOrder");
        Market market = null;
        double volume = 0.0;
        double price = 0.0;
        int leverage = 0;
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getRequiredCashForOrder(market, volume, price, leverage);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPerformance method, of class Account.
     */
    @Test
    public void testGetPerformance() {
        System.out.println("getPerformance");
        double lastPrice = 0.0;
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getPerformance(lastPrice);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEquity_Long method, of class Account.
     */
    @Test
    public void testGetEquity_Long_0args() {
        System.out.println("getEquity_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getEquity_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEquity_Long method, of class Account.
     */
    @Test
    public void testGetEquity_Long_long() {
        System.out.println("getEquity_Long");
        long price = 0L;
        Account instance = null;
        long expResult = 0L;
        long result = instance.getEquity_Long(price);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCash method, of class Account.
     */
    @Test
    public void testGetCash() {
        System.out.println("getCash");
        Account instance = new Account(currency, 10000.0);
        double expResult = 10000.0;
        double result = instance.getCash();
        assertEquals(expResult, result, 0);

    }

    /**
     * Test of getEquity method, of class Account.
     */
    @Test
    public void testGetEquity() {
        System.out.println("getEquity");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getEquity();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSnapshotEquity_Long method, of class Account.
     */
    @Test
    public void testGetSnapshotEquity_Long() {
        System.out.println("getSnapshotEquity_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getSnapshotEquity_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSnapShotEquity method, of class Account.
     */
    @Test
    public void testGetSnapShotEquity() {
        System.out.println("getSnapShotEquity");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getSnapShotEquity();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFreeMargin_Long method, of class Account.
     */
    @Test
    public void testGetFreeMargin_Long() {
        System.out.println("getFreeMargin_Long");
        Account instance = null;
        long expResult = 0L;
        long result = instance.getFreeMargin_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFreeMargin method, of class Account.
     */
    @Test
    public void testGetFreeMargin() {
        System.out.println("getFreeMargin");
        Account instance = null;
        double expResult = 0.0;
        double result = instance.getFreeMargin();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPosition method, of class Account.
     */
    @Test
    public void testGetPosition() {
        System.out.println("getPosition");
        Market asset = null;
        Account instance = null;
        Position expResult = null;
        Position result = instance.getPosition(asset);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isLiquidated method, of class Account.
     */
    @Test
    public void testIsLiquidated() {
        System.out.println("isLiquidated");
        Account instance = null;
        boolean expResult = false;
        boolean result = instance.isLiquidated();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateLiquidationStops method, of class Account.
     */
    @Test
    public void testCalculateLiquidationStops() {
        System.out.println("calculateLiquidationStops");
        long lastPrice = 0L;
        Account instance = null;
        instance.calculateLiquidationStops(lastPrice);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrency method, of class Account.
     */
    @Test
    public void testGetCurrency() {
        System.out.println("getCurrency");
        Account instance = null;
        Asset expResult = null;
        Asset result = instance.getCurrency();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
