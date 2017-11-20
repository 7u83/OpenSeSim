/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
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

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class IndicatorLoader<T> extends SeSimClassLoader {

    ArrayList<Class<T>> cache;
    final Class<T> class_type;
            
            
    public IndicatorLoader(Class<T> class_type){
        this.class_type=class_type;
    }

    /**
     * Get a list of all traders found in class path
     *
     * @return List of traders
     */
    public ArrayList<Class<T>> getInstalled() {

        if (cache != null) {
            return cache;
        }

        Class<?> tube ;

        ArrayList<Class<?>> trl;
        ArrayList<Class<T>> result = new ArrayList<>();
        trl = getInstalledClasses(new ArrayList(), class_type);
        for (Class<?> c : trl) {
            result.add((Class<T>) c);
        }

        cache = result;
        return cache;

    }

}
