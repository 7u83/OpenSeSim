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

    private long max = 0;
    private long min = 0;

    private int barDuration = 60000;
 //   int max_size = 100;
    Exchange se;

    /*public OHLCData() {

    }*/

    /**
     * Create an OHLCData object that stores OHLCDataItems
     *
     * @param se
     * @param timeFrameLength Time frame stored in one OHLCDataItem
     */
    public OHLCData(Exchange se, int timeFrameLength) {
        this.se=se;
        this.barDuration = timeFrameLength;
        //data = new OHLCData(se, timeFrameLength);
    }
    
    public OHLCData(Exchange se, int barDuration, OHLCData base){
        this(se,barDuration);
        
        for (OHLCDataItem i: base.data){
            this.realTimeAdd(i.time, i.open,i.volume);
            this.realTimeAdd(i.time, i.high,0);
            this.realTimeAdd(i.time, i.low,0);
            this.realTimeAdd(i.time, i.close,0);            
        }
    }

    public float getMax() {
        return max/se.money_df;
    }

    public float getMin() {
        return min/se.money_df;
    }

    public int size() {
        return data.size();
    }

    /**
     *
     * @return Time frame of OHLCDataItem
     */
    public int getFrameSize() {
        return this.barDuration;
    }

    /**
     * Get the minimum and maximum value between two OHLCDataItems
     *
     * @param first Position of first OHLCDataItem
     * @param last Position of last OHCLDataItem
     * @return MinMax object containing the calculated values
     */
    public MinMax getMinMax(int first, int last) {

        if (first >= data.size()) {
            OHLCDataItem di = data.get(data.size() - 1);
            return new MinMax(se.money_df, di.low, di.high);
        }

        OHLCDataItem di = data.get(first);
        MinMax minmax = new MinMax(se.money_df, di.low, di.high);

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
            return new MinMax(se.shares_df, di.volume, di.volume);
        }

        OHLCDataItem di = data.get(first);
        MinMax minmax = new MinMax(se.shares_df, di.volume, di.volume);

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

 /*   long getFrameStart(long time) {

        long rt = time / frame_size;
        return rt * frame_size;

    }*/

    public ArrayList<OHLCDataItem> data = new ArrayList<>();

    public OHLCDataItem get(int n) {
        return data.get(n);
    }

    private void updateMinMax(long price) {
        if (price > max) {

            max = price;
        }
        if (price < min) {
            min = price;
        }

    }

  /*  public Iterator<OHLCDataItem> iterator() {
        return data.iterator();
    }*/

    // Start and end of current frame
 //   private long current_frame_end = 0;
//    private long current_frame_start = 0;
    private long last_price = 0;

     boolean realTimeAdd(long time, long price, long volume) {

       // System.out.printf("REALTIME ADD QUOTE time: %d, vol:%f\n",time,volume);

        if (data.isEmpty()) {
            //System.out.printf("Data ist empty\n");
            if (time < barDuration) {
                //System.out.printf("Adding Qute to frame 0\n");
                data.add(new OHLCDataItem(se,0, price, volume));
                last_price = price;
                return true;
            }
        }

        long nFrame = (long)data.size() * (long)barDuration;
        //System.out.printf("nFrame is: %d, data.size(): %d\n", nFrame, data.size());
        if (time < nFrame) {
            last_price = price;
            this.updateMinMax(price);

            OHLCDataItem d = data.get(data.size() - 1);
            //System.out.printf("Regular update at data.size()-1: %d, %d\n", data.size()-1, d.time);
            return d.update(price, volume);

        }

        while (time > nFrame+barDuration ) {

            data.add(new OHLCDataItem(se, nFrame, last_price, 0));
          //  System.out.printf("Add empty frame %d\n", nFrame);            
            nFrame += barDuration;
        }

        //System.out.printf("Add a new Frame %d\n", nFrame);
        data.add(new OHLCDataItem(se, nFrame, price, volume));
        last_price = price;
        return true;

        /*    if (time >= current_frame_end) {
            if (current_frame_end == 0) {

                this.min = price;
                this.max = price;
            }

            //        long last_frame_start = current_frame_start;
            long next_frame = getFrameStart(time);

            //       current_frame_start += frame_size;
            while (current_frame_start < next_frame) {
                System.out.printf("Adding FRAME %d\n", current_frame_start);
                data.add(new OHLCDataItem(current_frame_start, last_price, 0));
                current_frame_start += frame_size;
            }
            current_frame_start = next_frame; //getFrameStart(time);

            current_frame_end = current_frame_start + frame_size;

            //System.out.printf("TA %d TE %d\n",this.current_frame_start,this.current_frame_end);
            data.add(new OHLCDataItem(this.current_frame_start, price, volume));
            this.updateMinMax(price);
            last_price = price;
            return true;
        }
        last_price = price;
        this.updateMinMax(price);

        OHLCDataItem d = data.get(data.size() - 1);
        boolean rc = d.update(price, volume);
        return rc;
         */
    }
}
