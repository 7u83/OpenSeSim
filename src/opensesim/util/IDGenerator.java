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
package opensesim.util;

import java.util.Objects;



/**
 * Implementation of a simple ID generator to create uniqe IDs of type long
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class IDGenerator {
    
    
    public static class Id {
        final Long value;

        public Id(String id) {
            value =Long.parseLong(id);
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != Id.class)
                return false;
            return Objects.equals(value, ((Id)o).value);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public String toString() {
            return value.toString();
        }
        
        
    }

    private Long next_id;
    private Long start_id;

    /**
     * Initialize the ID generator
     *
     * @param start ID value to start with
     */
    public IDGenerator(String start) {
        start_id=Long.parseLong(start);
        reset();
    }

    /**
     * Initialize ID Generator with start ID = 0
     */
    public IDGenerator() {
        this("0");
    }
    
    /**
     * Reset the ID generator
     */
    public final void reset(){
        next_id = start_id;
    }

    /**
     * Get the next ID
     *
     * @return the next generated ID
     */
    public synchronized Id getNext() {
        return new Id((next_id++).toString());
    }
}