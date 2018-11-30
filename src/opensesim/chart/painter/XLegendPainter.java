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
package opensesim.chart.painter;

import opensesim.old_sesim.ChartDef;
import opensesim.old_sesim.ChartPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import opensesim.old_sesim.OHLCData;
import opensesim.old_sesim.OHLCDataItem;

/**
 * Paints an x-legend for OHLC charts
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class XLegendPainter extends OHLCChartPainter {

    private String getTimeStrAt(OHLCData data, int unit) {

        int fs = data.getFrameSize();
        return opensesim.old_sesim.Scheduler.formatTimeMillis(0 + (long) unit * (long) fs);

    }

    @Override
    public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {

        init(g);
        int caption_tick = 10;
        int long_tick = 5;

        Dimension size = p.getSize();

        int first_bar = getFirstBar(p);

        double ticksize = em_size * def.x_unit_width;
        String text = getTimeStrAt(data, first_bar);
        int swidth = g.getFontMetrics().stringWidth(text);

        caption_tick = swidth * 2 / ((int) ticksize);
        caption_tick = (caption_tick / 5) * 5 + 5;

        int n;
        double x;
        int y = 0;

        for (n = first_bar, x = 0; x < size.width+em_size*3; x += ticksize) {

            if (n % long_tick == 0) {
                g.drawLine((int) x, y, (int) x, y + em_size);
            } else {
                g.drawLine((int) x, y, (int) x, y + em_size / 2);
            }

            if (n % caption_tick == 0) {

                text = getTimeStrAt(data, n);

                swidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, (int) x - swidth / 2, y + em_size * 3);

            }
            n += 1;
        }

    }

    @Override
    void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
    }

}
