/*
 * Copyright (c) 2025, tube
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
 * @author tube
 */
public class Quote implements Comparable {
    Exchange se;
    long bid;
    long bid_volume;
    long ask;
    long ask_volume;

    long price;
    long volume;
    long time;
    //  public long id;

    public void print() {
        System.out.print("Quote ("
                + time
                + ") :"
                + price
                + " / "
                + volume
                + "\n"
        );

    }
    
    public Quote(Exchange se){
        this.se=se;
    }

    @Override
    public int compareTo(Object o) {
        int ret;
        Quote q = (Quote) o;

        ret = (int) (this.time - q.time);
        if (ret != 0) {
            return ret;
        }
        return 0;

    }

    public float getPrice() {
        return price/se.money_df;
    }

    public float getBid() {
        return bid/se.money_df;
    }

    public float getAsk() {
        return ask/se.money_df;
    }

    public float getVolume() {
        return volume/se.shares_df;
    }

}
