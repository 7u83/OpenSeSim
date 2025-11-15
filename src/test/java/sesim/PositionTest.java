/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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
public class PositionTest {

    Account account;
    Exchange se;

    Sim sim;

    public PositionTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    float iniCash = 1000;
    float iniShares = 10;

    @BeforeEach
    public void setUp() {
        sim = new sesim.Sim();
        se = new Exchange(sim);
        se.setSymbol("SYM");
        account = new sesim.Account(se, iniCash, iniShares);

    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getName method, of class Position.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Position instance = new sesim.Position(se, account);
        String expResult = "SYM";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getShares method, of class Position.
     */
    @Test
    public void testGetShares() {
        System.out.println("getShares");
        Position instance = new sesim.Position(se, account);
        float expResult = 0.0F;
        float result = instance.getShares();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        //   fail("The test case is a prototype.");
    }

    /**
     * Test of getLeverage method, of class Position.
     */
    @Test
    public void testGetLeverage() {
        System.out.println("getLeverage");
        Position instance = new sesim.Position(se, account);
        float expResult = 1.0F;
        float result = instance.getLeverage();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getMargin method, of class Position.
     */
    @Test
    public void testGetMargin() {
        System.out.println("getMargin");
        Position instance = new sesim.Position(se, account);
        float expResult = 0.0F;
        float result = instance.getMargin();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        //   fail("The test case is a prototype.");
    }

    /**
     * Test of getMargin_Long method, of class Position.
     */
    @Test
    public void testGetMargin_Long() {
        System.out.println("getMargin_Long");
        Position instance = new sesim.Position(se, account);
        long expResult = 0L;
        long result = instance.getMargin_Long();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntryPrice method, of class Position.
     */
    @Test
    public void testGetEntryPrice() {
        System.out.println("getEntryPrice");
        Position instance = null;
        float expResult = 0.0F;
        float result = instance.getEntryPrice();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getExposure method, of class Position.
     */
    @Test
    public void testGetExposure() {
        System.out.println("getExposure");
        Position instance = null;
        long expResult = 0L;
        long result = instance.getExposure();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPnL_Long method, of class Position.
     */
    @Test
    public void testGetPnL_Long() {
        System.out.println("getPnL_Long");
        long currentPrice = 0L;
        Position instance = null;
        long expResult = 0L;
        long result = instance.getPnL_Long(currentPrice);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPnL method, of class Position.
     */
    @Test
    public void testGetPnL() {
        System.out.println("getPnL");

        Position instance = new sesim.Position(se, account);
        se.lastQuote = new sesim.Quote(se);

        se.lastQuote.price = (long) (100 * se.money_df);
        instance.addShares(100, (long) (100 * se.money_df), 1);

        float r;
        r = instance.getPnL();
        assertEquals(0.0f, r, 0);

        se.lastQuote.price = (long) (130 * se.money_df);
        r = instance.getPnL();
        assertEquals(3000.0f, r, 0);

        instance.addShares(100, (long) (50 * se.money_df), 1);
        se.lastQuote.price = (long) (100 * se.money_df);
        r = instance.getPnL();
        assertEquals(5000.0f, r, 0);

  /*      instance.addShares(-50, (long) (100 * se.money_df), 1);
        r = instance.getPnL();
        assertEquals(0.0f, r, 0);*/

        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getPnLPercent method, of class Position.
     */
    @Test
    public void testGetPnLPercent() {
        System.out.println("getPnLPercent");
        Position instance = null;
        float expResult = 0.0F;
        float result = instance.getPnLPercent();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalEntryCost method, of class Position.
     */
    @Test
    public void testGetTotalEntryCost() {
        System.out.println("getTotalEntryCost");
        Position instance = new sesim.Position(se, account);

        instance.addShares(1, (long) (50 * se.money_df), 1);
        assertEquals(50, instance.getTotalEntryCost(), 0);
        // TODO review the generated test code and remove the default call to fail.
        //  fail("The test case is a prototype.");
    }

    /**
     * Test of getShadowCash method, of class Position.
     */
    @Test
    public void testGetShadowCash() {
        System.out.println("getShadowCash");
        Position instance = null;
        float expResult = 0.0F;
        float result = instance.getShadowCash();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNetBrokerLoan method, of class Position.
     */
    @Test
    public void testGetNetBrokerLoan() {
        System.out.println("getNetBrokerLoan");
        Position instance = null;
        float expResult = 0.0F;
        float result = instance.getNetBrokerLoan();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addShares method, of class Position.
     */
    @Test
    public void testAddShares() {
        System.out.println("addShares");

        Position instance;

        instance = new sesim.Position(se, account);

        instance.addShares(15, (long) (20 * se.money_df), 1);
        assertEquals(15f, instance.getShares(), 0);
        assertEquals(0.0f, instance.getMargin(), 0);
        assertEquals(300f, instance.getTotalEntryCost(), 0);
        assertEquals(iniCash - 15 * 20, account.getCash(), 0);

        instance.addShares(15, (long) (20 * se.money_df), 1);
        assertEquals(30f, instance.getShares(), 0);
        assertEquals(0.0f, instance.getMargin(), 0);
        assertEquals(600f, instance.getTotalEntryCost(), 0);
        assertEquals(iniCash - 2 * 15 * 20, account.getCash(), 0);

        instance.addShares(-5, (long) (10 * se.money_df), 1);
        assertEquals(25f, instance.getShares(), 0);
        assertEquals(0.0f, instance.getMargin(), 0);
        assertEquals(550f, instance.getTotalEntryCost(), 0);
        assertEquals(iniCash - 2 * 15 * 20 + 50, account.getCash(), 0);

        setUp();

        instance = new sesim.Position(se, account);

        instance.addShares(3, (long) (100 * se.money_df), 1);
        assertEquals(3f, instance.getShares(), 0);
        assertEquals(0.0f, instance.getMargin(), 0);
        assertEquals(iniCash - 300, account.getCash(), 0);
        
        se.lastQuote = new sesim.Quote(se);

        se.lastQuote.price = (long) (50 * se.money_df);

        instance.addShares(-4, (long) (50 * se.money_df), 1);
        assertEquals(-1f, instance.getShares(), 0);
        assertEquals(50f, instance.getMargin(), 0);
        assertEquals(iniCash - 300 + 150, account.getCash(), 0);
        
          
        
        instance = new sesim.Position(se, account);
          
          

        // TODO review the generated test code and remove the default call to fail.
        //     fail("The test case is a prototype.");
    }

    /**
     * Test of getRequiredCashForOrder method, of class Position.
     */
    @Test
    public void testGetRequiredCashForOrder() {
        System.out.println("getRequiredCashForOrder");

        Position instance = new sesim.Position(se, account);

        long result;
        result = instance.getRequiredCashForOrder_Long(-10, 30, 1);
        assertEquals(300, result);
        result = instance.getRequiredCashForOrder_Long(10, 30, 1);
        assertEquals(300, result);
        instance.addShares(10, 30, 1);
        result = instance.getRequiredCashForOrder_Long(-10, 30, 1);
        assertEquals(0, result);
        result = instance.getRequiredCashForOrder_Long(10, 30, 1);
        assertEquals(300, result);
       result = instance.getRequiredCashForOrder_Long(-11, 30, 1);
        assertEquals(30, result);
        result = instance.getRequiredCashForOrder_Long(-11, 30, 10);
        assertEquals(3, result);
         instance.addShares(-20, 30, 1);
        result = instance.getRequiredCashForOrder_Long(10, 30, 1);
        assertEquals(0, result);
        result = instance.getRequiredCashForOrder_Long(11, 30, 10);
        assertEquals(3, result);
         
        
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

}
