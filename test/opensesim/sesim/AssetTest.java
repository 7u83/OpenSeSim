/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.sesim;

import opensesim.sesim.Assets.BasicAsset;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetTest {

    public AssetTest() {
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
     * Test of getSymbol method, of class Asset.
     */
    @Test
    public void testGetSymbol() {
        System.out.println("getSymbol");
        BasicAsset instance = new BasicAsset();
        String expResult = "EUR";
        instance.setSymbol(expResult);
        String result = instance.getSymbol();
        assertEquals(expResult, result);

    }

    /**
     * Test of getConfig method, of class Asset.
     */
    @Test
    public void testGetConfig() {
        System.out.println("getConfig");
        BasicAsset instance = new BasicAsset();

        int decimals = 17;
        String symbol = "EUR";
        String description = "Eruo - Europaen currency";
        String name = "Euro";

        instance.setDecimals(decimals);
        instance.setName(name);
        instance.setDescription(description);
        instance.setSymbol(symbol);

        JSONObject result = instance.getConfig();

        assertEquals(symbol, result.getString(BasicAsset.JSON_SYMBOL));
        assertEquals(name, result.getString(BasicAsset.JSON_NAME));
        assertEquals(description, result.getString(BasicAsset.JSON_DESCRIPTION));
        assertEquals(decimals, result.getInt(BasicAsset.JSON_DECIMALS));

    }

    /**
     * Test of putConfig method, of class Asset.
     */
    @Test
    public void testPutConfig() {
        System.out.println("putConfig");

        int decimals = 17;
        String symbol = "EUR";
        String name = "Euro";
        String description = "Eruo - Europaen currency";
     
        JSONObject cfg = new JSONObject();
        cfg.put(BasicAsset.JSON_SYMBOL, symbol);
        cfg.put(BasicAsset.JSON_NAME, name);
        cfg.put(BasicAsset.JSON_DESCRIPTION, description);
        cfg.put(BasicAsset.JSON_DECIMALS, decimals);
        
        BasicAsset instance = new BasicAsset();
        instance.putConfig(cfg);

        assertEquals(symbol,instance.getSymbol());
        assertEquals(name,instance.getName());
        assertEquals(description,instance.getDescription());        
        assertEquals(decimals,instance.getDecimals());

    }

    /**
     * Test of setSymbol method, of class Asset.
     */
    @Test
    public void testSetSymbol() {
        System.out.println("setSymbol");
        String symbol = "EUR";
        BasicAsset instance = new BasicAsset();
        instance.setSymbol(symbol);
        String result = instance.getSymbol();
        assertEquals(symbol, result);

    }

    /**
     * Test of getName method, of class Asset.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        BasicAsset instance = new BasicAsset();
        String expResult = "Euro";
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of setName method, of class Asset.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "Euro";
        BasicAsset instance = new BasicAsset();
        instance.setName(name);
        String result = instance.getName();
        assertEquals(name, result);

    }

    /**
     * Test of getDescription method, of class Asset.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        BasicAsset instance = new BasicAsset();
        String expResult = "Euro - European currency";
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);

    }

    /**
     * Test of setDescription method, of class Asset.
     */
    @Test
    public void testSetDescription() {
        System.out.println("setDescription");
        String description = "Eruo - Eruopean currency";
        BasicAsset instance = new BasicAsset();
        instance.setDescription(description);
        String result = instance.getDescription();
        assertEquals(description, result);

    }

    /**
     * Test of getDecimals method, of class Asset.
     */
    @Test
    public void testGetDecimals() {
        System.out.println("getDecimals");
        BasicAsset instance = new BasicAsset();
        int expResult = 7;
        instance.setDecimals(expResult);
        int result = instance.getDecimals();
        assertEquals(expResult, result);

    }

    /**
     * Test of setDecimals method, of class Asset.
     */
    @Test
    public void testSetDecimals() {
        System.out.println("setDecimals");
        int decimals;
        decimals = 350;
        BasicAsset instance = new BasicAsset();
        instance.setDecimals(decimals);
        int result = instance.getDecimals();
        assertEquals(decimals, result);

    }

}
