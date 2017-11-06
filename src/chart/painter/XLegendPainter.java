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
package chart.painter;

import chart.ChartDef;
import chart.ChartPanel;
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
public class XLegendPainter extends ChartPainter {

    private String getTimeStrAt(OHLCData data, int unit) {

        int fs = data.getFrameSize();
        return sesim.Scheduler.formatTimeMillis(0 + unit * fs);

    }
    
    int big_tick = 10;
    int y = 0;
    @Override
    public void drawChart(Graphics2D g,  ChartPanel p, ChartDef def)
    {
        OHLCData data = getData();
        if (data ==null)
            return;
        
        init(g);
        
 //       g.setColor(Color.black);
        Dimension size = p.getSize();

        //int bars = (int) (size.width / (def.x_unit_width * em_size));

        int bars = this.getBars(p, def);
        
        int first_bar = getFirstBar(p);
        
        int n;
        int x;
        for (n = first_bar, x = 0; x < size.width; x += em_size * def.x_unit_width) {
            if (n % big_tick == 1) {
                g.drawLine((int) x, y, (int) x, y + em_size);
                String text;
                text = getTimeStrAt(data, n);

                int swidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, (int) x - swidth / 2, y + em_size * 2);

            } else {
                g.drawLine((int) x, y, (int) x, y + em_size / 2);
            }

            if (n % big_tick == 0) {

            }

            n += 1;
        }

    }

}
