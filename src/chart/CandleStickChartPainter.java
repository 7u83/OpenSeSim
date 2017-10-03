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

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JScrollBar;
import sesim.OHLCData;
import sesim.OHLCDataItem;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class CandleStickChartPainter extends OHLCChartPainter {

    @Override
    protected void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        if (i.open < i.close) {
            int xl = (int) (x + iwidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) getY(i.close), xl, (int) getY(i.high));
            g.drawLine(xl, (int) getY(i.low), xl, (int) getY(i.open));

            float w = iwidth;
            float h = (int) (getY(i.open) - getY(i.close));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) getY(i.close), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) getY(i.close), (int) w, (int) h);

        } else {
            int xl = (int) (x + iwidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) getY(i.high), xl, (int) getY(i.close));
            g.drawLine(xl, (int) getY(i.open), xl, (int) getY(i.low));

            float w = iwidth;
            float h = (int) (getY(i.close) - getY(i.open));

            g.fillRect((int) (x), (int) getY(i.open), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) getY(i.open), (int) w, (int) h);
        }
    }



 
}
