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
package opensesim.util.idgenerator;

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
public class IdTest {

    public IdTest() {
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
     * Test of equals method, of class Id.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Id<Long> o = new Id<>(17L);
        Id<Long> instance = new Id<>(7L);
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class Id.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Id instance = new Id(31L);
        int expResult = 0;
        int result = instance.hashCode();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Id.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Id instance = new Id<Long>(18L);
        String expResult = "18";
        String result = instance.toString();
        assertEquals(expResult, result);

    }

    /**
     * Test of compareTo method, of class Id.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Id o = new Id(17L);
        Id instance = new Id(13L);
        int expResult = -1;
        int result = instance.compareTo(o);

        o = new Id(172L);
        instance = new Id(193L);
        expResult = 1;
        result = instance.compareTo(o);
        assertEquals(expResult, result);
        
        o = new Id(11L);
        instance = new Id(11L);
        expResult = 0;
        result = instance.compareTo(o);
        assertEquals(expResult, result);        
        
    }

}
