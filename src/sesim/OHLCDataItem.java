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

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class OHLCDataItem {

    public float open;
    public float high;
    public float low;
    public float close;
    public float volume;
    public long time;

    /**
     * Constructor for creating a new bar/item where the price has just started
     * (Open=High=Low=Close).
     *
     * @param time The timestamp for this data item in milliseconds
     * @param price The initial price, used for all OHLC values.
     * @param volume The initial trading volume.
     */
    public OHLCDataItem(long time, float price, float volume) {
        // Calls the fully-parameterized constructor
        this(time, price, price, price, price, volume);
    }

    /**
     * Fully-parameterized constructor to set all OHLC and volume values.
     *
     * @param time The timestamp for this data item in milliseconds
     * @param open The opening price.
     * @param high The highest price reached.
     * @param low The lowest price reached.
     * @param close The closing price.
     * @param volume The total trading volume.
     */
    public OHLCDataItem(long time, float open, float high, float low, float close, float volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.close = close;
        this.low = low;
        this.volume = volume;
    }

    /**
     * Updates the current data item with a new price and volume tick. This
     * adjusts high/low and updates the close price and total volume.
     *
     * @param price The latest price tick.
     * @param volume The volume of the latest trade.
     * @return true if the high or low price was updated, false otherwise.
     */
    public boolean update(float price, float volume) {
        boolean ret = false;
        if (price > high) {
            high = price;
            ret = true;

        }
        if (price < low) {
            low = price;
            ret = true;
        }
        this.volume = this.volume + volume;
        this.close = price;
        return ret;
    }

    /**
     * Calculates the average of the open, high, low, and close prices.
     *
     * @return The simple average price for the period.
     */
    public float getAverage() {
        return (open + high + low + close) / 4;
    }

}
