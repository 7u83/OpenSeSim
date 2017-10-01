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
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import sesim.OHLCData;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class XLegendChartPainter extends ChartPainter {

    private String getTimeStrAt(OHLCData data, int unit) {

        int fs = data.getFrameSize();
        return sesim.Scheduler.formatTimeMillis(0 + unit * fs);

    }

    public void drawChart(Graphics2D g, JScrollBar sb, OHLCData data, JPanel p, ChartDef def)
    {
        init(g);
        g.setColor(Color.black);
        Dimension size = p.getSize();
        //g.drawLine(0, 0, size.width, 100);

        System.out.printf("SIZE %d %d\n", size.width, size.height);

        int bars = (int) (size.width / (def.x_unit_width * em_width));
        System.out.printf("Units = %d\n", bars);

        int first_bar = def.x_scrollbar.getValue();
        //if (first_bar<0)
        //   first_bar=0;

        int n;
        int x;
        for (n = first_bar, x = 0; x < size.width; x += em_width * def.x_unit_width) {
            if (n % big_tick == 1) {
                g.drawLine((int) x, y, (int) x, y + em_width);
                String text;
                text = getTimeStrAt(data, n);

                int swidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, (int) x - swidth / 2, y + em_width * 2);

            } else {
                g.drawLine((int) x, y, (int) x, y + em_width / 2);
            }

            if (n % big_tick == 0) {

            }

            n += 1;
        }

    }

}
