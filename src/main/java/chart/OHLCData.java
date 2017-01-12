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
package chart;

import java.util.*;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class OHLCData { //extends ArrayList <OHLCDataItem> {

    float max=0;
    float min=0;

    long time_start;
    long time_step;

    public float getMax() {
        return max;
    }
    
    

    long rasterTime(long time) {

        long rt = time / 5000;
        return rt * 5000;

    }

    ArrayList<OHLCDataItem> data = new ArrayList<>();
    
    
    private void updateMinMax(float price){
        if (price > max){
            
            max = price;
        }
        if (price < min){
            min = price;
        }

    }

    private long ntime = 0;

    boolean realTimeAdd(long time, float price, float volume) {
        
        
        if (time > ntime) {
            if (ntime==0){
                System.out.print ("Setting ntimt was zero\n");
                this.min=price;
                this.max=price;
            }
            
            ntime = rasterTime(time) + 5000;
            data.add(new OHLCDataItem(price, price, price, price, volume));
            this.updateMinMax(price);
            return true;
        }

        OHLCDataItem d = data.get(data.size() - 1);
        this.updateMinMax(price);
        boolean rc = d.update(price, volume);
        return rc;
    }
}
