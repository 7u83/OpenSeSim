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

import opensesim.chart.Chart;
import java.awt.Color;
import opensesim.old_sesim.ChartDef;
import opensesim.old_sesim.ChartPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import opensesim.old_sesim.MinMax;
import opensesim.old_sesim.OHLCData;
import opensesim.old_sesim.OHLCDataItem;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class YLegendPainter extends OHLCChartPainter {

    ChartPanel master;

    public YLegendPainter(/*ChartPanel master*/) {
        //    this.master=master;
    }

    @Override
    public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {
        init(g);
        this.master = def.mainChart;

        OHLCData da = getData();

        Dimension dim = def.mainChart.getSize();
        int first_bar = getFirstBar(master);
        int last_bar = first_bar + getBars(master, def);
        MinMax minmax = this.getData().getMinMax(first_bar, last_bar);

        this.initGetY(minmax, dim);

        // calculate the number of captionable bars
        int ldist = em_size * 2;
        int steps = y_height / ldist;

        // distance between bars
        float stepsize = (y_max - y_min) / steps;

        // round stepsize to power of 10
        float stepsize10 = (float) Math.pow(10, Math.ceil(Math.log10(stepsize)));

        // build inverse of stepsize
        float stepsize10i = 1 / stepsize10;

        // calculate the first y value
        float firstyv = (float) Math.ceil(y_min * stepsize10i) / stepsize10i;

        float y1 = getY(minmax.getMin(false));
        float y2 = getY(minmax.getMax(false));

        int c_font_height = g.getFontMetrics().getHeight();

        float lastyv = firstyv-stepsize10;
        for (float yv = firstyv; yv < minmax.getMax(false); yv += stepsize10) {
            float y;

            float ministep = stepsize10/10.0f;
            
            
            for (float yv10 = lastyv+ministep; yv10 < yv; yv10 += ministep) {
                y = this.getY(yv10);
                Color oc = g.getColor();
                g.setColor(Color.RED);
                g.drawLine(0, (int) y, em_size/2, (int) y);
                g.setColor(oc);
                
            }

            y = this.getY(yv);

            g.drawLine(0, (int) y, em_size, (int) y);
            g.drawString(String.format("%.2f", yv), em_size * 1.5f, y + c_font_height / 3);
            lastyv = yv;
        }
 
    }

    @Override
    void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        return;
    }

    public void drawChart_old(Graphics2D g, ChartPanel p, ChartDef def) {
        init(g);
        this.master = def.mainChart;

        OHLCData da = getData();

        Dimension dim = def.mainChart.getSize();
        int first_bar = getFirstBar(master);
        int last_bar = first_bar + getBars(master, def);
        MinMax minmax = this.getData().getMinMax(first_bar, last_bar);

        this.initGetY(minmax, dim);

        this.getRoundNumber(90);

        //Rectangle dim;
        // dim = p.getSize();
        //      dim = this.clip_bounds;
        // Dimension rv = this.getSize();
//        int yw = (int) (this.yl_width * em_size);
//        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);
        float y1 = getY(minmax.getMin(false));
        float y2 = getY(minmax.getMax(false));
        float ydiff = y1 - y2;
//        System.out.printf("%s y1: %f, y2: %f, diff %f\n", Boolean.toString(c_mm.isLog()), y1, y2, ydiff);
        int c_font_height = g.getFontMetrics().getHeight();

        for (int yp = (int) y2; yp < y1; yp += em_size * 3) {
            g.drawLine(0, yp, em_size, yp);
            double v1 = getValAtY(yp);
            g.drawString(String.format("%.2f", v1), em_size * 1.5f, yp + c_font_height / 3);
        }

        double v1, v2;
//        v1 = ctx.getValAtY(y1);
//        v2 = ctx.getValAtY(y2);

    }

}
