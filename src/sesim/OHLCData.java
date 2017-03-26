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

import java.util.*;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class OHLCData {

    private float max = 0;
    private float min = 0;

    private int frame_size = 60000;
    int max_size = 100;

    public OHLCData() {

    }

    /**
     * Create an OHLCData object that stores OHLCDataItems
     * 
     * @param frame_size Time frame stored in one OHLCDataItem
     */
    public OHLCData(int frame_size) {

        this.frame_size = frame_size;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public int size() {
        return data.size();
    }

    /**
     * 
     * @return Time frame of OHLCDataItem
     */
    public int getFrameSize() {
        return this.frame_size;
    }

    /**
     * Get the minimum and maximum value between two OHLCDataItems
     * @param first Position of first OHLCDataItem
     * @param last Position of last OHCLDataItem
     * @return MinMax object containing the calculated values
     */
    public MinMax getMinMax(int first, int last) {

        if (first >= data.size()) {
            OHLCDataItem di = data.get(data.size() - 1);
            return new MinMax(di.low, di.high);
        }

        OHLCDataItem di = data.get(first);
        MinMax minmax = new MinMax(di.low, di.high);

        for (int i = first + 1; i < last && i < data.size(); i++) {
            di = data.get(i);
            if (di.low < minmax.min) {
                minmax.min = di.low;
            }
            if (di.high > minmax.max) {
                minmax.max = di.high;
            }
        }
        return minmax;
    }

    public MinMax getVolMinMax(int first, int last) {

        if (first >= data.size()) {
            OHLCDataItem di = data.get(data.size() - 1);
            return new MinMax(di.volume, di.volume);
        }

        OHLCDataItem di = data.get(first);
        MinMax minmax = new MinMax(di.volume, di.volume);

        for (int i = first + 1; i < last && i < data.size(); i++) {
            di = data.get(i);
            if (di.volume < minmax.min) {
                minmax.min = di.volume;
            }
            if (di.volume > minmax.max) {
                minmax.max = di.volume;
            }
        }
        return minmax;
    }

    long getFrameStart(long time) {

        long rt = time / frame_size;
        return rt * frame_size;

    }

    public ArrayList<OHLCDataItem> data = new ArrayList<>();

    public OHLCDataItem get(int n) {
        return data.get(n);
    }

    private void updateMinMax(float price) {
        if (price > max) {

            max = price;
        }
        if (price < min) {
            min = price;
        }

    }

    public Iterator<OHLCDataItem> iterator() {
        return data.iterator();
    }

    // Start and end of current frame
    private long current_frame_end = 0;
    private long current_frame_start = 0;

    public boolean realTimeAdd(long time, float price, float volume) {

        if (time >= current_frame_end) {
            if (current_frame_end == 0) {

                this.min = price;
                this.max = price;
            }

            long last_frame_start = current_frame_start;
            current_frame_start = getFrameStart(time);

            current_frame_end = current_frame_start + frame_size;

            //System.out.printf("TA %d TE %d\n",this.current_frame_start,this.current_frame_end);
            data.add(new OHLCDataItem(this.current_frame_start, price, volume));
            this.updateMinMax(price);
            return true;
        }

        this.updateMinMax(price);

        OHLCDataItem d = data.get(data.size() - 1);
        boolean rc = d.update(price, volume);
        return rc;
    }
}
