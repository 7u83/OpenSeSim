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
package opensesim.world;

import opensesim.util.idgenerator.IDGenerator;
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
public class OrderTest {

    public OrderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    World world;

    @Before
    public void setUp() {
        world = new World(new JSONObject("{}"));
    }

    @After
    public void tearDown() {
    }

  
    /**
     * Test of getVolume method, of class Order.
     */
    @Test
    public void testGetVolume() {
        System.out.println("getVolume");

        double expResult = 13.7;
        Order instance = new opensesim.world.Order(world, null, null, Order.Type.BUY, expResult, 0.0);
        double result = instance.getVolume();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getLimit method, of class Order.
     */
    @Test
    public void testGetLimit() {
        System.out.println("getLimit");
        Order instance = new opensesim.world.Order(world, null, null, Order.Type.BUY, 0, 17.4);
        double expResult = 0.0;
        double result = instance.getLimit();
        assertEquals(expResult, result, 17.4);
        // TODO review the generated test code and remove the default call to // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class Order.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Order.Type expResult = Order.Type.BUY;
        Order instance = new opensesim.world.Order(world, null, null, expResult, 0, 17.4);        
        Order.Type result = instance.getType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to // fail.
        // fail("The test case is a prototype.");
    }




 


}
